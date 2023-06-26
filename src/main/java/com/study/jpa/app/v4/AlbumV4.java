package com.study.jpa.app.v4;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@DiscriminatorValue("A")
public class AlbumV4 extends ItemV4 {
    private String artist;
    private String etc;
}
