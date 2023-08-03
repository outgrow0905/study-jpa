#### 영속성 컨텍스트와 jpql
jpql을 마무리할 떄가 되었다.  
마지막으로 jpql을 사용하면서 영속성 컨텍스트와 어떻게 상호작용하는 지 주의할 점을 알아보면서 마무리하자.  


#### jpql 쿼리 후 영속상태 관리여부
~~~sql
select m from Member m
select o.address from Order o
select m.id, m.username from Member m
~~~
위의 jpql문 3개를 살펴보자.  
첫번째 jpql은 영속성 컨텍스트에 의해 관리된다.  
두번쨰와 세번째는 관리되지 않는다.  
두번째 jpql의 `address`는 엔티티가 아닌 단순 임베디드 타입이라서 그렇다.  
세번째는 단순 필드 조회이기 때문에 엔티티의 키를 기준으로 관리되는 영속성 컨텍스트에 등록이 안될 것이다.



#### find()와 jpql 동시실행 시 영속성 컨텍스트 관리방식
~~~java
// member1 조회
CMemberV1 findMember1 = manager.find(CMemberV1.class, member1.getId());

// member1, member2 조회
List<CMemberV1> members = manager.createQuery(
        "select m from CMemberV1 m",
        CMemberV1.class
).getResultList();
~~~
영속성 컨텍스트 관점에서 위의 코드를 살펴보자.  
`find()`에 의해 영속성 컨텍스트에는 `member1`이 등록될 것이다.  
그리고 아래의 jpql문에 의해 `member1, member2`가 조회될 것이다.  
그러면 `find()`에 의해 영속성 컨텍스트에 등록된 `member1`은 뒤에 실행된 jpql에 의해 대체될까?  
아니면, jpql에 의해 조회된 `member1`은 폐기처리될까?  

폐기처리된다.  

왜그럴까?  

`find()`로 등록된 `member1`이 `flush()`되기 전까지 영속성 컨텍스트에서 수정될 수 있기 때문이다.  
jpa의 기본동작은 jpql이 수행되기 전에 기본적으로 `flush()`를 처리하지만, 사용자 설정에 의해 그렇지 않을 수도 있다.  
따라서, 영속성 컨텍스트에 먼저 등록된 엔티티가 수정중인 데이터를 관리하기 위해 뒤에 jpql에서 불러진 `member1`은 폐기처리된다.  



#### jpql과 flush 모드
`flush()`와 `commit()`의 차이를 초기에 공부했었다. [참조](../../ch1/description/3_flush.md)
중요하게 기억할 점은 jpql 수행전에는 기본적으로 `flush()`를 수행해준다는 부분이다.  
왜 그렇게 작동할까?  
jpql은 무조건적으로 데이터베이스를 조회하게 되는데, 만약 영속성 컨텍스트에서 변경된 부분이 미리 반영되지 않으면 개발자가 의도치 않게 데이터가 조회될 수 있다.  
예시를 보자.

~~~java
// member1 조회
CMemberV1 findMember1 = manager.find(CMemberV1.class, member1.getId());
findMember1.setAge(20); // 10 -> 20

// member1 jpql 조회
CMemberV1 findMemberByJpql = manager.createQuery(
    "select m from CMemberV1 m",
    CMemberV1.class
).getSingleResult();
log.info("findMemberByJpql age: {}", findMemberByJpql.getAge());
~~~

jpql로 `member`를 조회하기전에 `commit()`까지 이루어지지 않는다면 로그에는 `20`이 아닌 `10`이 조회될 것이다.  
jpql은 무조건 데이터베이스를 조회하기 때문이다.  
따라서 일반적으로 개발자가 의도한대로 작동하기 위해 jpa의 기본모드는 jpql 수행전에 `flush()`를 수행한다.  

테스트를 위해 기본모드가 아닌 `COMMIT` 모드로 오작동을 테스트해보자.

~~~java
manager.setFlushMode(FlushModeType.COMMIT);

// member1 조회
CMemberV1 findMember1 = manager.find(CMemberV1.class, member1.getId());
findMember1.setAge(20); // 10 -> 20

// member1 jpql 조회
assertThrows(
    NoResultException.class,
    () -> {
        manager.createQuery(
                "select m from CMemberV1 m where m.age = 20",
                CMemberV1.class
        ).getSingleResult();
});
~~~
`FlushModeType.COMMIT`이기 때문에 jpql이 수행되기 전에 데이터베이스에 `20`이 세팅되지 않는다.  
따라서, jpql에서 `20`을 기준으로 조회해도 조회되는 값이 없게 된다.  

기본모드를 쓰는것이 안전하고 편리함에도 가끔은 최적화를 위해 `FlushModeType.COMMIT`을 사용해야 할때가 있을 수 있다.  
위의 예시처럼 등록한 엔티티를 변경한 데이터로 조회하는 경우는 무조건 `AUTO`를 사용해야 한다.  

하지만, 변경한 데이터를 조건으로 조회하지 않는다면 jpql 수행전에 `flush()`를 할 필요는 사실 없다.  
데이터를 변경하고 jpql을 수행하더라도 영속성 컨텍스트에는 변경한 데이터가 잘 들어있을 것이기 때문이다.  
하지만, 가끔은 성능최적화를 위해 `FlushModeType.COMMIT`를 사용해야 할 때가 있을 것이고 그럴 때에는 주의깊게 사용하길 권장한다.


