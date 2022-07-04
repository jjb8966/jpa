package jpabook.jpashop.chapter10.select_etc;

import jpabook.jpashop.chapter10.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class SelectCase {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();

        try {
            Member member1 = new Member();
            Member member2 = new Member();
            Member member3 = new Member();

            member1.setAge(9);
            member1.setUsername("member1");
            member2.setAge(70);
            member2.setUsername("member2");
            member3.setAge(30);
            member3.setUsername(null);

            em.persist(member1);
            em.persist(member2);
            em.persist(member3);

            // select case
            List<String> resultList = em.createQuery("select " +
                            "case when m.age <= 10 then '학생 요금' " +
                            "when m.age >= 60 then '경로 요금'" +
                            "else '일반 요금'" +
                            "end " +
                            "from Member m", String.class)
                    .getResultList();

            for (String s : resultList) {
                System.out.println("요금 = " + s);
            }

            // select coalesce
            List<String> resultList1 = em.createQuery("select coalesce(m.username, '이름 없는 회원') from Member m", String.class)
                    .getResultList();

            for (String s : resultList1) {
                System.out.println("회원 이름 = " + s);
            }

            // select nullif
            List<String> resultList2 = em.createQuery("select nullif(m.username, 'member1') from Member m", String.class)
                    .getResultList();
            for (String s : resultList2) {
                System.out.println("member1은 null로 바꾸기 = " + s);
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
