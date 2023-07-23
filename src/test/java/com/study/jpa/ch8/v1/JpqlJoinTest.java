package com.study.jpa.ch8.v1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class JpqlJoinTest {
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
    void innerJoin() {
        template(manager -> {
            CTeamV1 team1 = new CTeamV1();
            team1.setName("team1");
            manager.persist(team1);

            CMemberV1 member1 = new CMemberV1();
            member1.setUsername("name1");
            member1.setAge(10);
            member1.setTeam(team1);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
            member2.setTeam(team1);
            manager.persist(member2);
        });

        template(manager -> {
            List<CMemberV1> members = manager.createQuery(
                    "select m from CMemberV1 m inner join m.team t where t.name = :name",
                    CMemberV1.class)
                .setParameter("name", "team1")
                .getResultList();

            members.forEach(member -> {
                assertEquals("team1", member.getTeam().getName());
            });
        });

        template(manager -> {
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
            manager.createQuery("delete from CTeamV1 t").executeUpdate();
        });
    }

    @Test
    void outerJoin() {
        template(manager -> {
            CTeamV1 team1 = new CTeamV1();
            team1.setName("team1");
            manager.persist(team1);

            CMemberV1 member1 = new CMemberV1();
            member1.setUsername("name1");
            member1.setAge(10);
            member1.setTeam(team1);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
//            member2.setTeam(team1); // for outer join test
            manager.persist(member2);
        });

        template(manager -> {
            List<CMemberV1> members = manager.createQuery(
                            "select m from CMemberV1 m left join m.team t order by m.age asc",
                            CMemberV1.class)
                    .getResultList();

            assertEquals(2, members.size());
            assertNotNull(members.get(0).getTeam());
            assertNull(members.get(1).getTeam());
        });

        template(manager -> {
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
            manager.createQuery("delete from CTeamV1 t").executeUpdate();
        });
    }

    @Test
    void collectionJoin() {
        template(manager -> {
            CTeamV1 team1 = new CTeamV1();
            team1.setName("team1");
            manager.persist(team1);

            CMemberV1 member1 = new CMemberV1();
            member1.setUsername("name1");
            member1.setAge(10);
            member1.setTeam(team1);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
            member2.setTeam(team1);
            manager.persist(member2);
        });

        template(manager -> {
            List<CMemberV1> members = manager.createQuery(
                            "select m from CTeamV1 t left join t.members m",
                            CMemberV1.class)
                    .getResultList();

            assertEquals(2, members.size());
        });

        template(manager -> {
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
            manager.createQuery("delete from CTeamV1 t").executeUpdate();
        });
    }

    @Test
    void thetaJoin() {
        template(manager -> {
            CTeamV1 team1 = new CTeamV1();
            team1.setName("name1");
            manager.persist(team1);

            CMemberV1 member1 = new CMemberV1();
            member1.setUsername("name1");
            member1.setAge(10);
            member1.setTeam(team1);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
            member2.setTeam(team1);
            manager.persist(member2);
        });

        template(manager -> {
            CMemberV1 member = manager.createQuery(
                            "select m from CMemberV1 m, CTeamV1 t where m.username = t.name",
                            CMemberV1.class)
                    .getSingleResult();

            assertEquals("name1", member.getUsername());
        });

        template(manager -> {
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
            manager.createQuery("delete from CTeamV1 t").executeUpdate();
        });
    }

    @Test
    void fetchJoin() {
        template(manager -> {
            CTeamV1 team1 = new CTeamV1();
            team1.setName("team1");
            manager.persist(team1);

            CMemberV1 member1 = new CMemberV1();
            member1.setUsername("name1");
            member1.setAge(10);
            member1.setTeam(team1);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
            member2.setTeam(team1);
            manager.persist(member2);
        });

        template(manager -> {
            List<CMemberV1> members = manager.createQuery(
                            "select m from CMemberV1 m join fetch m.team",
                            CMemberV1.class)
                    .getResultList();

            members.forEach(member -> {
                assertEquals("team1", member.getTeam().getName());
            });
        });

        template(manager -> {
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
            manager.createQuery("delete from CTeamV1 t").executeUpdate();
        });
    }

    @Test
    void collectionFetchJoin() {
        template(manager -> {
            CTeamV1 team1 = new CTeamV1();
            team1.setName("team1");
            manager.persist(team1);

            CMemberV1 member1 = new CMemberV1();
            member1.setUsername("name1");
            member1.setAge(10);
            member1.setTeam(team1);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
            member2.setTeam(team1);
            manager.persist(member2);
        });

        template(manager -> {
            List<CTeamV1> teams = manager.createQuery(
                            "select t from CTeamV1 t join fetch t.members",
                            CTeamV1.class)
                    .getResultList();

            teams.forEach(team -> {
                log.info("team name: {}", team.getName());
                log.info("team members size: {}", team.getMembers().size());
            });

            assertEquals(teams.get(0), teams.get(1));
            assertEquals(teams.get(0).getMembers(), teams.get(1).getMembers());
        });

        template(manager -> {
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
            manager.createQuery("delete from CTeamV1 t").executeUpdate();
        });
    }

    @Test
    void collectionDistinctFetchJoin() {
        template(manager -> {
            CTeamV1 team1 = new CTeamV1();
            team1.setName("team1");
            manager.persist(team1);

            CMemberV1 member1 = new CMemberV1();
            member1.setUsername("name1");
            member1.setAge(10);
            member1.setTeam(team1);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
            member2.setTeam(team1);
            manager.persist(member2);
        });

        template(manager -> {
            List<CTeamV1> teams = manager.createQuery(
                            "select distinct t from CTeamV1 t join fetch t.members",
                            CTeamV1.class)
                    .getResultList();

            assertEquals(1, teams.size());
        });

        template(manager -> {
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
            manager.createQuery("delete from CTeamV1 t").executeUpdate();
        });
    }
}