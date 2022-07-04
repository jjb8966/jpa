package jpabook.jpashop.chapter8.cascade;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Child {

    @Id @GeneratedValue
    @Column(name = "child_id")
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;
}
