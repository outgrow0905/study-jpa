package com.study.jpa.app.v5;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@DiscriminatorValue("M")
public class MovieV5 extends ItemV5 {
    private String director;
    private String actor;
}
