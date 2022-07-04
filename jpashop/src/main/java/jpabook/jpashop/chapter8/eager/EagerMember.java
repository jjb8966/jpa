package jpabook.jpashop.chapter8.eager;

import jpabook.jpashop.chapter8.proxy.Team;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class EagerMember {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private String city;
    private String street;
    private String zipcode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    private Team team;
}
