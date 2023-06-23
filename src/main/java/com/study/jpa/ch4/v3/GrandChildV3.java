package com.study.jpa.ch4.v3;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@IdClass(GrandChildV3Id.class)
@Entity
public class GrandChildV3 {
    @Id
    @Column(name = "GRAND_CHILD_ID")
    private int grandChildId;

    @Id
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID", referencedColumnName = "PARENT_ID"),
            @JoinColumn(name = "CHILD_ID", referencedColumnName = "CHILD_ID")
    })
    private ChildV3 child;
}
