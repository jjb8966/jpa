package jpabook.jpashop.chapter8.proxy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class DetachedEntity {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();

        try {
            ProxyMember member = new ProxyMember();
            member.setUsername("test");
            em.persist(member);

            em.flush();
            em.clear();

            ProxyMember reference = em.getReference(ProxyMember.class, member.getId());
            System.out.println("reference = " + reference.getClass());

            em.detach(reference);
            // = em.close()
            // = em.clear()

            System.out.println("reference.getUsername() = " + reference.getUsername());
            // 에러 발생
            // org.hibernate.LazyInitializationException: could not initialize proxy [jpabook.jpashop.chapter8.proxy.Member#1] - no Session

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
