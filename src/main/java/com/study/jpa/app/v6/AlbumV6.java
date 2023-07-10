package com.study.jpa.app.v6;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@DiscriminatorValue("A")
public class AlbumV6 extends ItemV6 {
    private String artist;
    private String etc;
}
