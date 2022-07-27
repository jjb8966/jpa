package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    @Autowired
    EntityManager em;

    @Test
    public void testMember() {
        // 프록시 객체
        System.out.println("memberRepository = " + memberRepository.getClass());

        Member member = new Member("memberA");

        Member savedMember = memberRepository.save(member);
        em.flush();

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("bbb", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("bbb");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    // 거의 안씀
    public void testNamedQuery() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("aaa");
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("aaa", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();

        for (String name : result) {
            System.out.println("name = " + name);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member = new Member("aaa", 10);
        member.setTeam(team);
        memberRepository.save(member);

        List<MemberDto> result = memberRepository.findMemberDto();

        for (MemberDto memberDto : result) {
            System.out.println("dto = " + memberDto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("aaa", "bbb"));

        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // 정상 조회
        List<Member> result1 = memberRepository.findListByUsername("aaa");
        Member result2 = memberRepository.findMemberByUsername("aaa");
        Optional<Member> result3 = memberRepository.findOptionalByUsername("aaa");
        System.out.println("result1 = " + result1); // result1 = [Member(id=1, username=aaa, age=10)]
        System.out.println("result2 = " + result2); // result2 = Member(id=1, username=aaa, age=10)
        System.out.println("result3 = " + result3); // result3 = Optional[Member(id=1, username=aaa, age=10)]

        // 없는 데이터 조회
        // 컬렉션은 절대 null을 반환하지 않음!!
        List<Member> result4 = memberRepository.findListByUsername("xxx");
        /*
        안좋은 코드
        if (result4 != null) {
            // 로직
        }
        */

        /**
         * 단건 조회 시 null을 리턴함
         * -> 순수 JPA는 getSingleResult() 사용 시 조회 결과가 없으면 NoResultException 예외를 던짐
         * 예외 vs null 뭐가 더 좋은가??
         * -> 큰 의미 없음. Optional을 쓰는게 가장 좋음
         */
        Member result5 = memberRepository.findMemberByUsername("xxx");
        Optional<Member> result6 = memberRepository.findOptionalByUsername("xxx");
        System.out.println("result4 = " + result4); // result4 = []
        System.out.println("result5 = " + result5); // result5 = null
        System.out.println("result6 = " + result6); // result6 = Optional.empty

        /**
         * 만약 단건 조회 시 결과가 2개 이상이라면?
         * -> NonUniqueResultException 예외 발생
         * --> 스프링 데이 JPA가 스프링 예외로 변환해서 던짐 (IncorrectResultSizeDataAccessException)
         * DB가 변경되어도 클라이언트가 스프링 예외를 처리하도록 되어있다면 코드를 변경할 필요가 없음
         */
//        Member m3 = new Member("aaa", 30);
//        memberRepository.save(m3);
//        Optional<Member> result7 = memberRepository.findOptionalByUsername("aaa");
    }

    @Test
    public void paging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
//        int offset = 1;
//        int limit = 3;

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

//        List<Member> members = memberRepository.findByAge(age, offset, limit);
//        long totalCount = memberRepository.totalCount(10);

        // 리턴타입이 Page인 경우 totalCount도 자동으로 구해줌
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // 엔티티 -> DTO
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        List<Member> members = page.getContent();
        long totalCount = page.getTotalElements();

        for (Member member : members) {
            System.out.println("member = " + member);
        }

        System.out.println("totalCount = " + totalCount);

        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);

        assertThat(page.getNumber()).isEqualTo(0);      // 페이지 숫자
        assertThat(page.getTotalPages()).isEqualTo(2);  // 총 페이지 수
        assertThat(page.isFirst()).isTrue();                    // 첫번째 페이지인지
        assertThat(page.hasNext()).isTrue();                    // 다음 페이지가 있는지
    }

    @Test
    public void bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 15));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 25));
        memberRepository.save(new Member("member5", 30));

        int resultCount = memberRepository.bulkAgePlus(20);

        //em.clear(); -> @Modifying(clearAutomatically = true)

        List<Member> result = memberRepository.findByUsername("member3");
        Member member = result.get(0);
        System.out.println("member.getAge() = " + member.getAge());

        assertThat(resultCount).isEqualTo(3);
    }
}