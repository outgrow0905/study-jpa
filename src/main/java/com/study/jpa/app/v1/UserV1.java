package com.study.jpa.app.v1;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class UserV1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;
    private String name;
    private String city;
    private String street;
    private String zipcode;
}
