package jpabook.jpashop.chapter9.embedded_type;

import lombok.Getter;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Getter
//@Setter -> setter 생성을 막아 외부에서 객체를 변경할 수 없도록 막음 (immutable)
public class AddressPra {

    private String city;
    private String street;
    private String zipcode;

    public AddressPra() {

    }

    public AddressPra(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressPra address = (AddressPra) o;
        return Objects.equals(city, address.city) && Objects.equals(street, address.street) && Objects.equals(zipcode, address.zipcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, street, zipcode);
    }
}
