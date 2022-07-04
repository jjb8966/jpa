package jpabook.jpashop.chapter10.projection;

import jpabook.jpashop.chapter10.domain.Address;
import jpabook.jpashop.chapter10.domain.Member;
import jpabook.jpashop.chapter10.domain.Team;

import javax.persistence.*;
import java.util.List;

public class Projection {

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
            
            em.flush();
            em.clear();

            // 엔티티 프로젝션 1
            // select 결과로 조회된 엔티티들은 모두 영속성 컨텍스트가 관리함
            // -> 수정 시 update 쿼리 나감
            List<Member> entityProjection1 = em.createQuery("select m from Member m", Member.class)
                    .getResultList();

            Member member1 = entityProjection1.get(0);
            member1.setUsername("memberNew");

            em.flush();

            // 엔티티 프로젝션 2
            // m.team -> team을 조회하는 것이므로 Team 테이블과 조인하여 조회함 (묵시적 조인)
            // 참고) 묵시적 조인하지말고 명시적 조인을 해라 (뒤에서 자세히)
            List<Team> entityProjection2 = em.createQuery("select m.team from Member m", Team.class)
                    .getResultList();

            // 임베디드 타입 프로젝션
            List<Address> embeddedTypeProjection = em.createQuery("select o.address from Order o", Address.class)
                    .getResultList();

            // 스칼라 타입 프로젝션 -> 필요한 속성만 조회
            // List에 Object[] 들어가 있음
//            List resultList = em.createQuery("select m.username, m.age from Member m")
//                    .getResultList();
            List<Object[]> resultList = em.createQuery("select m.username, m.age from Member m")
                    .getResultList();

            Object[] result = resultList.get(0);
            Object username = result[0];
            Object age = result[1];

            System.out.println("username = " + username);
            System.out.println("age = " + age);

            // 스칼라 타입 -> new 명령어로 조회
            // 새로운 DTO 클래스를 만들어서 객체 타입으로 조회하기 - 제일 깔끔함
            List<MemberDTO> scalarTypeProjection = em.createQuery("select new jpabook.jpashop.chapter10.projection.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();

            MemberDTO memberDTO = scalarTypeProjection.get(0);
            System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
            System.out.println("memberDTO.getAge() = " + memberDTO.getAge());
            
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
