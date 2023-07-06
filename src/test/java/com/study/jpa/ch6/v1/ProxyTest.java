package com.study.jpa.ch6.v1;

import com.study.jpa.ch6.v2.HelloMemberV2;
import com.study.jpa.ch6.v2.HelloTeamV2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class ProxyTest {


    EntityManagerFactory factory;

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("jpabook");
        template(
                manager -> {
                    manager.createQuery("DELETE FROM HelloMemberV1 m").executeUpdate();
                    manager.createQuery("DELETE FROM HelloTeamV1 t").executeUpdate();
                    manager.createQuery("DELETE FROM HelloMemberV2 m").executeUpdate();
                    manager.createQuery("DELETE FROM HelloTeamV2 t").executeUpdate();
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
            HelloTeamV1 team = new HelloTeamV1();
            team.setName("team1");
            manager.persist(team);

            HelloMemberV1 member = new HelloMemberV1();
            member.setUsername("member1");
            member.setTeam(team);
            manager.persist(member);
        });

        template(manager -> {
            HelloMemberV1 member = manager.find(HelloMemberV1.class, "member1");
            log.info("name: {}", member.getUsername());
        });
    }

    @Test
    void proxy1() {
        template(manager -> {
            HelloTeamV1 team = new HelloTeamV1();
            team.setName("team1");
            manager.persist(team);

            HelloMemberV1 member = new HelloMemberV1();
            member.setUsername("member1");
            member.setTeam(team);
            manager.persist(member);
        });

        template(manager -> {
            HelloMemberV1 member = manager.getReference(HelloMemberV1.class, "member1");
            log.info("name: {}", member.getUsername());
        });
    }

    @Test
    void proxy2() {
        template(manager -> {
            HelloTeamV1 team = new HelloTeamV1();
            team.setName("team1");
            manager.persist(team);

            HelloMemberV1 member = new HelloMemberV1();
            member.setUsername("member1");
            member.setAddress("address1");
            member.setTeam(team);
            manager.persist(member);
        });

        template(manager -> {
            HelloMemberV1 member = manager.getReference(HelloMemberV1.class, "member1");
            log.info("name: {}", member.getAddress());
        });
    }

    @Test
    void proxy3() {
        template(manager -> {
            HelloTeamV1 team = new HelloTeamV1();
            team.setName("team1");
            team.setAddress("address1");
            manager.persist(team);

            HelloMemberV1 member = new HelloMemberV1();
            member.setUsername("member1");
            member.setAddress("address1");
            member.setTeam(team);
            manager.persist(member);
        });

        template(manager -> {
            HelloTeamV1 team = manager.getReference(HelloTeamV1.class, "team1");
            assertFalse(manager.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(team));
            log.info("name: {}", team.getAddress());
            assertTrue(manager.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(team));
            log.info("persistent: {}", team.getMembers().getClass().getName());
        });
    }

    @Test
    void proxy4() {
        template(manager -> {
            HelloTeamV2 team = new HelloTeamV2();
            team.setName("team1");
            team.setAddress("address1");
            manager.persist(team);

            HelloMemberV2 member = new HelloMemberV2();
            member.setUsername("member1");
            member.setAddress("address1");
            member.setTeam(team);
            manager.persist(member);
        });

        template(manager -> {
            HelloMemberV2 member = manager.getReference(HelloMemberV2.class, "member1");
            log.info("name: {}", member.getAddress());
        });
    }

    @Test
    void proxy5() {
        template(manager -> {
            HelloTeamV2 team = new HelloTeamV2();
            team.setName("team1");
            team.setAddress("address1");
            manager.persist(team);

            HelloMemberV2 member = new HelloMemberV2();
            member.setUsername("member1");
            member.setAddress("address1");
            member.setTeam(team);
            manager.persist(member);
        });

        template(manager -> {
            HelloTeamV2 team = manager.getReference(HelloTeamV2.class, "team1");
            log.info("name: {}", team.getAddress());
        });
    }
}