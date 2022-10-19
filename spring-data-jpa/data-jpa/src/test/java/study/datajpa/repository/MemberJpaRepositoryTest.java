package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("MemberA");

        Member saveMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(saveMember.getId());

        assertThat(saveMember).isEqualTo(findMember);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);
        long deleteCount = memberJpaRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("bbb", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("bbb");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    // 거의 안씀
    public void testNamedQuery() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsername("aaa");
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void paging() {
        memberJpaRepository.save(new Member("member1", 5));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 8));
        memberJpaRepository.save(new Member("member4", 7));
        memberJpaRepository.save(new Member("member5", 12));
        memberJpaRepository.save(new Member("member6", 4));
        memberJpaRepository.save(new Member("member7", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(10);

        for (Member member : members) {
            System.out.println("member = " + member);
        }

        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(2);
    }

    @Test
    public void bulkUpdate() {
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 15));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 25));
        memberJpaRepository.save(new Member("member5", 30));

        // update 쿼리를 날리면(JPQL) update 전에 영속성 컨텍스트가 flush 하여 만들어둔 멤버를 DB에 저장함
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        assertThat(resultCount).isEqualTo(3);

        // 벌크성 쿼리를 날리는 경우 DB에 직접 쿼리를 날리는 것이기 때문에 영속성 컨텍스트가 변화를 인지하지 못함
        List<Member> result1 = memberJpaRepository.findByUsername("member3");
        Member findMember1 = result1.get(0);

        assertThat(findMember1.getAge()).isEqualTo(20);

        // 영속성 컨텍스트를 초기화하여 변화된 DB의 정보를 다시 읽어와야 함!
        em.clear();     // 영속성 컨텍스트 초기화

        // DB에 쿼리를 날려 다시 읽어옴
        // 초기화하지 않으면 영속성 컨텍스트의 캐시 정보를 읽기 때문에 변화된 데이터를 읽을 수 없음
        List<Member> result2 = memberJpaRepository.findByUsername("member3");
        Member findMember2 = result2.get(0);

        assertThat(findMember2.getAge()).isEqualTo(21);
    }
}