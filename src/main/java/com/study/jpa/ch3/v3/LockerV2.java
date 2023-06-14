package com.study.jpa.ch3.v3;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class LockerV2 {
    @Id
    @Column(name = "LOCKER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String location;

    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private LockerMemberV2 member;
}
