package jpabook.jpashop.chapter10.projection;

import lombok.Getter;
import lombok.Setter;

// 스칼라 타입을 조회 시 생성할 클래스 타입
@Getter @Setter
public class MemberDTO {

    private String username;
    private Integer age;

    public MemberDTO(String username, Integer age) {
        this.username = username;
        this.age = age;
    }
}
