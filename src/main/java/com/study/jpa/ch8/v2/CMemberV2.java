package com.study.jpa.ch8.v2;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NamedQuery(
        name = "CMemberV2.findByUsername", query = "select m from CMemberV2 m where m.username = :username"
)
public class CMemberV2 {
    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private int age;
}
