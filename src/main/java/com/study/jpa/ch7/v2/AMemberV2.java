package com.study.jpa.ch7.v2;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class AMemberV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

//    @Embedded
    private Period workPeriod;

//    @Embedded
    private Address homeAddress;
}
