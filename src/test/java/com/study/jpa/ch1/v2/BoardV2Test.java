package com.study.jpa.ch1.v2;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class BoardV2Test {
    EntityManagerFactory factory;

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("jpabook");
    }

    @AfterEach
    void close() {
        factory.close();
    }


    @Test
    void insert() {
        template((manager) -> {
            BoardV2 board1 = new BoardV2();
            board1.setData("data1");
            manager.persist(board1);
            BoardV2 findBoard1 = manager.find(BoardV2.class, 1);
            log.info("findBoard1: {}", findBoard1);

            BoardV2 board2 = new BoardV2();
            board2.setData("data2");
            manager.persist(board2);
            BoardV2 findBoard2 = manager.find(BoardV2.class, 2);
            log.info("findBoard2: {}", findBoard2);
        });
    }

    private void template(Consumer<EntityManager> consumer) {
        EntityManager manager = factory.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();

        try {
            transaction.begin();
            consumer.accept(manager);
            transaction.commit();
        } catch (Exception e) {
            log.error("{}", e);
            transaction.rollback();
            throw e;
        } finally {
            manager.close();
        }
    }
}