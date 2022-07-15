package jpabook.jpashopproject.service;

import jpabook.jpashopproject.domain.Address;
import jpabook.jpashopproject.domain.Member;
import jpabook.jpashopproject.domain.Order;
import jpabook.jpashopproject.domain.OrderStatus;
import jpabook.jpashopproject.domain.item.Book;
import jpabook.jpashopproject.exception.NotEnoughStockException;
import jpabook.jpashopproject.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() {
        Member member = createMember("회원1", new Address("서울", "강가", "123-123"));
        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문 시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 주문 * 수량이다.", 10000 * 2, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.", 8, book.getStockQuantity());
    }

    @Test
    public void 주문_재고수량_초과() {
        Member member = createMember("회원1", new Address("서울", "강가", "123-123"));
        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 11;

        assertThrows("재고량을 초과하여 주문하면 예외 발생", NotEnoughStockException.class,
                () -> orderService.order(member.getId(), book.getId(), orderCount));
    }

    @Test
    public void 주문취소() {
        Member member = createMember("회원1", new Address("서울", "강가", "123-123"));
        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        orderService.cancel(orderId);

        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 최소 시 상태는 CANCEL", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("재고가 취소된 수량만큼 늘어나야 한다.", 10, book.getStockQuantity());
    }

    private Member createMember(String name, Address address) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(address);
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int quantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(quantity);
        em.persist(book);

        return book;
    }
}