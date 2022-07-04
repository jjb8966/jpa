package jpabook.jpashop.chapter8.eager;

import jpabook.jpashop.chapter8.proxy.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class FetchTypeEager {

    public static void main(String[] args) {
        EntityManagerFactory emf= Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();

        try {
            Team team = new Team();
            em.persist(team);

            EagerMember member = new EagerMember();
            member.setUsername("test");
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            // Member, Team join 해서 같이 로딩
            EagerMember findMember = em.find(EagerMember.class, member.getId());
            // findMember.team -> 프록시 객체
            System.out.println("findMember.getTeam().getClass() = " + findMember.getTeam().getClass());

            System.out.println("----------------------------------------------");
            System.out.println("findMember.getTeam() = " + findMember.getTeam());
            System.out.println("----------------------------------------------");

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
