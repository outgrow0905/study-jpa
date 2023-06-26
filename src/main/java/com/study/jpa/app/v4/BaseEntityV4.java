package com.study.jpa.app.v4;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class BaseEntityV4 {
    private LocalDateTime createdTime;
    private LocalDateTime lasModifiedTime;
}
