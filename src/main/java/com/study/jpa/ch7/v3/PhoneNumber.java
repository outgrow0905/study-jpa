package com.study.jpa.ch7.v3;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class PhoneNumber {
    private String areaCode;
    private String localNumber;
    @ManyToOne
    private PhoneServiceProviderV3 provider;
}
