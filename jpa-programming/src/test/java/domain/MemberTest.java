package domain;

import domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class MemberTest {

    EntityManagerFactory emf;
    EntityManager em;
    EntityTransaction tx;

    @BeforeEach
    void before() {
        emf = Persistence.createEntityManagerFactory("jpa-programming");
        em = emf.createEntityManager();
        tx = em.getTransaction();
    }

    @AfterEach
    void after() {
        em.close();
        emf.close();
    }

    @Test
    void test() {
        Member member = new Member();
        member.setName("memberA");
        em.persist(member);
        Member findMember = em.find(Member.class, member.getId());

        assertThat(member).isEqualTo(findMember);
        assertThat(findMember.getName()).isEqualTo("memberA");
    }
}
