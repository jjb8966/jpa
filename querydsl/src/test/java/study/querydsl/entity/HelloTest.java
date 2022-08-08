package study.querydsl.entity;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class HelloTest {

    @Autowired
    EntityManager em;

    @Test
    void test() {
        Hello hello = new Hello();
        em.persist(hello);

        JPAQueryFactory query = new JPAQueryFactory(em);
        QHello qHello = QHello.hello;
//        QHello qHello = new QHello("h");

        Hello findHello = query
                .selectFrom(qHello)
                .fetchOne();

        Assertions.assertThat(findHello).isEqualTo(hello);
    }

}