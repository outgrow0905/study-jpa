package com.study.jpa.ch8.v1;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class COrderV1 {
    @Id
    @Column(name = "ORDER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int orderAmount;
    private CAddressV1 address;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private CMemberV1 member;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private CProductV1 product;
}
