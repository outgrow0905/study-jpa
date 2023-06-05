package com.study.jpa.ch1.v1;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class MemberV1 {
    @Id
    @Column(name = "IO")
    private String id;

    @Column(name = "NAME")
    private String username;

    private Integer age;
}
