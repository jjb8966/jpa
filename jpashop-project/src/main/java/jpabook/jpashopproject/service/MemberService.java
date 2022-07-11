package jpabook.jpashopproject.service;

import jpabook.jpashopproject.domain.Member;
import jpabook.jpashopproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 가입
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);

        return member.getId();

    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());

        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    // 회원 조회
    // 데이터 조회용 메소드에는 (readOnly = true) 옵션을 추가
    // -> 읽기 전용 트랜잭션으로 인식하여 성능 최적화
    // 조회용 메소드가 더 많은 경우 클래스 레벨에서 선언 후 데이터 변경이 필요한 메소드에 @Transactional 추가 선언
//    @Transactional(readOnly = true)
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

//    @Transactional(readOnly = true)
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
