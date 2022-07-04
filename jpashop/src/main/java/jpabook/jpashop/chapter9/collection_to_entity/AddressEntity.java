package jpabook.jpashop.chapter9.collection_to_entity;

import jpabook.jpashop.chapter9.embedded_type.AddressPra;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity @Getter
@Table(name = "ADDRESS_ENTITY")
public class AddressEntity {

    @Id @GeneratedValue
    private Long id;

    private AddressPra address;

    public AddressEntity() {

    }

    public AddressEntity(String city, String street, String zipcode) {
        address = new AddressPra(city, street, zipcode);
    }
}
