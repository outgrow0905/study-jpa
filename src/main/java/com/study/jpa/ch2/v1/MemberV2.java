package com.study.jpa.ch2.v1;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "NAME_AGE_UNIQUE", columnNames = {"NAME", "AGE"})})
public class MemberV2 {
    @Id
    @Column(name = "IO")
    private String id;

    @Column(name = "NAME", nullable = false, length = 10)
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    private LocalDateTime createDate;

    private LocalDateTime lastModifiedDate;

    @Lob
    private String description;

    public enum RoleType {
        USER, ADMIN
    }
}
