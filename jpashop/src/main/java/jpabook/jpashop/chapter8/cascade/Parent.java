package jpabook.jpashop.chapter8.cascade;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Parent {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Child> childList = new ArrayList<>();

    // 연관관계 편의 메소드 -> 양방향 초기화
    public void addChild(Child child) {
        childList.add(child);
        child.setParent(this);
    }
}
