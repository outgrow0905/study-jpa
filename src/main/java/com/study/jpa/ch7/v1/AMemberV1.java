package com.study.jpa.ch7.v1;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class AMemberV1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    // 근무기간
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // 집 주소
    private String city;
    private String street;
    private String zipcode;
}
