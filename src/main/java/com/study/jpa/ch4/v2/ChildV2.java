package com.study.jpa.ch4.v2;

import com.study.jpa.ch4.v1.ParentV1;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class ChildV2 {
    @Id
    @Column(name = "CHILD_ID1")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int childId1;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID1", referencedColumnName = "PARENT_ID1"),
            @JoinColumn(name = "PARENT_ID2", referencedColumnName = "PARENT_ID2")
    })
    private ParentV2 parent;
}
