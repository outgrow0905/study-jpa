package com.study.jpa.ch4.v4;

import com.study.jpa.ch4.v3.ChildV3Id;
import com.study.jpa.ch4.v3.ParentV3;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
public class ChildV4 {

    @EmbeddedId
    private ChildV4Id childId;

    @MapsId("parentId")
    @ManyToOne
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "PARENT_ID")
    private ParentV4 parent;
}
