package com.study.jpa.ch4.v3;

import com.study.jpa.ch4.v1.ParentV1Id;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Getter
@Setter
@Entity
public class ParentV3 {
    @Id
    @Column(name = "PARENT_ID")
    private int parentId;
    private String name;
}
