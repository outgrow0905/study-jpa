package com.study.jpa.ch6.v1;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@Entity
public class HelloTeamV1 {
    @Id
    private String name;

    private String address;

    @OneToMany(mappedBy = "team")
    private List<HelloMemberV1> members;
}
