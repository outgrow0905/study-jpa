package com.study.jpa.app.v1;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class OrderItemV1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID")
    private Long id;
    @Column(name = "ITEM_ID")
    private Long itemId;
    @Column(name = "ORDER_ID")
    private Long orderId;
    private int orderPrice;
    private int count;
}
