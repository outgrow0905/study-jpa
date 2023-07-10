package com.study.jpa.ch7.v3;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;

@Getter
@Setter
@Embeddable
public class Address {
    private String city;
    private String street;
    private ZipCode zipCode;
}
