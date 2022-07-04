package jpabook.jpashop.chapter8.eager;

import jpabook.jpashop.chapter8.proxy.Team;

import javax.persistence.*;
import java.util.List;

public class NPlusOneProb {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
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

            // select member만 했는데 Team 테이블을 조회하는 쿼리가 나감 -> N + 1
            System.out.println("---------------------1 + 1---------------------");
            List<EagerMember> members = em.createQuery("select m from LazyMember m", EagerMember.class).getResultList();
            System.out.println("-----------------------------------------------");
            // 만약 멤버들이 다른 팀 소속이라면?
            // 1 : 최초 쿼리
            // N : 최초 쿼리로 발생하는 쿼리
            em.clear();

            Team otherTeam = new Team();
            em.persist(otherTeam);

            EagerMember newMember = new EagerMember();
            newMember.setUsername("newMember");
            newMember.setTeam(otherTeam);
            em.persist(newMember);

            em.flush();
            em.clear();

            // select member 쿼리 1개로 Team을 조회하는 쿼리 2번 나감
            System.out.println("---------------------2 + 1---------------------");
            List<EagerMember> newMembers = em.createQuery("select m from LazyMember m", EagerMember.class).getResultList();
            System.out.println("-----------------------------------------------");

            // 결론 : 즉시 로딩 (EAGER) 쓰지 마라
            // -> 전부 지연 로딩으로 작성하고 fetch join으로 필요할 떄 땡겨오자 (fetch join은 나중에)
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
