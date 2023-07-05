package com.study.jpa.ch6.v1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ProxyTest {


    EntityManagerFactory factory;

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("jpabook");
        template(
                manager -> {
                    manager.createQuery("DELETE FROM HelloMember m").executeUpdate();
                    manager.createQuery("DELETE FROM HelloTeam t").executeUpdate();
                }
        );
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
    void uselessJoin() {
        template(manager -> {
            HelloTeam team = new HelloTeam();
            team.setName("team1");
            manager.persist(team);

            HelloMember member = new HelloMember();
            member.setUsername("member1");
            member.setTeam(team);
            manager.persist(member);
        });

        template(manager -> {
            HelloMember member = manager.find(HelloMember.class, "member1");
            log.info("name: {}", member.getUsername());
        });
    }

    @Test
    void proxy1() {
        template(manager -> {
            HelloTeam team = new HelloTeam();
            team.setName("team1");
            manager.persist(team);

            HelloMember member = new HelloMember();
            member.setUsername("member1");
            member.setTeam(team);
            manager.persist(member);
        });

        template(manager -> {
            HelloMember member = manager.getReference(HelloMember.class, "member1");
            log.info("name: {}", member.getUsername());
        });
    }

    @Test
    void proxy2() {
        template(manager -> {
            HelloTeam team = new HelloTeam();
            team.setName("team1");
            manager.persist(team);

            HelloMember member = new HelloMember();
            member.setUsername("member1");
            member.setAddress("address1");
            member.setTeam(team);
            manager.persist(member);
        });

        template(manager -> {
            HelloMember member = manager.getReference(HelloMember.class, "member1");
            log.info("name: {}", member.getAddress());
        });
    }

    @Test
    void proxy3() {
        template(manager -> {
            HelloTeam team = new HelloTeam();
            team.setName("team1");
            team.setAddress("address1");
            manager.persist(team);

            HelloMember member = new HelloMember();
            member.setUsername("member1");
            member.setAddress("address1");
            member.setTeam(team);
            manager.persist(member);
        });

        template(manager -> {
            HelloTeam team = manager.getReference(HelloTeam.class, "team1");
            log.info("isProxy: {}", manager.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(team));
            log.info("name: {}", team.getAddress());
            log.info("isProxy: {}", manager.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(team));
        });
    }
}