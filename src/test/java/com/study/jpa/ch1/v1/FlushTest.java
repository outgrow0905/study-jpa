package com.study.jpa.ch1.v1;

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

@Slf4j
public class FlushTest {

    EntityManagerFactory factory;

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("jpabook");
        templateOnlyCommitWithoutFlush(
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
    void flushWithoutCommit() {
        templateOnlyFlushWithoutCommit(
                (manager) -> {
                    MemberV1 member = new MemberV1();
                    member.setId("id1");
                    member.setUsername("name1");
                    member.setAge(20);

                    // insert
                    manager.persist(member);
                }
        );

        templateOnlyFlushWithoutCommit(
                (manager) -> {
                    MemberV1 findMember = manager.find(MemberV1.class, "id1");
                    log.info("findMember: {}", findMember);
                }
        );
    }

    @Test
    void commitWithoutFlush() {
        templateOnlyCommitWithoutFlush(
                (manager) -> {
                    MemberV1 member = new MemberV1();
                    member.setId("id1");
                    member.setUsername("name1");
                    member.setAge(20);

                    // insert
                    manager.persist(member);
                }
        );

        templateOnlyCommitWithoutFlush(
                (manager) -> {
                    MemberV1 findMember = manager.find(MemberV1.class, "id1");
                    log.info("findMember: {}", findMember);
                }
        );
    }

    @Test
    void jpql() {
        templateOnlyCommitWithoutFlush(
                (manager) -> {
                    MemberV1 member1 = new MemberV1();
                    member1.setId("id1");
                    member1.setUsername("name1");
                    member1.setAge(10);

                    // insert
                    manager.persist(member1);

                    MemberV1 member2 = new MemberV1();
                    member2.setId("id2");
                    member2.setUsername("name2");
                    member2.setAge(20);

                    // insert
                    manager.persist(member2);

                    MemberV1 member3 = new MemberV1();
                    member3.setId("id3");
                    member3.setUsername("name3");
                    member3.setAge(30);

                    // insert
                    manager.persist(member3);

                    MemberV1 findMember = manager.find(MemberV1.class, "id1");
                    log.info("findMember: {}", findMember);

                    // JPQL
                    List<MemberV1> memberV1List = manager.createQuery("SELECT m FROM MemberV1 m WHERE m.id = 'id3'", MemberV1.class).getResultList();
                    log.info("size: {}", memberV1List.size());
                }
        );
    }

    @Test
    void findAfterFlush() {
        EntityManager manager = factory.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();

        try {
            transaction.begin();

            MemberV1 member1 = new MemberV1();
            member1.setId("id1");
            member1.setUsername("name1");
            member1.setAge(10);

            // insert
            manager.persist(member1);

            transaction.commit();

            MemberV1 findMember = manager.find(MemberV1.class, "id1");
            log.info("findMember: {}", findMember);
        } catch (Exception e) {
            log.error("{}", e);
            transaction.rollback();
            throw e;
        } finally {
            manager.close();
        }
    }


    private void templateOnlyFlushWithoutCommit(Consumer<EntityManager> consumer) {
        EntityManager manager = factory.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();

        try {
            transaction.begin();
            consumer.accept(manager);
            manager.flush();
//            transaction.commit();
        } catch (Exception e) {
            log.error("{}", e);
            transaction.rollback();
            throw e;
        } finally {
            manager.close();
        }
    }

    private void templateOnlyCommitWithoutFlush(Consumer<EntityManager> consumer) {
        EntityManager manager = factory.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();

        try {
            transaction.begin();
            consumer.accept(manager);
//            manager.flush();
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
