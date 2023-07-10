package com.study.jpa.ch7.v4;

import javax.persistence.Embeddable;

@Embeddable
public class ZipCode {
    private String zip;
    private String plusFour;
}
