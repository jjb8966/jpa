package jpabook.jpashop.chapter9.collection_field;

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
public class HasCollectionMember {

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
    @CollectionTable(name = "FAVORITE_FOOD", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "FOOD_NAME") // member_id를 제외한 속성이 1개밖에 없으므로 가능함
    private Set<String> favoriteFoods = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "ADDRESS", joinColumns = @JoinColumn(name = "member_id"))
    private List<AddressPra> addressHistory = new ArrayList<>();

}
