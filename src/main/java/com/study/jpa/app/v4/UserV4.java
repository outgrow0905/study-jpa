package com.study.jpa.app.v4;

import com.study.jpa.app.v3.OrderV3;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class UserV4 extends BaseEntityV4 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;
    private String name;
    private String city;
    private String street;
    private String zipcode;

    @OneToMany(mappedBy = "user")
    List<OrderV4> orders = new ArrayList<>();
}
