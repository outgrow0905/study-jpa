package com.study.jpa.app.v4;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@DiscriminatorValue("B")
public class BookV4 extends ItemV4 {
    private String author;
    private String isbn;
}
