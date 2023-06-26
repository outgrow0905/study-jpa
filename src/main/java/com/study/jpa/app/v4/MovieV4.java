package com.study.jpa.app.v4;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@DiscriminatorValue("M")
public class MovieV4 extends ItemV4 {
    private String director;
    private String actor;
}
