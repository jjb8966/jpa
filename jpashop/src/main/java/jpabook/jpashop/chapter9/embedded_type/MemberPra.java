package jpabook.jpashop.chapter9.embedded_type;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class MemberPra {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;

    @Embedded
    private Period period;

    @Embedded
    private AddressPra homeAddress;

    @Embedded
    @AttributeOverrides({   // 잘 쓰진 않음
            @AttributeOverride(name = "city", column = @Column(name = "work_city")),
            @AttributeOverride(name = "street", column = @Column(name = "work_steet")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "work_zipcode"))
    })
    private AddressPra workAddress;
}
