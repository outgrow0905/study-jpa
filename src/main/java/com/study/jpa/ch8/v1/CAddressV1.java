package com.study.jpa.ch8.v1;

import javax.persistence.Embeddable;

@Embeddable
public class CAddressV1 {
    private String city;
    private String street;
    private String zipcode;
}
