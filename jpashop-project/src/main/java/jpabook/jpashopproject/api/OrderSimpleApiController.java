package jpabook.jpashopproject.api;

import jpabook.jpashopproject.domain.*;
import jpabook.jpashopproject.repository.OrderRepository;
import jpabook.jpashopproject.repository.order.simplequery.OrderSimpleQueryRepository;
import jpabook.jpashopproject.repository.order.simplequery.OrderSimpleQueryDto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("api/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAllString(new OrderSearch());

        for (Order order : all) {
            // lazy 강제 초기화
            order.getMember().getName();
            order.getDelivery().getAddress();
        }

        return all;
    }

    @GetMapping("api/v2/simple-orders")
    public List<OrderSimpleQueryDto> ordersV2() {
        // Order 2개 조회
        // 각각 member, delivery 조회 쿼리가 한번씩 나감
        // -> 1 + 2 + 2 (1 + N 문제)
        List<Order> all = orderRepository.findAllString(new OrderSearch());

        List<OrderSimpleQueryDto> result = all.stream()
                .map(o -> new OrderSimpleQueryDto(o))
                .collect(Collectors.toList());

        return result;
    }

    // 권장
    @GetMapping("api/v3/simple-orders")
    public List<OrderSimpleQueryDto> ordersV3() {
        List<Order> all = orderRepository.findAllWithMemberDelivery();

        List<OrderSimpleQueryDto> result = all.stream()
                .map(o -> new OrderSimpleQueryDto(o))
                .collect(Collectors.toList());

        return result;
    }

    // 필요한 데이터만 골라서 쿼리 날림
    @GetMapping("api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }
}
