package jpabook.jpashop.chapter8.lazy;

import jpabook.jpashop.chapter8.proxy.Team;

import javax.persistence.*;
import java.util.List;

public class FetchTypeLazy {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();

        try {
            Team team = new Team();
            em.persist(team);

            LazyMember member = new LazyMember();
            member.setUsername("test");
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            // Member만 디비에서 로딩함
            LazyMember findMember = em.find(LazyMember.class, member.getId());
            // findMember.team -> 프록시 객체
            System.out.println("findMember.getTeam().getClass() = " + findMember.getTeam().getClass());

            System.out.println("----------------------------------------------");
            System.out.println("findMember.getTeam() = " + findMember.getTeam());
            System.out.println("----------------------------------------------");

            em.clear();

            // +@ join fetch
            // 필요한 경우 관련된 참조 테이블을 조인해서 같이 조회할 수 있는 기능
            List<LazyMember> resultList = em.createQuery("select m from LazyMember m join fetch m.team", LazyMember.class).getResultList();

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
