package com.study.jpa.config.v1;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.jpa.app.v6.ItemV6;
import com.study.jpa.app.v6.MovieV6;
import com.study.jpa.app.v6.QItemV6;
import com.study.jpa.ch8.v1.*;
import com.study.jpa.ch9.v1.DMemberV1;
import com.study.jpa.ch9.v1.QDMemberV1;
import com.study.jpa.ch9.v2.ItemDTOV1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
class QueryDslTest {
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
    void queryDsl1() {
        template(manager -> {
            JPAQuery<DMemberV1> query = new JPAQuery<>(manager);
            QDMemberV1 member1 = new QDMemberV1("m");
            List<DMemberV1> members = query.from(member1)
                    .where(member1.username.eq("name1"))
                    .orderBy(member1.username.desc())
                    .stream().toList();
        });
    }

    @Test
    void queryDsl2() {
        template(manager -> {
            JPAQuery<ItemV6> query = new JPAQuery<>(manager);
            QItemV6 item1 = new QItemV6("i");
            List<ItemV6> items = query.from(item1)
                    .where(item1.name.eq("item1")
                            .and(item1.price.gt(2000))
                            .and(item1.name.contains("luxury"))
                            .and(item1.name.startsWith("it")))
                    .stream().toList();
        });
    }

    @Test
    void queryDsl3() {
        template(manager -> {
            JPAQuery<ItemV6> query = new JPAQuery<>(manager);
            QItemV6 item1 = new QItemV6("i");
            List<ItemV6> items = query.from(item1)
                    .where(item1.price.gt(2000))
                    .orderBy(item1.price.desc(), item1.stockQuantity.asc())
                    .offset(5).limit(10)
                    .stream().toList();
        });
    }

    @Test
    void queryDsl4() {
        template(manager -> {
            JPAQueryFactory query = new JPAQueryFactory(manager);
            QItemV6 item1 = new QItemV6("i");
            Long count = query.from(item1)
                .select(item1.count())
                .where(item1.price.gt(2000))
                .orderBy(item1.price.desc(), item1.stockQuantity.asc())
                .fetchOne();
        });
    }

    @Test
    void queryDsl5() {
        template(manager -> {
            JPAQueryFactory query = new JPAQueryFactory(manager);
            QItemV6 item1 = new QItemV6("i");
            List<Tuple> hello = query
                    .select(item1.name, item1.price.min(), item1.createdTime.max(), item1.stockQuantity.avg())
                    .from(item1)
                    .where(item1.price.gt(2000))
                    .groupBy(item1.name)
                    .having(item1.name.contains("hello"))
                    .orderBy(item1.name.desc())
                    .fetch();
        });
    }

    @Test
    void queryDsl6() {
        template(manager -> {
            QCMemberV1 member = QCMemberV1.cMemberV1;
            QCOrderV1 order = QCOrderV1.cOrderV1;
            QCProductV1 product = QCProductV1.cProductV1;

            JPAQueryFactory query = new JPAQueryFactory(manager);
            List<Tuple> orders = query.select(order, member)
                    .from(order)
                    .join(order.member, member)
                    .leftJoin(order.product, product)
                    .on(order.product.price.gt(100))
                    .stream().toList();
        });
    }

    @Test
    void queryDslSubquery1() {
        template(manager -> {
            QCMemberV1 member = QCMemberV1.cMemberV1;
            QCTeamV1 team = QCTeamV1.cTeamV1;

            JPAQueryFactory query = new JPAQueryFactory(manager);
            List<CTeamV1> teams = query.selectFrom(team)
                    .where(team.members.contains(
                            JPAExpressions
                                    .selectFrom(member)
                                    .where(member.age.gt(20))

                    )).stream().toList();
        });
    }

    @Test
    void projection1() {
        template(manager -> {
            JPAQueryFactory query = new JPAQueryFactory(manager);
            QItemV6 item1 = QItemV6.itemV6;
            List<String> names =
                    query.select(item1.name)
                            .from(item1)
                            .stream().toList();
        });
    }

    @Test
    void projection2() {
        template(manager -> {
            JPAQueryFactory query = new JPAQueryFactory(manager);
            QItemV6 item1 = QItemV6.itemV6;

            List<Tuple> tuples = query.select(item1.name, item1.price)
                    .from(item1)
                    .stream().toList();
            for(Tuple tuple : tuples) {
                log.info("name: {}", tuple.get(item1.name));
                log.info("price: {}", tuple.get(item1.price));
            }
        });
    }

    @Test
    void projection3() {
        template(manager -> {
            JPAQueryFactory query = new JPAQueryFactory(manager);
            QItemV6 item1 = QItemV6.itemV6;

            List<ItemDTOV1> items =
                    query.select(
                            Projections.bean(ItemDTOV1.class,
                                    item1.name.as("itemName"), // item은 name 이고 DTO는 itemName이므로 alias를 지정하여 맞춰주어야 한다.
                                    item1.price))
                    .from(item1)
                    .stream().toList();
        });
    }

    @Test
    void projection4() {
        template(manager -> {
            JPAQueryFactory query = new JPAQueryFactory(manager);
            QItemV6 item1 = QItemV6.itemV6;

            List<ItemDTOV1> items =
                    query.select(
                                    Projections.constructor(ItemDTOV1.class,
                                            item1.name,
                                            item1.price))
                            .from(item1)
                            .stream().toList();
        });
    }

    @Test
    void dynamic1() {
        // application parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("price", 100);
        parameters.put("name", "name1");

        // set parameters
        QItemV6 item1 = QItemV6.itemV6;
        BooleanBuilder builder = new BooleanBuilder();
        if (parameters.containsKey("name")) {
            builder.and(item1.name.contains((String)parameters.get("name")));
        }
        if (parameters.containsKey("price")) {
            builder.and(item1.price.eq((Integer)parameters.get("price")));
        }

        // query
        template(manager -> {
            JPAQueryFactory query = new JPAQueryFactory(manager);
            List<ItemV6> items =
                    query.selectFrom(item1)
                    .where(builder)
                    .stream().toList();
        });
    }
}