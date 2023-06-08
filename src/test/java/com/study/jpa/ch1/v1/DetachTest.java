package com.study.jpa.ch1.v1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Objects;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;


@Slf4j
public class DetachTest {
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
    void detach() {
        template(manager -> {
            MemberV1 member = new MemberV1();
            member.setId("id1");
            member.setUsername("name1");
            member.setAge(20);

            // insert
            manager.persist(member);

            // detach
            manager.detach(member);

            return member;
        });

        template(manager -> {
            MemberV1 member = manager.find(MemberV1.class, "id1");
            assertTrue(Objects.isNull(member));

            return member;
        });
    }

    @Test
    void merge() {
        MemberV1 member1 = template(manager -> {
            MemberV1 member = new MemberV1();
            member.setId("id1");
            member.setUsername("name1");
            member.setAge(20);

            // insert
            manager.persist(member);

            return member;
        });

        template(manager -> {
            log.info("detached: {}", manager.contains(member1));
            member1.setUsername("name2");
            MemberV1 mergedMember = manager.merge(member1);
            log.info("merged: {}", manager.contains(mergedMember));
            return mergedMember;
        });
    }

    private MemberV1 template(Function<EntityManager, MemberV1> function) {
        EntityManager manager = factory.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();

        MemberV1 result;
        try {
            transaction.begin();
            result = function.apply(manager);
            transaction.commit();
        } catch (Exception e) {
            log.error("{}", e);
            transaction.rollback();
            throw e;
        } finally {
            manager.close();
        }

        return result;
    }
}
