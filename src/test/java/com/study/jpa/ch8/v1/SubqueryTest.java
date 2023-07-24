package com.study.jpa.ch8.v1;

import com.study.jpa.ch5.v1.MyItemV1;
import com.study.jpa.ch5.v2.MyAlbumV2;
import com.study.jpa.ch5.v2.MyItemV2;
import com.study.jpa.ch5.v2.MyMovieV2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class SubqueryTest {
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
    void subquery1() {
        template(manager -> {
            CTeamV1 teamV1 = new CTeamV1();
            teamV1.setName("team1");
            manager.persist(teamV1);

            CMemberV1 member1 = new CMemberV1();
            member1.setUsername("name1");
            member1.setAge(10);
            member1.setTeam(teamV1);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
            member1.setTeam(teamV1);
            manager.persist(member2);

            COrderV1 order1 = new COrderV1();
            order1.setMember(member1);
            order1.setOrderAmount(100);
            manager.persist(order1);

            COrderV1 order2 = new COrderV1();
            order2.setMember(member1);
            order2.setOrderAmount(200);
            manager.persist(order2);
        });

        template(manager -> {
            List<CMemberV1> members = manager.createQuery(
                    "select m from CMemberV1 m where m.age > (select avg(m2.age) from CMemberV1 m2)",
//                            "select m from CMemberV1 m where (select count(o) from COrderV1 o where m = o.member) > 0",
//                            "select m from CMemberV1 m where exists (select t from m.team t where t.name = 'team1')",
//                            "select o from COrderV1 o where o.orderAmount > ALL(select p.stockAmount from CProductV1 p)",
//                            "select t from CTeamV1 t where t IN (select t2 from CTeamV1 t2 inner join t2.members m2 where m2.age >= 20)",
                            CMemberV1.class)
                .getResultList();

            for (CMemberV1 member : members) {
                log.info("member age: {}", member.getAge());
            }
        });

        template(manager -> {
            manager.createQuery("delete from COrderV1 o").executeUpdate();
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
            manager.createQuery("delete from CTeamV1 t").executeUpdate();
        });
    }

    @Test
    void subquery2() {
        template(manager -> {
            CTeamV1 teamV1 = new CTeamV1();
            teamV1.setName("team1");
            manager.persist(teamV1);

            CMemberV1 member1 = new CMemberV1();
            member1.setUsername("name1");
            member1.setAge(10);
            member1.setTeam(teamV1);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
            member1.setTeam(teamV1);
            manager.persist(member2);

            COrderV1 order1 = new COrderV1();
            order1.setMember(member1);
            order1.setOrderAmount(100);
            manager.persist(order1);

            COrderV1 order2 = new COrderV1();
            order2.setMember(member1);
            order2.setOrderAmount(200);
            manager.persist(order2);
        });

        template(manager -> {
            Integer size = manager.createQuery(
                            "select m.orders.size from CMemberV1 m where m.orders is not empty",
                            Integer.class)
                    .getSingleResult();

            log.info("size: {}", size);
        });

        template(manager -> {
            manager.createQuery("delete from COrderV1 o").executeUpdate();
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
            manager.createQuery("delete from CTeamV1 t").executeUpdate();
        });
    }

    @Test
    void subquery3() {
        template(manager -> {
            MyMovieV2 movie1 = new MyMovieV2();
            movie1.setPrice(100);
            movie1.setName("movie1");
            movie1.setActor("actor1");
            manager.persist(movie1);

            MyAlbumV2 album1 = new MyAlbumV2();
            album1.setPrice(110);
            album1.setName("album1");
            album1.setArtist("artist1");
            manager.persist(album1);
        });

        template(manager -> {
            List<MyItemV2> list = manager.createQuery(
                            "select i from MyItemV2 i where type(i) in (MyAlbumV2)",
                            MyItemV2.class)
                    .getResultList();
        });

        template(manager -> {
            manager.createQuery("delete from MyItemV2 o").executeUpdate();
        });
    }

    @Test
    void subquery4() {
        CMemberV1 member1 = new CMemberV1();
        template(manager -> {
            member1.setUsername("name1");
            manager.persist(member1);
        });

        template(manager -> {
            Long count = manager.createQuery(
                    "select count(m) from CMemberV1 m where m = :member",
                    Long.class)
                    .setParameter("member", member1)
                    .getSingleResult();
            assertEquals(1, count);
        });

        template(manager -> {
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
        });
    }
}