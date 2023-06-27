package com.study.jpa.ch1.v1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.persistence.*;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
class HelloJpaTest {

    EntityManagerFactory factory;

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("jpabook");
        template(
                manager -> {
                    manager.createQuery("DELETE FROM MemberV1 m").executeUpdate();
                }
        );
    }

    @AfterEach
    void close() {
        factory.close();
    }

    @Test
    void helloJpa() {
        template(this::hello);
    }

    @Test
    void persistenceContextFindInCache() {
        template(this::findInCache);
    }

    @Test
    void persistenceContextFindInDatabase() {
        template(this::findInDatabase);
    }

    @Test
    void equals() {
        template(this::equals);
    }

    @Test
    void dirtyCheckingThrowsException() {
        template(this::insertMember1);
        assertThrows(RollbackException.class, () -> template(this::updateMemberViolationFail)) ;
    }

    @Test
    void dirtyCheckingSuccess() {
        template(this::insertMember1);
        template(this::updateMemberSuccess);
    }

    @Test
    void writeBehind() {
        template(this::writeBehind);
    }

    @Test
    void removeFromPersistenceContext() {
        template(this::removeFromPersistenceContext);
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

    private void hello(EntityManager manager) {
        MemberV1 member = new MemberV1();
        member.setId("id1");
        member.setUsername("name1");
        member.setAge(20);

        // insert
        manager.persist(member);

        // select
        MemberV1 findMember = manager.find(MemberV1.class, "id1");
        log.info("member1: {}", findMember);

        // update
        member.setAge(21);

        // select
        findMember = manager.find(MemberV1.class, "id1");
        log.info("member2: {}", findMember);

        // select list
        List<MemberV1> memberV1List = manager.createQuery("SELECT m FROM MemberV1 m", MemberV1.class).getResultList();
        log.info("member list: {}", memberV1List);

        // delete
        manager.remove(member);
    }

    private void findInCache(EntityManager manager) {
        MemberV1 member = new MemberV1();
        member.setId("id1");
        member.setUsername("name1");
        member.setAge(20);

        // insert
        manager.persist(member);

        // select
        MemberV1 findMember = manager.find(MemberV1.class, "id1");
        log.info("findMember: {}", findMember);
    }

    private void findInDatabase(EntityManager manager) {
        MemberV1 member = new MemberV1();
        member.setId("id1");
        member.setUsername("name1");
        member.setAge(20);

        // insert
        manager.persist(member);

        // select
        MemberV1 findMember = manager.find(MemberV1.class, "id2");
        log.info("findMember: {}", findMember);
    }

    private void equals(EntityManager manager) {
        MemberV1 member = new MemberV1();
        member.setId("id1");
        member.setUsername("name1");
        member.setAge(20);

        // insert
        manager.persist(member);

        // select
        MemberV1 member1 = manager.find(MemberV1.class, "id1");
        MemberV1 member2 = manager.find(MemberV1.class, "id1");

        log.info("member1: {}", member1.toString());
        log.info("member2: {}", member2.toString());
        log.info("member1 == member2: {}", member1 == member2);
    }

    private void insertMember1(EntityManager manager) {
        MemberV1 member = new MemberV1();
        member.setId("id1");
        member.setUsername("name1");
        member.setAge(20);

        // insert
        manager.persist(member);
    }

    private void updateMemberViolationFail(EntityManager manager) {
        MemberV1 member = new MemberV1();
        member.setId("id1");
        member.setUsername("name2");
        member.setAge(21);

        // insert 가 수행되어 violation fail
        manager.persist(member);
    }

    private void updateMemberSuccess(EntityManager manager) {
        MemberV1 member = manager.find(MemberV1.class, "id1");
        member.setId("id1");
//        member.setUsername("name2");
        member.setAge(21);

        // update
        manager.persist(member);
    }

    private void writeBehind(EntityManager manager) {
        MemberV1 member = new MemberV1();
        member.setId("id1");
        member.setUsername("name1");
        member.setAge(20);

        // insert
        manager.persist(member);

        // update
        member.setAge(21);

        // update
        member.setAge(22);
    }

    private void removeFromPersistenceContext(EntityManager manager) {
        MemberV1 member = new MemberV1();
        member.setId("id1");
        member.setUsername("name1");
        member.setAge(20);

        // insert
        manager.persist(member);

        // select
        MemberV1 findMember1 = manager.find(MemberV1.class, "id1");
        log.info("findMember1: {}", findMember1);

        // delete
        manager.remove(findMember1);

        // select
        MemberV1 findMember2 = manager.find(MemberV1.class, "id1");
        log.info("findMember2: {}", findMember2);
    }
}