package com.study.jpa.ch2.v1;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@TableGenerator(
        name = "BOARD_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "BOARD_SEQ",
        allocationSize = 1
)
public class BoardV4 {
    @Id
    @GeneratedValue(
            strategy = GenerationType.TABLE,
            generator = "BOARD_SEQ_GENERATOR"
    )
    private int id;
    private String data;
}
