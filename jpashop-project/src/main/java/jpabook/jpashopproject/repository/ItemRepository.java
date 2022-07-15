package jpabook.jpashopproject.repository;

import jpabook.jpashopproject.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item);
        } else {
            // entity -> 영속성 컨텍스트가 관리함 (item x)
            Item entity = em.merge(item);

            // item price==null 이라면??
            // -> entity price는 무조건 null
            // ==> 변경 감지를 사용해라!!
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
