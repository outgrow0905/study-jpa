package com.study.jpa.ch3.v4;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@IdClass(MemberProductIdV2.class)
public class MyOrderV2MyProductV2 {
    @Id
    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private MyOrderV2 order;

    @Id
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private MyProductV2 product;

    private int count;
    private LocalDateTime orderDate;
}
