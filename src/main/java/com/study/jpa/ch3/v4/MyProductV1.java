package com.study.jpa.ch3.v4;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
public class MyProductV1 {
    @Id
    @Column(name = "PRODUCT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String productName;

    @ManyToMany(mappedBy = "products")
    List<MyOrderV1> orders;
}
