package jpabook.jpashopproject.service;

import jpabook.jpashopproject.domain.*;
import jpabook.jpashopproject.domain.item.Item;
import jpabook.jpashopproject.repository.ItemRepository;
import jpabook.jpashopproject.repository.MemberRepository;
import jpabook.jpashopproject.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
//        OrderItem orderItem1 = new OrderItem();
//        OrderItem orderItem2 = new OrderItem();
//        OrderItem orderItem3 = new OrderItem();
        /**
         * 누군가 createOrderItem을 사용하지 않고 생성자를 통해 orderItem을 생성한다면?
         * -> 유지보수하기 어려워짐
         * --> 생성자를 protected로 선언해 생성자 사용을 막아야 함
         */

        Order order = Order.createOrder(member, delivery, orderItem);

        orderRepository.save(order);    //orderItems, delivery -> CascadeType.ALL (영속성 전이)
        /**
         * 만약 delivery를 여러 엔티티에서 참조한다면??
         * -> CascadeType.ALL 사용하지 않는것이 좋음
         * -> 만약 order를 삭제하면 delivery가 전부 삭제될 수 있기 때문에
         */
        return order.getId();
    }
    // 취소
    @Transactional
    public void cancel(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        order.cancel();
    }

    // 검색
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllString(orderSearch);
    }
}
