package jpabook.jpashopproject.service;

import jpabook.jpashopproject.domain.Member;
import jpabook.jpashopproject.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @PersistenceContext EntityManager em;

    /**
     * test 클래스에 Transcational을 적용했으므로 em.commit이 실행되지 않음
     * 영속성 컨텍스트의 변화 쿼리를 DB에 날리지 않음
     * 2가지 방법으로 insert 쿼리를 확인할 수 있음
     */

    @Test
    // 방법 1. 롤백 x -> DB에 데이터가 남음
    // @Rollback(false)
    void 회원가입() {
        // given
        Member member = new Member();
        member.setName("kim");

        // when
        Long savedId = memberService.join(member);

        // 방법 2. 영속성 컨텍스트 강제 flush -> 쿼리는 나가지만 마지막에 롤백함 (DB에 데이터 x)
         em.flush();

        // then
        Assertions.assertThat(savedId).isEqualTo(member.getId());
    }

    void 중복_회원_예외() {
        // given
        Member member1 = new Member();
        Member member2 = new Member();
        member1.setName("kim");
        member2.setName("kim");

        // when
        memberService.join(member1);

        // then
        Assertions.assertThatThrownBy(() -> memberService.join(member2))
                .isInstanceOf(IllegalStateException.class);
    }
}