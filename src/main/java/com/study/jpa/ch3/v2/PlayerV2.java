package com.study.jpa.ch3.v2;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class PlayerV2 {
    @Id
    @Column(name = "PLAYER_ID")
    private String id;
    private String username;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID", insertable = false, updatable = false)
    private TeamV2 team;
}
