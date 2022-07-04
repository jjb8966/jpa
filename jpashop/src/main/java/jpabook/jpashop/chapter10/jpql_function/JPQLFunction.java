package jpabook.jpashop.chapter10.jpql_function;

import jpabook.jpashop.chapter10.domain.Member;
import jpabook.jpashop.chapter10.domain.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JPQLFunction {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();

        try {
            Team team = new Team();
            em.persist(team);

            Member member1 = new Member();
            Member member2 = new Member();
            member1.changeTeam(team);
            member2.changeTeam(team);

            em.persist(member1);
            em.persist(member2);

            // concat
            List<String> resultList = em.createQuery("select concat('a','b') from Member m", String.class)
                    .getResultList();

            for (String s : resultList) {
                System.out.println("s = " + s);
            }

            // size
            List<Integer> resultList1 = em.createQuery("select size(t.members) from Team t", Integer.class)
                    .getResultList();
            for (Integer integer : resultList1) {
                System.out.println("team size = " + integer);
            }

            // 사용자 정의 함수
            // 사용전에 방언(dialect)에 추가해줘야 함
            List<String> resultList2 = em.createQuery("select function( 'group_concat', m) from Member m", String.class)
                    .getResultList();
            for (String s : resultList2) {
                System.out.println("s = " + s);
            }

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
