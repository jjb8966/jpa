package jpabook.jpashop.chapter8.proxy;

import org.hibernate.Hibernate;

import javax.persistence.*;

public class ProxyFunction {

    public static void main(String[] args) {
        EntityManagerFactory emf= Persistence.createEntityManagerFactory("jpashop");
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
            // 1. 프록시 강제 초기화
//             reference.getUsername();
            Hibernate.initialize(reference);

            // 2. 프록시 인스턴스 초기화 여부
            PersistenceUnitUtil persistenceUnitUtil = emf.getPersistenceUnitUtil();
            boolean loaded = persistenceUnitUtil.isLoaded(reference);
            System.out.println("isLoaded = " + loaded);

            // 3. 프록시 클래스 이름 얻기
            System.out.println("proxyName = " + reference.getClass().getName());

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
