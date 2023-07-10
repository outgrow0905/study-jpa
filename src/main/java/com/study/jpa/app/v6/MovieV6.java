package com.study.jpa.app.v6;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@DiscriminatorValue("M")
public class MovieV6 extends ItemV6 {
    private String director;
    private String actor;
}
