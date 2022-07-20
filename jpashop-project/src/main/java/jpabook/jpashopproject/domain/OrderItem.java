package jpabook.jpashopproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashopproject.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;
    private int count;

    // jpa에서 protected -> 생성자 쓰지 마라!!!
//    protected OrderItem() {} -> 롬복 사용 가능

    // 생성 메소드
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStockQuantity(count);

        return orderItem;
    }

    // 비지니스 로직
    public void cancel() {
        getItem().addStockQuantity(count);
    }

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
