package com.study.jpa.ch6.v1;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class HelloMemberV1 {
    @Id
    private String username;

    private String address;

    @ManyToOne
    private HelloTeamV1 team;
}
