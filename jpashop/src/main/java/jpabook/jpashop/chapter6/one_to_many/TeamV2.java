package jpabook.jpashop.chapter6.one_to_many;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Entity
@Getter @Setter
public class TeamV2 {

    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany
    @JoinColumn(name = "team_id")
    private List<MemberV2> members = new ArrayList<>();
}
