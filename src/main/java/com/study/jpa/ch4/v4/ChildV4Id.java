package com.study.jpa.ch4.v4;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChildV4Id implements Serializable {
    @Column(name = "CHILD_ID")
    private int childId;


    private int parentId;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChildV4Id childV4Id = (ChildV4Id) o;
        return childId == childV4Id.childId && parentId == childV4Id.parentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(childId, parentId);
    }
}
