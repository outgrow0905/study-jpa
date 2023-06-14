package com.study.jpa.ch3.v2;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
public class TeamV2 {
    @Id
    @Column(name = "TEAM_ID")
    private String id;

    private String name;

    @OneToMany
    @JoinColumn(name = "TEAM_ID")
    private List<PlayerV2> players = new ArrayList<>();
}
