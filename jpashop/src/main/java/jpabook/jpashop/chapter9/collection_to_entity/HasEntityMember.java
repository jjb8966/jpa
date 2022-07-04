package jpabook.jpashop.chapter9.collection_to_entity;

import jpabook.jpashop.chapter9.embedded_type.AddressPra;
import jpabook.jpashop.chapter9.embedded_type.Period;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class HasEntityMember {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;

    @Embedded
    private Period period;

    @Embedded
    private AddressPra address;

    @ElementCollection
    @CollectionTable(name = "FAVORITE_FOOD_ENTITY", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "FOOD_NAME") // member_id를 제외한 속성이 1개밖에 없으므로 가능함
    private Set<String> favoriteFoods = new HashSet<>();

//    @ElementCollection
//    @CollectionTable(name = "ADDRESS", joinColumns = @JoinColumn(name = "member_id"))
//    private List<Address> addressHistory = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true) // 생명주기를 엔티티와 동일하게 가져감
    @JoinColumn(name = "member_id") // 특수한 관계이므로 one 쪽이 연관관계 주인이 됨
    private List<AddressEntity> addressHistory = new ArrayList<>();
}
