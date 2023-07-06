package com.study.jpa.app.v5;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class BaseEntityV5 {
    private LocalDateTime createdTime;
    private LocalDateTime lasModifiedTime;
}
