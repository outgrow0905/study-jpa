package com.study.jpa.ch2.v1;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class BoardV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String data;
}