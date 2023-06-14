package com.study.jpa.ch3.v3;

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
public class OneToOneTest {
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
    void insert1() {
        template(manager -> {
            LockerV1 locker1 = new LockerV1();
            locker1.setLocation("location1");
            manager.persist(locker1);

            LockerMemberV1 member1 = new LockerMemberV1();
            member1.setName("name1");
            member1.setLocker(locker1);
            manager.persist(member1);

            log.info("member: {}", member1);
            log.info("locker1: {}", locker1);
        });

        template(manager -> {
            LockerV1 locker1 = manager.find(LockerV1.class, 1);
            log.info("member id: {}", locker1.getMember().getId());
            log.info("member name: {}", locker1.getMember().getName());
            log.info("member == locker: {}", locker1 == locker1.getMember().getLocker());

            LockerMemberV1 member1 = manager.find(LockerMemberV1.class, 1);
            log.info("locker id: {}", member1.getLocker().getId());
            log.info("locker location: {}", member1.getLocker().getLocation());
            log.info("member == locker: {}", member1 == member1.getLocker().getMember());
        });
    }

    @Test
    void insert2() {
        template(manager -> {
            LockerMemberV2 member1 = new LockerMemberV2();
            member1.setName("name1");
            manager.persist(member1);

            LockerV2 locker1 = new LockerV2();
            locker1.setLocation("location1");
            locker1.setMember(member1);
            manager.persist(locker1);

            log.info("member: {}", member1);
            log.info("locker: {}", locker1);
        });

        template(manager -> {
            LockerV2 locker1 = manager.find(LockerV2.class, 1);
            log.info("member id: {}", locker1.getMember().getId());
            log.info("member name: {}", locker1.getMember().getName());
            log.info("member == locker: {}", locker1 == locker1.getMember().getLocker());

            LockerMemberV2 member1 = manager.find(LockerMemberV2.class, 1);
            log.info("locker id: {}", member1.getLocker().getId());
            log.info("locker location: {}", member1.getLocker().getLocation());
            log.info("member == locker: {}", member1 == member1.getLocker().getMember());
        });
    }
}
