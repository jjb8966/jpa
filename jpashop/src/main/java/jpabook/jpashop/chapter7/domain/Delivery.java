package jpabook.jpashop.chapter7.domain;

import jpabook.jpashop.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    private String city;
    private String street;
    private String zipcode;
    private DeliveryStatus status;
}
