package jpabook.jpashop.chapter9.embedded_type;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class EmbeddedTest {

    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = factory.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();

        try {
            MemberPra member = new MemberPra();
            member.setPeriod(new Period());
            member.setHomeAddress(new AddressPra("a", "b", "c"));
            em.persist(member);

            tr.commit();
        } catch (Exception e) {
            tr.rollback();
        } finally {
            em.close();
        }

        factory.close();
    }
}