package domain;

import domain.item.Book;
import domain.item.Item;
import domain.item.Movie;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class ItemTest {

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
        tx.rollback();
        em.close();
        emf.close();
    }

    @Test
    void type() {
        Book book = new Book();
        book.setName("bookA");
        em.persist(book);

        Movie movie = new Movie();
        movie.setTitle("movieA");
        em.persist(movie);

        List result = em.createQuery("select i from Item i where type(i) in (Book)")
                .getResultList();

        List nativeResult = em.createNativeQuery("select i.* from Item i where i.what_kind_of_item in ('this_is_book')", Item.class)
                .getResultList();

        for (Object o : result) {
            Book findBook = (Book) o;
            System.out.println("findBook = " + findBook);
        }

        for (Object o : nativeResult) {
            Book nativeBook = (Book) o;
            System.out.println("nativeBook = " + nativeBook);
        }
    }

    @Test
    void treat() {
        Book bookA = new Book();
        bookA.setName("bookA");
        bookA.setAuthor("authorA");

        Book bookB = new Book();
        bookB.setName("bookB");
        bookB.setAuthor("authorB");

        em.persist(bookA);
        em.persist(bookB);

        List result = em.createQuery("select i from Item i where treat(i as Book).author = 'authorA'")
                .getResultList();

        for (Object o : result) {
            System.out.println("o = " + o);
        }
    }

    @Test
    void 엔티티_직접_사용() {
        Book bookA = new Book();
        bookA.setName("bookA");

        Book bookB = new Book();
        bookB.setName("bookB");

        em.persist(bookA);
        em.persist(bookB);

        Book findBook = em.createQuery("select b from Book b where b = :book", Book.class)
                .setParameter("book", bookA)
                .getSingleResult();

        assertThat(findBook.getName()).isEqualTo(bookA.getName());
    }

    @Test
    void named_쿼리() {
        Book bookA = new Book();
        bookA.setName("bookA");

        Book bookB = new Book();
        bookB.setName("bookB");

        em.persist(bookA);
        em.persist(bookB);

        Book findBook = em.createNamedQuery("Book.findByName", Book.class)
                .setParameter("name", "bookA")
                .getSingleResult();

        assertThat(findBook).isEqualTo(bookA);
    }

    @Test
    void 벌크_연산() {
        for (int i = 0; i < 10; i++) {
            Book book = new Book();
            book.setPrice(i);
            em.persist(book);
        }

        int count = em.createQuery("update Book b set b.price = b.price * 10 where b.price < :price")
                .setParameter("price", 5)
                .executeUpdate();

        System.out.println("count = " + count);

        // 영속성 컨텍스트 초기화 x -> 영속성 컨텍스트 1차 캐시에 데이터를 조회하므로 변경되기 전 데이터가 조회됨
        List<Book> result = em.createQuery("select b from Book b", Book.class)
                .getResultList();

        assertThat(result).as("초기화 전")
                .extracting("price")
                .doesNotContain(10);

        // 영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        // DB에서 다시 데이터를 조회하므로 변경된 데이터가 조회됨
        List<Book> resultAfterClear = em.createQuery("select b from Book b", Book.class)
                .getResultList();

        assertThat(resultAfterClear).as("초기화 후")
                .extracting("price")
                .contains(10, 20, 30, 40);
    }

    @Test
    @DisplayName("table per class 전략은 슈퍼 타입 테이블로 조회 시 효율이 엄청 떨어짐")
    void table_per_class() {
        Book book = new Book();
        book.setName("bookA");
        em.persist(book);

        em.flush();
        em.clear();

        Item item = em.find(Item.class, book.getId());

        assertThat(item.getName()).isEqualTo(book.getName());
    }
}
