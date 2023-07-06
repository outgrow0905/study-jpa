package com.study.jpa.ch6.v2;

import com.study.jpa.ch6.v1.HelloMemberV1;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@Entity
public class HelloTeamV2 {
    @Id
    private String name;

    private String address;

    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
    private List<HelloMemberV2> members;
}
