package com.study.jpa.ch2.v1;

import com.study.jpa.ch2.v1.BoardV2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.function.Consumer;

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
            log.info("board1: {}", board1);

            BoardV2 board2 = new BoardV2();
            board2.setData("data2");
            manager.persist(board2);
            log.info("board2: {}", board2);
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