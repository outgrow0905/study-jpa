package com.study.jpa.ch4.v1;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public class ParentV1Id implements Serializable {
    private int parentId1; // ParentV1의 @Id의 변수명과 동일해야 한다.
    private int parentId2; // ParentV1의 @Id의 변수명과 동일해야 한다.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParentV1Id that = (ParentV1Id) o;
        return parentId1 == that.parentId1 && parentId2 == that.parentId2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentId1, parentId2);
    }
}
