package com.study.jpa.ch4.v4;

import com.study.jpa.ch4.v3.ChildV3;
import com.study.jpa.ch4.v3.GrandChildV3Id;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
public class GrandChildV4 {

    @EmbeddedId
    private GrandChildV4Id grandChildId;

    @MapsId("childId")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID", referencedColumnName = "PARENT_ID"),
            @JoinColumn(name = "CHILD_ID", referencedColumnName = "CHILD_ID")
    })
    private ChildV4 child;
}
