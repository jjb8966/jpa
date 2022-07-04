package jpabook.jpashop.chapter4.domain;

import jpabook.jpashop.OrderStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ORDERS")
public class Order {

    // 객체 사이의 연관관계를 객체 자체가 아닌 id 값으로 맺음
    // 객체지향스럽지 않은 코드 -> v2에서 업그레이드
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
