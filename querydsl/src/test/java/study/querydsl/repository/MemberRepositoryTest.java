package study.querydsl.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired      // Querydsl 동적 쿼리를 별도로 분리한 Repository
    MemberQueryRepository memberQueryRepository;

    @Test
    void 스프링데이터_JPA() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);

        List<Member> result1 = memberRepository.findAll();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberRepository.findByUsername("member1");
        assertThat(result2)
                .extracting("username")
                .containsExactly("member1");
    }

    @Test
    void 동적쿼리_where_with_querydsl() {
        init();

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setUsername("member3");
        condition.setAgeGoe(25);
        condition.setAgeLoe(35);

        List<MemberTeamDto> result = memberRepository.search(condition);

        assertThat(result)
                .extracting("username")
                .containsExactly("member3");
    }

    @Test
    void 동적쿼리_별도의_repository_분리() {
        init();

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setUsername("member3");
        condition.setAgeGoe(25);
        condition.setAgeLoe(35);

        List<MemberTeamDto> result = memberQueryRepository.search(condition);

        assertThat(result)
                .extracting("username")
                .containsExactly("member3");
    }

    @Test
    void querydsl_페이징_simple() {
        init();

        MemberSearchCondition condition = new MemberSearchCondition();
        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<MemberTeamDto> result1 = memberRepository.searchPageSimple(condition, pageRequest);
        Page<MemberTeamDto> result2 = memberRepository.searchPageComplex(condition, pageRequest);

        assertThat(result1.getSize()).isEqualTo(3);
        assertThat(result1.getContent())
                .extracting("username")
                .containsExactly("member1", "member2", "member3");

        assertThat(result2.getSize()).isEqualTo(3);
        assertThat(result2.getContent())
                .extracting("username")
                .containsExactly("member1", "member2", "member3");
    }

    private void init() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }
}