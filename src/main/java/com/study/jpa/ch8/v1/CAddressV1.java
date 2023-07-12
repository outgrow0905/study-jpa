package com.study.jpa.ch8.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CAddressV1 {
    private String city;
    private String street;
    private String zipcode;
}
