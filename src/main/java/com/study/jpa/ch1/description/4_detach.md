#### detach
[Persistence Context 예제](2_persistence-context.md)에서 `detach` 상태에 대해 더 알아보자.  
`detach` 상태는 `Persistence Context`에 등록되었다가 해지된 상태를 말한다.  
따라서 `transient` 상태와 별반 다를 것이 없다.  

다만 한가지 차이점은 `Persistence Context` 상태에 `등록되었다가` 해지된 상태이기 때문에 `key` 값이 반드시 있다는 것이다.
예를 들어, [Member Entity](../MemberV1.java)이면  
`transient` 상태라면 `id` 값이 없을 수도 있지만, `detach` 상태이면 `id` 값이 반드시 있다.  



#### detach 상태로 만드는 세가지 방법
`Entity`를 `detach` 상태로 만드는 방법은 세가지가 있다.  

##### detach()
`detach()`는 특정 `Entity`를 `Persistence Context`에서 제거하는 것이다.

~~~java
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
~~~

위의 테스트에서 로그는 `insert` 조차 수행되지 않는다.  
`detach()` 한 뒤에 `commit()`이 수행되면서 `Persistence Context`에 아무런 `Entity`가 없기 때문이다.  
따라서, 아래의 `find()`에서도 `null`이 반환되면서 `assertTrue`를 통과한다.

##### clear()
`clear()`는 `Persistence Context`의 모든 `Entity`를 초기화한다.  

##### close()
`Persistence Context`를 아예 종료한다. 종료시키기 때문에 당연히 특정 `Entity`를 다시 등록할 수도 없다.   
다시 등록하려고 하면 `Session/EntityManager is closed` 오류메시지를 보게 된다.  