package com.study.jpa.app.v1;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class ItemV1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID")
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
}
