package com.study.jpa.ch5.v1;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@DiscriminatorValue("B")
public class MyBookV1 extends MyItemV1{
    private String author;
    private String isbn;
}
