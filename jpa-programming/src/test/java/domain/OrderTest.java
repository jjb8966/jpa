package domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    EntityManagerFactory emf;
    EntityManager em;
    EntityTransaction tx;

    @BeforeEach
    void before() {
        emf = Persistence.createEntityManagerFactory("jpa-programming");
        em = emf.createEntityManager();
        tx = em.getTransaction();
        tx.begin();
    }

    @AfterEach
    void after() {
        tx.commit();
        em.close();
        emf.close();
    }

    @Test
    @DisplayName("연관관계 메소드를 쓰지 않았을 경우 발생하는 문제점")
    void test() {
        Member member = new Member();
        member.setName("member");
        em.persist(member);

        Order order = new Order();
        order.setMember(member);
        em.persist(order);

        // DB 반영 전
        // order는 member를 제대로 참조하지만
        assertThat(order.getMember()).isEqualTo(member);
        // member는 order를 참조하지 못함
        assertThat(member.getOrders().contains(order)).isFalse();

        em.flush();
        em.clear();

        // DB 반영 후
        Order findOrder = em.find(Order.class, order.getId());
        Member findMember = em.find(Member.class, member.getId());

        // 양방향 참조가 제대로 이루어짐
        assertThat(findOrder.getMember()).isEqualTo(findMember);
        assertThat(findMember.getOrders().contains(findOrder)).isTrue();
    }

    @Test
    @DisplayName("연관관계 메소드를 사용한 경우")
    void use() {
        Member member = new Member();
        member.setName("member");
        em.persist(member);

        Order order = new Order();
        // 연관관계 메소드 사용
        order.changeMember(member);
        em.persist(order);

        // DB 반영 전
        // -> 참조가 제대로 이루어짐
        assertThat(order.getMember()).isEqualTo(member);
        assertThat(member.getOrders().contains(order)).isTrue();

        em.flush();
        em.clear();

        Order findOrder = em.find(Order.class, order.getId());
        Member findMember = em.find(Member.class, member.getId());

        assertThat(findOrder.getMember()).isEqualTo(findMember);
        assertThat(findMember.getOrders().contains(findOrder)).isTrue();
    }
}