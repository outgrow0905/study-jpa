package com.study.jpa.ch3.v4;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
public class MyOrderV3 {
    @Id
    @Column(name = "ORDER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderName;

    @OneToMany(mappedBy = "order")
    List<MyOrderV3MyProductV3> orderProducts;
}