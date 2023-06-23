package com.study.jpa.ch4.v3;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@IdClass(ChildV3Id.class)
@Entity
public class ChildV3 {
    @Id
    @Column(name = "CHILD_ID")
    private int childId;

    @Id
    @ManyToOne
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "PARENT_ID")
    private ParentV3 parent;
}
