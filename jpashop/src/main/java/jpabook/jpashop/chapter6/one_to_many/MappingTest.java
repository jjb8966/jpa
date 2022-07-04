package jpabook.jpashop.chapter6.one_to_many;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class MappingTest {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();
        try {
            MemberV2 member = new MemberV2();
            em.persist(member);

            TeamV2 team = new TeamV2();
            List<MemberV2> members = team.getMembers();
            members.add(member);
            em.persist(team);

            tr.commit();
        } catch (Exception e) {
            tr.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
