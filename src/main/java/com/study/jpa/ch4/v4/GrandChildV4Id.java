package com.study.jpa.ch4.v4;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GrandChildV4Id implements Serializable {
    @Column(name = "GRAND_CHILD_ID")
    private int grandChildId;
    private ChildV4Id childId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrandChildV4Id that = (GrandChildV4Id) o;
        return grandChildId == that.grandChildId && Objects.equals(childId, that.childId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grandChildId, childId);
    }
}
