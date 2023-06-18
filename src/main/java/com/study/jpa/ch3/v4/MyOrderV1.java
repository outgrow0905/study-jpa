package com.study.jpa.ch3.v4;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
public class MyOrderV1 {
    @Id
    @Column(name = "ORDER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderName;

    @ManyToMany
    @JoinTable(
            name = "MyOrderV1_MyProductV1",
            joinColumns = @JoinColumn(name = "ORDER_ID"),
            inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID"))
    List<MyProductV1> products;
}
