package com.study.jpa.ch3.v1;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@ToString
@Entity
public class PlayerV1 {
    @Id
    @Column(name = "PLAYER_ID")
    private String id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private TeamV1 team;
}
