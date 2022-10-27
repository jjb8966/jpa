package dto;

import lombok.Getter;

@Getter
public class MemberDto {
    private String name;
    private Integer age;

    public MemberDto(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
