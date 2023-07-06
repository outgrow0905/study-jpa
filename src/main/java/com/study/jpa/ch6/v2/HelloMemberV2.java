package com.study.jpa.ch6.v2;

import com.study.jpa.ch6.v1.HelloTeamV1;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class HelloMemberV2 {
    @Id
    private String username;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    private HelloTeamV2 team;
}
