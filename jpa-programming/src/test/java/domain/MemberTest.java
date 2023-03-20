package domain;

import dto.MemberDto;
import lombok.EqualsAndHashCode;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

        List resultList = em.createQuery("select m.name, m.age from Member m").getResultList();

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

        List<MemberDto> resultList = em.createQuery("select new dto.MemberDto(m.name, m.age) from Member m", MemberDto.class).getResultList();

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

        List<Member> findByPaging = em.createQuery("select m from Member m", Member.class).setFirstResult(4)  // 4개 건너뜀. member5부터
                .setMaxResults(5).getResultList();

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

        List resultList1 = em.createQuery("select m.age, t.name from Member m, Team t").getResultList();

        List resultList2 = em.createQuery("select m.age, t.name from Member m inner join m.team t").getResultList();

        List resultList3 = em.createQuery("select m.age, t.name from Member m left outer join m.team t").getResultList();

        List resultList4 = em.createQuery("select m.age, t.name from Member m join m.team t on m.age > 15").getResultList();

        List resultList5 = em.createQuery("select m.name, i.name from Member m join Item i on m.age = i.price").getResultList();

        Object[] objects = (Object[]) resultList1.get(0);

        System.out.println("objects[0] = " + objects[0]);
        System.out.println("objects[1] = " + objects[1]);
    }

    @Test
    void sub_query() {
        String subQuery = " (select avg(m2.age) from Member m2)";

        List<Member> resultList = em.createQuery("select m from Member m where m.age >" + subQuery, Member.class).getResultList();

        List<Member> resultList2 = em.createQuery("select m from Member m where exists" + subQuery, Member.class).getResultList();

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

        List resultList1 = em.createQuery("select case" + " when m.age > 60 then '경로 요금'" + " when m.age > 10 then '학생 요금'" + " else '일반 요금'" + " end" + " from Member m").getResultList();

        for (Object o : resultList1) {
            System.out.println("result = " + o);
        }

        List resultList2 = em.createQuery("select case m.name" + " when 'memberA' then '일반회원'" + " when 'memberB' then '우수회원'" + " else 'vip'" + " end" + " from Member m").getResultList();

        for (Object o : resultList2) {
            System.out.println("result2 = " + o);
        }
    }

    @Test
    void 묵시적_내부_조인() {
        Member member = new Member();
        member.setName("memberA");
        em.persist(member);

        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        List members = em.createQuery("select t.members from Team t").getResultList();

        for (Object o : members) {
            System.out.println("o = " + o);
        }
    }

    @Test
    void 페치_조인_사용x() {
        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        Member member = new Member();
        member.setName("memberA");
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        Member findMember = em.createQuery("select m from Member m", Member.class).getSingleResult();

        // team 프록시 조회
        Team findTeam = findMember.getTeam();
        System.out.println("findTeam.getClass() = " + findTeam.getClass());

        // team 프록시 사용 -> 실제 target 조회 (프록시 초기화)
        findTeam.getName();

        // 하이버네이트를 통한 프록시 강제 초기화
        //Hibernate.initialize(findTeam);
    }

    @Test
    void 페치_조인_사용o() {
        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        Member member = new Member();
        member.setName("memberA");
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        // member 조회 시 Team 테이블도 조인 후 team에 대한 모든 데이터도 같이 조회
        // 조회 결과는 Member만 나오지만 실제 쿼리는 Team에 대한 쿼리도 날린 상태
        Object findObject = em.createQuery("select m from Member m join fetch m.team").getSingleResult();

        // findObject.getClass() = class domain.Member
        System.out.println("findObject.getClass() = " + findObject.getClass());

        // 동일한 쿼리
        // 실제 조회 결과는 다름 (Member 타입으로 조회 못함)
        // Object findMemberAndTeam = em.createQuery("select m, t from Member m join m.team t")
        //        .getSingleResult();

        // 프록시가 아닌 실제 객체
        Member findMember = (Member) findObject;
        Team findTeam = findMember.getTeam();
        System.out.println("findTeam.getClass() = " + findTeam.getClass());

        // Team 테이블 조회 없이 바로 사용
        findTeam.getName();
    }

    @Test
    void 일반_조인_사용() {
        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        Member member = new Member();
        member.setName("memberA");
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        // 조인은 했지만 데이터 조회는 Member만 한 상태
        Member findMember = em.createQuery("select m from Member m join m.team t", Member.class).getSingleResult();

        // Team 데이터를 조회하지 않았으므로 프록시 객체
        Team findTeam = findMember.getTeam();
        System.out.println("findTeam.getClass() = " + findTeam.getClass());

        // 프록시 초기화
        findTeam.getName();
    }

    @Test
    @DisplayName("컬렉션 값 연관 필드는 페치 조인 후 페이징 처리할 수 없음")
    void 페치조인_한계() {
        for (int i = 0; i < 5; i++) {
            Team team = new Team();
            team.setName("team" + i);
            em.persist(team);

            for (int j = 0; j < 5; j++) {
                Member member = new Member();
                member.setName("member" + j);
                member.setTeam(team);
                em.persist(member);
            }
        }

        em.flush();
        em.clear();

        List<Team> teams = em.createQuery("select t from Team t join fetch t.members", Team.class).setFirstResult(0).setMaxResults(3).getResultList();

        for (Team team : teams) {
            System.out.println("team.getName() = " + team.getName());
        }
    }

    @Test
    void 다대일_조회로_페이징() {
        for (int i = 0; i < 5; i++) {
            Team team = new Team();
            team.setName("team" + i);
            em.persist(team);

            for (int j = 0; j < 5; j++) {
                Member member = new Member();
                member.setName("member" + j);
                member.setTeam(team);
                em.persist(member);
            }
        }

        em.flush();
        em.clear();

        //List<Team> teams = em.createQuery("select t from Team t join fetch t.members", Team.class)
        //        .setFirstResult(0)
        //        .setMaxResults(3)
        //        .getResultList();

        List<Member> members = em.createQuery("select m from Member m join fetch m.team", Member.class).setFirstResult(0).setMaxResults(15).getResultList();

        for (Member member : members) {
            System.out.println("member = " + member.getName() + ", team = " + member.getTeam().getName());
        }
    }

    @Test
    void 단일_엔티티_조회_후_프록시_초기화() {
        for (int i = 0; i < 5; i++) {
            Team team = new Team();
            team.setName("team" + i);
            em.persist(team);

            for (int j = 0; j < 5; j++) {
                Member member = new Member();
                member.setName("member" + j);
                member.setTeam(team);
                team.getMembers().add(member);
                em.persist(member);
            }
        }

        em.flush();
        em.clear();

        List<Team> teams = em.createQuery("select t from Team t", Team.class).setFirstResult(0).setMaxResults(3).getResultList();

        for (Team team : teams) {
            System.out.println("team.getName() = " + team.getName());
            Hibernate.initialize(team.getMembers());
        }
    }

    @Test
    void proxy_name() {
        Member member = new Member();
        member.setName("test");
        em.persist(member);

        em.flush();
        em.clear();

        Member reference = em.getReference(Member.class, member.getId());
        // 1. 프록시 강제 초기화
        // reference.getUsername(); -> 메소드 호출로 초기화
        Hibernate.initialize(reference);

        // 2. 프록시 인스턴스 초기화 여부
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(reference);
        System.out.println("isLoaded = " + loaded);

        // 3. 프록시 클래스 이름 얻기
        System.out.println("proxyName = " + reference.getClass().getName());
    }

    @Test
    @DisplayName("orphanRemoval = true -> 고아 객체 자동 삭제 하기")
    void orphanRemoval_true() {
        Member member = new Member();
        member.setName("memberA");
        em.persist(member);

        Order order1 = new Order();
        Order order2 = new Order();
        order1.setMember(member);
        order2.setMember(member);
        em.persist(order1);
        em.persist(order2);

        em.flush();
        em.clear();

        Member findMember = em.find(Member.class, member.getId());
        Order findOrder1 = em.find(Order.class, order1.getId());

        findMember.getOrders().remove(findOrder1);
        System.out.println("findMember.getOrders().size() = " + findMember.getOrders().size());

        em.flush();
        em.clear();

        List<Order> allOrders = em.createQuery("select o from Order o", Order.class)
                .getResultList();

        Member findMember2 = em.find(Member.class, member.getId());

        System.out.println("allOrders.size() = " + allOrders.size());
        System.out.println("findMember2.getOrders().size() = " + findMember2.getOrders().size());
    }

}
