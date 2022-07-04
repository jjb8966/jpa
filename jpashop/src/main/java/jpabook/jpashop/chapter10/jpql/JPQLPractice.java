package jpabook.jpashop.chapter10.jpql;

import jpabook.jpashop.chapter10.domain.Member;
import jpabook.jpashop.chapter10.domain.Team;

import javax.persistence.*;
import java.util.List;

public class JPQLPractice {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            // flush 자동으로 일어남
            List<Member> find1 = em.createQuery(
                    "select m from Member m where m.username like '%1'", Member.class)
                    .getResultList();

            for (Member findMember : find1) {
                System.out.println(findMember.getUsername());
            }

            // native query -> 쿼리 대상이 테이블
            List<Member> find2 = em.createNativeQuery("select id from Member where username='member1'", Member.class)
                    .getResultList();

            for (Member member1 : find2) {
                System.out.println(member1.getUsername());
            }

            em.clear();

            // TypedQuery -> 조회 결과 타입을 지정할 수 있는 경우
            TypedQuery<Member> typedQueryMember = em.createQuery("select m from Member m where m.age = 10", Member.class);
            TypedQuery<String> typeQueryString = em.createQuery("select m.username from Member m", String.class);

            // Query -> 조회 결과 타입을 지정할 수 없는 경우
            Query query = em.createQuery("select m.id, m.username from Member m");

            // 결과 조회
            // 1. getResultList -> 조회 결과가 여러 개 인 경우
            // 조회 결과가 없는 경우 -> empty list 반환 (NPE x)
            List<Member> members = typedQueryMember.getResultList();

            for (Member m : members) {
                // TODO 로직
            }

            // 2. getSingleResult() -> 조회 결과가 1개인 경우
            // 조회 결과가 없는 경우 -> NoResultException 예외 발생
            // 조회 결과 2개 이상 -> NonUniqueResultException 예외 발생
            Member findMember = typedQueryMember.getSingleResult();

            em.clear();

            // 파라미터 바인딩
            // 위치 기반으로 바인딩 할 수도 있지만 유지보수가 어러우므로 이름 기반으로 바인딩할 것
            Member singleResult = em.createQuery("select m from Member m where m.username = :matchName", Member.class)
                    .setParameter("matchName", "member1")
                    .getSingleResult();

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
