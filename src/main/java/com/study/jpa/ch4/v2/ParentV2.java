package com.study.jpa.ch4.v2;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class ParentV2 {
    @EmbeddedId
    private ParentV2Id id;

    private String name;
}
