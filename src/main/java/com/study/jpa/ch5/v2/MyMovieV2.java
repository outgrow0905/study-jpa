package com.study.jpa.ch5.v2;

import com.study.jpa.ch5.v1.MyItemV1;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@DiscriminatorValue("M")
public class MyMovieV2 extends MyItemV2 {
    private String director;
    private String actor;
}
