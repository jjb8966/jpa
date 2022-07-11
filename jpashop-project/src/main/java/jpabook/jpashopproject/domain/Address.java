package jpabook.jpashopproject.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

/**
 * 값 타입은 불변 객체로 설계해야 안전함
 * JPA에서 리플렉션, 프록시같은 기능을 사용하려면 기본 생성자가 필요함
 * 기본 생성자를 public으로 설정하면 사용자가 임의로 객체를 생성할 수도 있음
 * -> setter를 막고 값 타입은 기본 생성자를 protected로 설정해서
 */
@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
