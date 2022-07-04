package jpabook.jpashop.chapter8.cascade;

import javax.persistence.*;

public class Cascade {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();

        try {
            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            // @OneToMany(mappedBy = "parent")
            // cascade 설정하지 않은 경우 -> 3번 persist 해야함
//            em.persist(parent);
//            em.persist(child1);
//            em.persist(child2);

            //@OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
            em.persist(parent);

            em.flush();
            em.clear();

            // @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST, orphanRemoval = true)
            // -> 부모 엔티티에서 자식 엔티티 삭제하면 자동으로 처리됨
            Parent findParent = em.find(Parent.class, parent.getId());
            findParent.getChildList().remove(0);

            // 부모를 지우면 자식들도 같이 지워짐
            em.remove(findParent);

            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}
