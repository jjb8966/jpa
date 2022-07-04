package jpabook.jpashop.chapter10.type_expression;

import jpabook.jpashop.chapter10.domain.Member;
import jpabook.jpashop.chapter10.domain.MemberType;
import jpabook.jpashop.chapter10.type_expression.item.Book;
import jpabook.jpashop.chapter10.type_expression.item.Item;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class TypeExpression {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();

        try {
            Member member = new Member();
            member.setUsername("testMember");
            member.setMemberType(MemberType.ADMIN);
            em.persist(member);

            em.flush();
            em.clear();

            // enum type -> 클래스의 풀네임을 써줘야해서 번거로움
            // => 타입 바인딩으로 해결
//            List<Object[]> resultList = em.createQuery("select m, 'hello', true from Member m where m.memberType = jpabook.jpashop.chapter10.domain.MemberType")
//                    .getResultList();
            List<Object[]> resultList = em.createQuery("select m, 'hello', true from Member m where m.memberType = :memberType")
                    .setParameter("memberType", MemberType.ADMIN)
                    .getResultList();

            for (Object[] objects : resultList) {
                Object findMember = objects[0];
                Object hi = objects[1];
                Object booleanTrue = objects[2];

                System.out.println("findMember = " + findMember);
                System.out.println("hi = " + hi);
                System.out.println("booleanTrue = " + booleanTrue);
            }

            // 엔티티 타입 -> type 예약어를 이용해 상속관계에서 특정 자식 클래스를 조회할 수 있음
            List<Item> resultList1 = em.createQuery("select i from Item i where type(i) = Book", Item.class)
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
