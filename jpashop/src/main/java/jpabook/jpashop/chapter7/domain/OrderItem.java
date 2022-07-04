package jpabook.jpashop.chapter7.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "orderItem_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Integer orderPrice;
    private Integer count;

}
