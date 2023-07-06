package com.study.jpa.ch6.v5;

import com.study.jpa.ch6.v4.MyParentV2;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class MyChildV3 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private MyParentV3 parent;
}
