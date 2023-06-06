package com.study.jpa.ch1.v1;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
//@DynamicUpdate
public class MemberV1 {
    @Id
    @Column(name = "IO")
    private String id;

    @Column(name = "NAME")
    private String username;

    private Integer age;
}
