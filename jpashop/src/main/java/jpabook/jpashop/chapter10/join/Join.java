package jpabook.jpashop.chapter10.join;

import jpabook.jpashop.chapter10.domain.Member;
import jpabook.jpashop.chapter10.domain.Team;

import javax.persistence.*;
import java.util.List;

public class Join {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();

        try {
            Team team = new Team();
            team.setName("team1");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            member.changeTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("====================innerJoinResult====================");
            List<Member> innerJoinResult = em.createQuery("select m from Member m inner join m.team t", Member.class)
                    .getResultList();

            System.out.println("====================leftOuterJoinResult====================");
            List<Member> leftOuterJoinResult = em.createQuery("select m from Member m left join m.team t", Member.class)
                    .getResultList();

            System.out.println("====================thetaJoinResult====================");
            List<Member> thetaJoinResult = em.createQuery("select m from Member m, Team t where m.age=10", Member.class)
                    .getResultList();

            // join on
            // 1. 조인 대상 필터링
            System.out.println("====================조인 대상 필터링====================");
            List joinOn1 = em.createQuery("select m,t from Member m left join m.team t on t.name='team1'")
                    .getResultList();

            // 2. 연관관계 없는 엔티티 외부 조인
            System.out.println("====================연관관계 없는 엔티티 외부 조인====================");
            List joinOn2 = em.createQuery("select m,t from Member m left join Team t on m.username=t.name")
                    .getResultList();

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
