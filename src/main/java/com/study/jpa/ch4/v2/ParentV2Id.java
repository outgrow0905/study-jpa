package com.study.jpa.ch4.v2;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ParentV2Id implements Serializable {
    @Column(name = "PARENT_ID1")
    private int parentId1;

    @Column(name = "PARENT_ID2")
    private int parentId2;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParentV2Id that = (ParentV2Id) o;
        return parentId1 == that.parentId1 && parentId2 == that.parentId2;
//        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentId1, parentId2);
    }
}
