package com.study.jpa.ch8.v1;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class CMemberV1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private int age;

    @ManyToOne
    private CTeamV1 team;
}
