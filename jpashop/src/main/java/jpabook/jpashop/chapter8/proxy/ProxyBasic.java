package jpabook.jpashop.chapter8.proxy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class ProxyBasic {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();

        try {
            ProxyMember member = new ProxyMember();
            member.setUsername("hello");
            em.persist(member);

            em.flush();
            em.clear();

            // find는 디비에서 바로 조회 후 진짜 객체를 바로 반환
//            Member findMember = em.find(Member.class, member.getId());

            //getReference는 디비 조회 미루고 가짜 객체를 반환
            ProxyMember refMember = em.getReference(ProxyMember.class, member.getId());

            System.out.println("메소드 사용 전 refMember : " + refMember.getClass());

            // db에서 꺼내와야 하는 username -> 이 때 쿼리로 조회함
            System.out.println("refMember.getUsername() = " + refMember.getUsername());

            System.out.println("메소드 사용 후 refMember : " + refMember.getClass());

            tr.commit();
        } catch (Exception e) {
            tr.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
