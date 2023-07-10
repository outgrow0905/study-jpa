package com.study.jpa.app.v6;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class UserV6 extends BaseEntityV6 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;
    private String name;
    private AddressV6 address;

    @OneToMany(mappedBy = "user")
    List<OrderV6> orders = new ArrayList<>();
}
