package study.querydsl.dto;

import lombok.ToString;

// 필드 접근으로 조회할 dto
@ToString
public class UserDto {

    private String name;
    private Integer maxAge;
}
