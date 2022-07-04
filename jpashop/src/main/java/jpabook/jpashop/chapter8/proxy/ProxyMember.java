package jpabook.jpashop.chapter8.proxy;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class ProxyMember {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private String city;
    private String street;
    private String zipcode;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
}
