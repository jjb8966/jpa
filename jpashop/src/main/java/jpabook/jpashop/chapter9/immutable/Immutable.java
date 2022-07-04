package jpabook.jpashop.chapter9.immutable;

import jpabook.jpashop.chapter9.embedded_type.AddressPra;
import jpabook.jpashop.chapter9.embedded_type.MemberPra;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Immutable {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();

        try {
            AddressPra address = new AddressPra("city", "street", "zip");

            MemberPra member1 = new MemberPra();
            member1.setUsername("member1");
            member1.setHomeAddress(address);
            em.persist(member1);

            MemberPra member2 = new MemberPra();
            member2.setUsername("member2");
            member2.setHomeAddress(address);
            em.persist(member2);

            // 공유중인 객체가 mutable 할 경우 -> member1 의 필드를 변경했는데 변경된 값이 member2와 공유됨
//            member1.getHomeAddress().setCity("newCity");

            // 해결 방법 1. 값을 복사해서 각각 저장
            AddressPra copyAddress = new AddressPra(address.getCity(), address.getStreet(), address.getZipcode());

            MemberPra member3 = new MemberPra();
            member3.setUsername("member3");
            member3.setHomeAddress(copyAddress);
            em.persist(member3);

            // member1의 city를 바꿔도 member3의 city는 바뀌지 않음
//            member1.getHomeAddress().setCity("newCity");

            // 해결 방법 2. setter 자체를 없애서 불변 객체로 만듦
            // 만약 member1의 city를 변경하고 싶다면?
            // 생성자로 새로 객체를 만들어서 넣어줘야함
            AddressPra newAddress = new AddressPra("newCity", address.getStreet(), address.getZipcode());
            member1.setHomeAddress(newAddress);

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
