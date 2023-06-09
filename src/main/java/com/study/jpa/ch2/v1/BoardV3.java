package com.study.jpa.ch2.v1;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@SequenceGenerator(
        name = "BOARD_SEQ_GENERATOR",
        sequenceName = "BOARD_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class BoardV3 {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "BOARD_SEQ_GENERATOR"
    )
    private int id;
    private String data;
}
