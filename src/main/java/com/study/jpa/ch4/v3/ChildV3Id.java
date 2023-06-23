package com.study.jpa.ch4.v3;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public class ChildV3Id implements Serializable {
    private int parent;
    private int childId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChildV3Id childV3Id = (ChildV3Id) o;
        return parent == childV3Id.parent && childId == childV3Id.childId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, childId);
    }
}
