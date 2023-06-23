package com.study.jpa.ch4.v4;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class ParentV4 {
    @Id
    @Column(name = "PARENT_ID")
    private int parentId;
    private String name;
}
