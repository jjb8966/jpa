package jpabook.jpashop.chapter10.paging;

import jpabook.jpashop.chapter10.domain.Address;
import jpabook.jpashop.chapter10.domain.Member;
import jpabook.jpashop.chapter10.domain.Team;
import jpabook.jpashop.chapter10.projection.MemberDTO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class Paging {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();

        try {
            for (int i = 0; i < 100; i++) {
                Member member = new Member();
                member.setUsername("member" + i);
                member.setAge(i);
                em.persist(member);
            }

            // db 별로 페이징 쿼리가 다 다름 -> 특히 oracle 은 굉장히 복잡한 쿼리가 나감
            // mysql db -> ~~ member0_.age desc limit ?,?
            // h2 db -> ~~ member0_.age desc limit ? offset ?
            // oracle db
            /**
             * select from
                    * (select
                    * row_. *,
             *rownum rownum_
             *from
                    * (select
                    * member0_.id as id1_0_,
             *member0_.age as age2_0_,
             *member0_.team_id as team_id4_0_,
             *member0_.username as username3_0_
                    * from
                    * Member member0_
                    * order by
                    * member0_.age desc )row_
                    * where
                    * rownum <= ?
             *             )
             *where
                    * rownum_ > ?
             */
            List<Member> resultList = em.createQuery("select m from Member m order by m.age desc ", Member.class)
                    .setFirstResult(1)
                    .setMaxResults(10)
                    .getResultList();

            for (Member member : resultList) {
                System.out.println("member = " + member);
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
