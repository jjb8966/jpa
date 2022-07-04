package jpabook.jpashop.chapter10.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ORDERS")
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    private Integer orderAmount;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Embedded
    private Address address;
}
