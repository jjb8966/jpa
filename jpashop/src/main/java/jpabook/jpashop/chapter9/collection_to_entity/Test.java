package jpabook.jpashop.chapter9.collection_to_entity;

import jpabook.jpashop.chapter9.embedded_type.AddressPra;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Test {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();

        try {
            HasEntityMember member1 = new HasEntityMember();
            // 컬렉션 데이터 저장
            member1.setAddress(new AddressPra("homeCity", "street", "zip"));
            member1.getFavoriteFoods().add("치킨");
            member1.getFavoriteFoods().add("피자");
            member1.getFavoriteFoods().add("햄버거");
            member1.getAddressHistory().add(new AddressEntity("old1", "street", "zip"));
            member1.getAddressHistory().add(new AddressEntity("old2", "street", "zip"));
            em.persist(member1);    // persist 한번 하면 FAVORITE_FOOD, ADDRESS 테이블에 자동으로 insert 쿼리가 날라감

            em.flush();
            em.clear();

            // 조회 -> 기본이 지연 로딩
            System.out.println("=================멤버만 조회================");
            HasEntityMember findMember = em.find(HasEntityMember.class, member1.getId());

            System.out.println("=================favoriteFood 조회================");
            for (String favoriteFood : findMember.getFavoriteFoods()) {
                System.out.println("favoriteFood = " + favoriteFood);
            }

            System.out.println("=================addressHistory.city 조회================");
            for (AddressEntity addressEntity : findMember.getAddressHistory()) {
                System.out.println("addressEntity.getAddress().getCity() = " + addressEntity.getAddress().getCity());
            }

            em.clear();

            // 수정
            System.out.println("=================멤버만 조회================");
            HasEntityMember findMember2 = em.find(HasEntityMember.class, member1.getId());

            System.out.println("=================값 타입 수정================");
            // (값 타입) address homeCity -> newCity
            //findMember2.getAddress().setCity("newCity"); -> immutable 객체로 설계해야 하기 때문에 이렇게 못 씀 (절대 이렇게 하지 말 것)
            AddressPra origin = findMember2.getAddress();
            findMember2.setAddress(new AddressPra("newCity", origin.getStreet(), origin.getZipcode()));

            em.flush();

            System.out.println("=================값 타입 컬렉션 수정================");
            System.out.println("=================favoriteFoods 수정================");
            // Set<String> favoriteFoods 치킨 -> 한식
            // -> 값 자체를 지우고 추가해야 함 (업데이트 개념이 아님)
            findMember2.getFavoriteFoods().remove("치킨");
            findMember2.getFavoriteFoods().add("한식");

            em.flush();

            System.out.println("=================addressHistory 수정================");
            // List<Address> addressHistory old1 -> new1
            findMember2.getAddressHistory().remove(new AddressEntity("old1", "street", "zip"));
            findMember2.getAddressHistory().add(new AddressEntity("new1", "street", "zip"));

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
