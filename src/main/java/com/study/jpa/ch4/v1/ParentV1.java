package com.study.jpa.ch4.v1;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Getter
@Setter
@Entity
@IdClass(ParentV1Id.class)
public class ParentV1 {
    @Id
    @Column(name = "PARENT_ID1")
    private int parentId1;

    @Id
    @Column(name = "PARENT_ID2")
    private int parentId2;

    private String name;
}
