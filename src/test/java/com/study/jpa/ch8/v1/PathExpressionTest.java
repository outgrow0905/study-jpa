package com.study.jpa.ch8.v1;

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
class PathExpressionTest {
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
    void pathExpression1() {
        template(manager -> {
            CMemberV1 member1 = new CMemberV1();
            member1.setUsername("name1");
            member1.setAge(10);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
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
                    "select o.member from COrderV1 o",
                            CMemberV1.class)
                .getResultList();
        });

        template(manager -> {
            manager.createQuery("delete from COrderV1 o").executeUpdate();
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
        });
    }

    @Test
    void pathExpression2() {
        template(manager -> {
            // team
            CTeamV1 team1 = new CTeamV1();
            team1.setName("team1");
            manager.persist(team1);

            // member
            CMemberV1 member1 = new CMemberV1();
            member1.setTeam(team1);
            member1.setUsername("name1");
            member1.setAge(10);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
            manager.persist(member2);

            // product
            CProductV1 product1 = new CProductV1();
            product1.setName("product1");
            manager.persist(product1);

            // order
            COrderV1 order1 = new COrderV1();
            order1.setMember(member1);
            order1.setOrderAmount(100);
            order1.setProduct(product1);
            order1.setAddress(new CAddressV1("city1", "street1", "00000"));
            manager.persist(order1);

            COrderV1 order2 = new COrderV1();
            order2.setMember(member1);
            order1.setProduct(product1);
            order2.setOrderAmount(200);
            manager.persist(order2);
        });

        template(manager -> {
            List<CTeamV1> teams = manager.createQuery(
                            "select o.member.team from COrderV1 o where o.product.name='product1' and o.address.city='city1'",
                            CTeamV1.class)
                    .getResultList();
            log.info("team size: {}", teams.size());
        });

        template(manager -> {
            manager.createQuery("delete from COrderV1 o").executeUpdate();
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
            manager.createQuery("delete from CProductV1 p").executeUpdate();
            manager.createQuery("delete from CTeamV1 t").executeUpdate();
        });
    }

    @Test
    void pathExpression3() {
        template(manager -> {
            // team
            CTeamV1 team1 = new CTeamV1();
            team1.setName("team1");
            manager.persist(team1);

            // member
            CMemberV1 member1 = new CMemberV1();
            member1.setTeam(team1);
            member1.setUsername("name1");
            member1.setAge(10);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
            manager.persist(member2);

            // product
            CProductV1 product1 = new CProductV1();
            product1.setName("product1");
            manager.persist(product1);

            // order
            COrderV1 order1 = new COrderV1();
            order1.setMember(member1);
            order1.setOrderAmount(100);
            order1.setProduct(product1);
            order1.setAddress(new CAddressV1("city1", "street1", "00000"));
            manager.persist(order1);

            COrderV1 order2 = new COrderV1();
            order2.setMember(member1);
            order1.setProduct(product1);
            order2.setOrderAmount(200);
            manager.persist(order2);
        });

        template(manager -> {
            List<CTeamV1> teams = manager.createQuery(
                            "select o.member.team from COrderV1 o inner join o.product inner join o.member inner join o.member.team where o.product.name='product1' and o.address.city='city1'",
                            CTeamV1.class)
                    .getResultList();
            log.info("team size: {}", teams.size());
        });

        template(manager -> {
            manager.createQuery("delete from COrderV1 o").executeUpdate();
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
            manager.createQuery("delete from CProductV1 p").executeUpdate();
            manager.createQuery("delete from CTeamV1 t").executeUpdate();
        });
    }

    @Test
    void pathExpression4() {
        template(manager -> {
            // team
            CTeamV1 team1 = new CTeamV1();
            team1.setName("team1");
            manager.persist(team1);

            // member
            CMemberV1 member1 = new CMemberV1();
            member1.setTeam(team1);
            member1.setUsername("name1");
            member1.setAge(10);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
            manager.persist(member2);

            // product
            CProductV1 product1 = new CProductV1();
            product1.setName("product1");
            manager.persist(product1);

            // order
            COrderV1 order1 = new COrderV1();
            order1.setMember(member1);
            order1.setOrderAmount(100);
            order1.setProduct(product1);
            order1.setAddress(new CAddressV1("city1", "street1", "00000"));
            manager.persist(order1);

            COrderV1 order2 = new COrderV1();
            order2.setMember(member1);
            order1.setProduct(product1);
            order2.setOrderAmount(200);
            manager.persist(order2);
        });

        template(manager -> {
            List<String> names = manager.createQuery(
                            "select m.username from CTeamV1 c inner join c.members m",
                            String.class)
                    .getResultList();
            log.info("names size: {}", names.size());
        });

        template(manager -> {
            manager.createQuery("delete from COrderV1 o").executeUpdate();
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
            manager.createQuery("delete from CProductV1 p").executeUpdate();
            manager.createQuery("delete from CTeamV1 t").executeUpdate();
        });
    }
}