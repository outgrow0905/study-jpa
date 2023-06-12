package com.study.jpa.ch3.v1;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
public class TeamV1 {
    @Id
    @Column(name = "TEAM_ID")
    private String id;

    private String name;

    @OneToMany(mappedBy = "team")
    private List<PlayerV1> players = new ArrayList<>();
}
