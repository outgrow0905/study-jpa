package com.study.jpa.ch3.v4;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class MyOrderV3MyProductV3 {

    @Id
    @Column(name = "ORDER_PRODUCT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private MyOrderV3 order;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private MyProductV3 product;

    private int count;
    private LocalDateTime orderDate;
}
