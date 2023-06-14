package com.study.jpa.ch3.v2;

import com.study.jpa.ch2.v1.MemberV2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class OneToManyTest {
    EntityManagerFactory factory;

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("jpabook");
    }

    @AfterEach
    void close() {
        factory.close();
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


    @Test
    void insert() {
        template(manager -> {
            PlayerV2 player1 = new PlayerV2();
            player1.setId("player1");
            player1.setUsername("name1");

            PlayerV2 player2 = new PlayerV2();
            player2.setId("player2");
            player2.setUsername("name2");

            TeamV2 team1 = new TeamV2();
            team1.setId("team1");
            team1.setName("team name1");
            team1.setPlayers(List.of(player1, player2));

            manager.persist(player1);
            manager.persist(player2);
            manager.persist(team1);
        });
    }
}