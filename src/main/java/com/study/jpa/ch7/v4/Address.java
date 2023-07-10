package com.study.jpa.ch7.v4;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
//@Setter
@Embeddable
public class Address {
    private String city;
    private String street;
    private ZipCode zipCode;

    protected Address() {}

    public Address(String city, String street, ZipCode zipCode) {
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
    }
}
