package com.study.jpa.ch4.v3;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public class GrandChildV3Id implements Serializable {
    private int grandChildId;
    private ChildV3Id child;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrandChildV3Id that = (GrandChildV3Id) o;
        return grandChildId == that.grandChildId && Objects.equals(child, that.child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grandChildId, child);
    }
}
