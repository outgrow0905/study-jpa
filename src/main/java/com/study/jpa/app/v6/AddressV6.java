package com.study.jpa.app.v6;

import javax.persistence.Embeddable;

@Embeddable
public class AddressV6 {
    private String city;
    private String street;
    private String zipcode;
}
