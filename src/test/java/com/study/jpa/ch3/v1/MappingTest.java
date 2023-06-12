package com.study.jpa.ch3.v1;

import com.study.jpa.ch1.v1.MemberV1;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
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
class MappingTest {

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
    void find() {
        template(manager -> {
            TeamV1 team = new TeamV1();
            team.setId("team1");
            team.setName("team Seoul");
            manager.persist(team);

            PlayerV1 player1 = new PlayerV1();
            player1.setId("player1");
            player1.setTeam(team);
            player1.setName("name1");
            manager.persist(player1);

            PlayerV1 player2 = new PlayerV1();
            player2.setId("player2");
            player2.setTeam(team);
            player2.setName("name2");
            manager.persist(player2);
        });

        template(manager -> {
            PlayerV1 player1 = manager.find(PlayerV1.class, "player1");
            log.info("player1: {}", player1);
            TeamV1 team1 = player1.getTeam();
            log.info("team1: {}", team1);
        });

        template(manager -> {
            TeamV1 team1 = manager.find(TeamV1.class, "team1");
            List<PlayerV1> players = team1.getPlayers();
            log.info("players: {}", players);
        });
    }

    @Test
    void findByJpql() {
        template(manager -> {
            TeamV1 team = new TeamV1();
            team.setId("team1");
            team.setName("team Seoul");
            manager.persist(team);

            PlayerV1 player1 = new PlayerV1();
            player1.setId("player1");
            player1.setTeam(team);
            player1.setName("name1");
            manager.persist(player1);

            PlayerV1 player2 = new PlayerV1();
            player2.setId("player2");
            player2.setTeam(team);
            player2.setName("name2");
            manager.persist(player2);
        });

        template(manager -> {
            List<PlayerV1> players =
                    manager.createQuery("select p from PlayerV1 p join p.team t where t.name = :teamName", PlayerV1.class)
                            .setParameter("teamName", "team Seoul")
                            .getResultList();
            log.info("players: {}", players);
        });
    }

    @Test
    void update() {
        template(manager -> {
            TeamV1 team = new TeamV1();
            team.setId("team1");
            team.setName("team Seoul");
            manager.persist(team);

            PlayerV1 player1 = new PlayerV1();
            player1.setId("player1");
            player1.setTeam(team);
            player1.setName("name1");
            manager.persist(player1);

            PlayerV1 player2 = new PlayerV1();
            player2.setId("player2");
            player2.setTeam(team);
            player2.setName("name2");
            manager.persist(player2);
        });

        template(manager -> {
            TeamV1 team2 = new TeamV1();
            team2.setId("team2");
            team2.setName("team Busan");
            manager.persist(team2);

            PlayerV1 player1 = manager.find(PlayerV1.class, "player1");
            log.info("player1's team: {}", player1.getTeam());

            player1.setTeam(team2);
            log.info("player1's team: {}", player1.getTeam());
        });
    }

    @Test
    void deleteMapping() {
        template(manager -> {
            TeamV1 team = new TeamV1();
            team.setId("team1");
            team.setName("team Seoul");
            manager.persist(team);

            PlayerV1 player1 = new PlayerV1();
            player1.setId("player1");
            player1.setTeam(team);
            player1.setName("name1");
            manager.persist(player1);

            PlayerV1 player2 = new PlayerV1();
            player2.setId("player2");
            player2.setTeam(team);
            player2.setName("name2");
            manager.persist(player2);
        });

        template(manager -> {
            PlayerV1 player1 = manager.find(PlayerV1.class, "player1");
            log.info("player1's team: {}", player1.getTeam());
            player1.setTeam(null);
        });
    }

    @Test
    void oneToMany() {
        template(manager -> {
            TeamV1 team = new TeamV1();
            team.setId("team1");
            team.setName("team Seoul");
            manager.persist(team);

            PlayerV1 player1 = new PlayerV1();
            player1.setId("player1");
            player1.setTeam(team);
            player1.setName("name1");
            manager.persist(player1);

            PlayerV1 player2 = new PlayerV1();
            player2.setId("player2");
            player2.setTeam(team);
            player2.setName("name2");
            manager.persist(player2);
        });

        template(manager -> {
            TeamV1 team1 = manager.find(TeamV1.class, "team1");
            log.info("team1: {}", team1);

            List<PlayerV1> players = team1.getPlayers();
            log.info("players: {}", players);
        });
    }

    @Test
    void mappedBy1() {
        template(manager -> {
            TeamV1 team1 = new TeamV1();
            team1.setId("team1");
            team1.setName("team Seoul");
            manager.persist(team1);

            TeamV1 team2 = new TeamV1();
            team2.setId("team2");
            team2.setName("team Pusan");
            manager.persist(team2);

            PlayerV1 player1 = new PlayerV1();
            player1.setId("player1");
            player1.setTeam(team1);
            player1.setName("name1");
            manager.persist(player1);

            PlayerV1 player2 = new PlayerV1();
            player2.setId("player2");
            player2.setTeam(team1);
            player2.setName("name2");
            manager.persist(player2);

            TeamV1 findTeam1 = manager.find(TeamV1.class, "team1");
            List<PlayerV1> players = findTeam1.getPlayers();
            log.info("size: {}", players.size());
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