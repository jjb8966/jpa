package domain;

import dto.MemberDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class MemberTest {

    EntityManagerFactory emf;
    EntityManager em;
    EntityTransaction tx;

    @BeforeEach
    void before() {
        emf = Persistence.createEntityManagerFactory("jpa-programming");
        em = emf.createEntityManager();
        tx = em.getTransaction();
        tx.begin();
    }

    @AfterEach
    void after() {
        tx.rollback();
        em.close();
        emf.close();
    }

    @Test
    void test() {
        Member member = new Member();
        member.setName("memberA");
        em.persist(member);
        Member findMember = em.find(Member.class, member.getId());
        assertThat(member).isEqualTo(findMember);
        assertThat(findMember.getName()).isEqualTo("memberA");
    }

    @Test
    void getRef() {
        Member member = new Member();
        member.setName("hi");
        em.persist(member);

        em.flush();
        em.clear();

        Member refMember = em.getReference(Member.class, member.getId());
        System.out.println("프록시 객체 : refMember.getClass() = " + refMember.getClass());
        System.out.println("refMember.getName() = " + refMember.getName());
        System.out.println("실제 엔티티 객체 : refMember.getClass() = " + refMember.getClass());
    }

    @Test
    @DisplayName("find() 메소드를 getReference() 메소드보다 먼저 호출한 경우 getReference의 결과도 실제 엔티티 객체이다.")
    void findFirst() throws ClassNotFoundException {
        // given
        Member member = new Member();
        member.setName("memberA");
        em.persist(member);

        em.flush();
        em.clear();

        //when
        Member memberByFind = em.find(Member.class, member.getId());
        Member memberByGetReference = em.getReference(Member.class, member.getId());

        System.out.println("memberByFind.getClass() = " + memberByFind.getClass());
        System.out.println("memberByGetReference.getClass() = " + memberByGetReference.getClass());

        //then
        assertThat(memberByGetReference.getClass()).isEqualTo(Class.forName("domain.Member"));
        assertThat(memberByFind).isEqualTo(memberByGetReference);
    }

    @Test
    @DisplayName("getReference() 메소드를 find() 메소드보다 먼저 호출한 경우 find의 결과도 프록시 객체이다.")
    void getRefFirst() throws ClassNotFoundException {
        // given
        Member member = new Member();
        member.setName("memberA");
        em.persist(member);

        em.flush();
        em.clear();

        //when
        Member memberByGetReference = em.getReference(Member.class, member.getId());
        Member memberByFind = em.find(Member.class, member.getId());

        System.out.println("memberByGetReference.getClass() = " + memberByGetReference.getClass());
        System.out.println("memberByFind.getClass() = " + memberByFind.getClass());

//        emf.getPersistenceUnitUtil().isLoaded();

        //then
        assertThat(memberByFind.getClass()).isNotEqualTo(Class.forName("domain.Member"));
        assertThat(memberByFind).isInstanceOf(Class.forName("domain.Member"));
        assertThat(memberByGetReference).isEqualTo(memberByFind);
    }

    @Test
    void select_object_array() {
        Member member = new Member();
        member.setName("memberA");
        member.setAge(10);

        em.persist(member);

        List resultList = em.createQuery("select m.name, m.age from Member m")
                .getResultList();

        Object o = resultList.get(0);
        Object[] result = (Object[]) o;

        assertThat(result[0]).isEqualTo("memberA");
        assertThat(result[1]).isEqualTo(10);
    }

    @Test
    void select_dto() {
        Member member = new Member();
        member.setName("memberA");
        member.setAge(10);

        em.persist(member);

        List<MemberDto> resultList = em.createQuery("select new dto.MemberDto(m.name, m.age) from Member m", MemberDto.class)
                .getResultList();

        MemberDto memberDto = resultList.get(0);

        assertThat(memberDto.getName()).isEqualTo("memberA");
        assertThat(memberDto.getAge()).isEqualTo(10);
    }

    @Test
    void paging() {
        for (int i = 1; i <= 100; i++) {
            Member member = new Member();
            member.setName("member" + i);
            em.persist(member);
        }

        List<Member> findByPaging = em.createQuery("select m from Member m", Member.class)
                .setFirstResult(4)  // 4개 건너뜀. member5부터
                .setMaxResults(5)
                .getResultList();

        for (Member member : findByPaging) {
            System.out.println("member = " + member.getName());
        }

        assertThat(findByPaging.size()).isEqualTo(5);
    }

    @Test
    void test3() {
        Member member1 = new Member();
        Member member2 = new Member();
        member1.setAge(10);
        member2.setAge(20);
        Team team = new Team();
        team.setName("teamA");

        member1.setTeam(team);

        em.persist(member1);

        List resultList1 = em.createQuery("select m.age, t.name from Member m, Team t")
                .getResultList();

        List resultList2 = em.createQuery("select m.age, t.name from Member m inner join m.team t")
                .getResultList();

        List resultList3 = em.createQuery("select m.age, t.name from Member m left outer join m.team t")
                .getResultList();

        List resultList4 = em.createQuery("select m.age, t.name from Member m join m.team t on m.age > 15")
                .getResultList();

        List resultList5 = em.createQuery("select m.name, i.name from Member m join Item i on m.age = i.price")
                .getResultList();

        Object[] objects = (Object[]) resultList1.get(0);

        System.out.println("objects[0] = " + objects[0]);
        System.out.println("objects[1] = " + objects[1]);
    }

    @Test
    void sub_query() {
        String subQuery = " (select avg(m2.age) from Member m2)";

        List<Member> resultList = em.createQuery("select m from Member m where m.age >" + subQuery, Member.class)
                .getResultList();

        List<Member> resultList2 = em.createQuery("select m from Member m where exists" + subQuery, Member.class)
                .getResultList();

    }

    @Test
    void case_조건식() {
        Member member1 = new Member();
        Member member2 = new Member();
        Member member3 = new Member();
        member1.setAge(20);
        member1.setName("memberA");
        member2.setAge(70);
        member2.setName("memberB");
        member3.setAge(5);
        member3.setName("memberC");
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);

        List resultList1 = em.createQuery("select case" +
                        " when m.age > 60 then '경로 요금'" +
                        " when m.age > 10 then '학생 요금'" +
                        " else '일반 요금'" +
                        " end" +
                        " from Member m")
                .getResultList();

        for (Object o : resultList1) {
            System.out.println("result = " + o);
        }

        List resultList2 = em.createQuery("select case m.name" +
                        " when 'memberA' then '일반회원'" +
                        " when 'memberB' then '우수회원'" +
                        " else 'vip'" +
                        " end" +
                        " from Member m")
                .getResultList();

        for (Object o : resultList2) {
            System.out.println("result2 = " + o);
        }
    }

}
