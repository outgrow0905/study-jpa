package com.study.jpa.ch6.v4;

import com.study.jpa.ch6.v3.MyParentV1;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class MyChildV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private MyParentV2 parent;
}
