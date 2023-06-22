#### 복합 키 매핑
`복합키`를 가진 테이블과의 매핑전략은 여러가지가 있다.  
크게는 `식별관계`와 `비식별관계`로 나누어볼 수 있다.  

##### 식별관계와 비식별관계
`parent` 테이블이 `p_id1, p_id2`를 `복합키`로 있다고 가정해보자.  
이와 관계를 맺는 `child` 테이블에서 `p_id1, p_id2 그리고 c_id1` 총 세가지로 `복합키`를 구성한다면 이를 `식별관계`라고 한다.  
여기서 `p_id1, p_id2`는 `복합키`의 구성이 됨과 동시에 `외래키`가 된다.  

다시 `parent` 테이블이 `p_id1, p_id2`를 `복합키`로 있다고 가정해보자.    
이와 관계를 맺는 `chlid` 테이블에서 `c_id1` 하나로 `필수키`를 구성하고, `p_id1, p_id2`는 외래키로 구성한다면 이는 비식별관계이다.  
그리고 이 `비식별관계`는 `p_id1, p_id2` 두 값을 `not null`로 지정하는지 여부에 따라 `필수적 혹은 선택적 비식별관계`로 다시 분류할 수 있다.  
`p_id1, p_id2`를 `not null`로 지정한다면 `필수적 비식별관계`이고, 그렇지 않다면 `선택적 비식별관계`이다.  
`비식별관계`에서 알아두어야 할 점은 엔티티 관점에서는 `필수적 비식별관계`와 `선택적 비식별관계`에 따라 달라지는 것이 없다는 것이다.   
이것은 오로지 데이터베이스 설계에 따른 개념이다.


#### 복합키
`복합키` 매핑을 공부하기 전에 먼저 `복합키`를 JPA에서 어떻게 사용해야하는 지부터 알아보자.  
JPA에서 `복합키`는 편의에 따라 두 가지 방법으로 사용할수 있다.  

##### @IdClass
`@IdClass` 어노테이션을 이용하여 `복합키`를 구성해보자.  
`ParentV1` 엔티티만 봤을떄에 특별한 부분은 `@IdClass(ParentV1Id.class)` 부분밖에 없다.  
`ParentV1Id`를 살펴보자.  

`@IdClass`를 사용하려면 몇가지를 필수구현사항이 있다.  
먼저 `Serializable`를 구현해야 한다.    
그리고 이를 사용하는 엔티티 (여기서는 `ParentV1`)에서 `@Id`가 붙은 멤버변수들을 그대로 이름변경없이 똑같이 멤버변수로 가져야 한다.  
그리고 기본생성자가 있어야 한다. 아래의 예시에서는 편의를 위해 `@AllArgsConstructor`를 추가하였고,  
이를 추가하게되면 기본생성자가 자동으로 생성되지 않으므로 `@NoArgsConstructor`까지 같이 추가하였다.  

그리고 `hashcode()`와 `equals()`를 구현해야 한다.  
왜 그래야 할까?  
[Persistence Context](../../../ch1/v1/description/v1-2_persistence-context.md)에서 영속성 컨텍스트는 일종의 `map`구조와 같다고 하였다.  
`key`와 엔티티를 저장하는 구조인데, `복합키`를 사용하는 엔티티는 `key`를 넣을떄에 복합키 여러개를 넣을 수 없다.  
따라서 `equals()`와 `hashcode()`를 사용하여 `key`를 생성하고 이를 영속성 컨텍스트에 저장하는 것이다.  
자세한 부분은 뒤에서 다루도록 하자.  

여러 조건을 만족하는 코드는 아래와 같다.

~~~java
@Entity
@IdClass(ParentV1Id.class)
public class ParentV1 {
    @Id
    @Column(name = "PARENT_ID1")
    private int parentId1;

    @Id
    @Column(name = "PARENT_ID2")
    private int parentId2;

    private String name;
}

@AllArgsConstructor
@NoArgsConstructor
public class ParentV1Id implements Serializable {
    private int parentId1; // ParentV1의 @Id의 변수명과 동일해야 한다.
    private int parentId2; // ParentV1의 @Id의 변수명과 동일해야 한다.

    @Override
    public boolean equals(Object o) {
        ...
    }

    @Override
    public int hashCode() {
        ...
    }
}
~~~

엔티티를 저장하고 다시 조회하는 테스트코드는 아래와 같다.

~~~java
@Test
void idClass() {
    template(manager -> {
        // insert
        ParentV1 parent1 = new ParentV1();
        parent1.setParentId1(1);
        parent1.setParentId2(1);
        parent1.setName("parent1");
        manager.persist(parent1);
    });

    template(manager -> {
        // select
        ParentV1Id parentId1 = new ParentV1Id(1, 1);
        ParentV1 parent1 = manager.find(ParentV1.class, parentId1);
        assertEquals("parent1", parent1.getName());
    });
}
~~~

##### @IdClass 자식클래스
위의 `ParentV1`의 자식클래스를 만들어보자.  
`비식별관계`로 구현할 것이다.  

~~~java
@Entity
public class ChildV1 {
    @Id
    @Column(name = "CHILD_ID1")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int childId1;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID1", referencedColumnName = "PARENT_ID1"),
            @JoinColumn(name = "PARENT_ID2", referencedColumnName = "PARENT_ID2")
    })
    private ParentV1 parent;
}
~~~

테스트코드는 아래와 같다.

~~~java
@Test
void idClassChild() {
    template(manager -> {
        // insert parent
        ParentV1 parent1 = new ParentV1();
        parent1.setParentId1(1);
        parent1.setParentId2(1);
        parent1.setName("parent1");
        manager.persist(parent1);

        // insert child
        ChildV1 child1 = new ChildV1();
        child1.setParent(parent1);
        manager.persist(child1);
    });

    template(manager -> {
        // select
        ParentV1Id parentId1 = new ParentV1Id(1, 1);
        ParentV1 parent1 = manager.find(ParentV1.class, parentId1);
        assertEquals("parent1", parent1.getName());

        ChildV1 child1 = manager.find(ChildV1.class, 1);
        assertEquals(parent1, child1.getParent());
    });
}
~~~


##### @EmbeddedId
`복합키`를 구성하는 또다른 방법은 `@EmbeddedId` 어노테이션을 이용하는 것이다.  
어떤것이 다른지 먼저 `Parent` 엔티티부터 보자.  

~~~java
@Entity
public class ParentV2 {
    @EmbeddedId
    private ParentV2Id id;

    private String name;
}

@Embeddable
public class ParentV2Id implements Serializable {
    @Column(name = "PARENT_ID1")
    private int parentId1;

    @Column(name = "PARENT_ID2")
    private int parentId2;

    @Override
    public boolean equals(Object o) {
        ...
    }

    @Override
    public int hashCode() {
        ...
    }
}
~~~

뭔가 간결해진 것 같다.  
`@IdClass` 어노테이션도 업어졌고, 무엇보다 복합키 자체를 `ParentV2Id` 객체로 참조하고 있다.  
`@IdClass`는 데이터베이스 테이블 설계와 엔티티를 똑같이 가져가야 했다. 마치 [App v1](../../../app/v1/description/app1.md)의 설계와 비슷하다.  
`@EmbeddedId`는 조금 더 객체지향적인 것처럼 느껴진다.  
`ParentV2Id`는 `@Column`을 추가한 것정도 변경되었다.   



##### equals(), hashcode()
`복합키` 객체를 만들떄에는 반드시 `equals(), hashcode()`를 오버라이드 해야한다.  
이는 `영속성 컨텍스트`에 `엔티티`를 저장할 키가 되기 때문이다.  
이를 적절히 오버라이드하지 않으면 기본적으로 `Object`의 `equals()`를 사용하게 된다.  
`Object`의 `equals`는 기본적으로 `==` 비교를 한다. 다른 표현으로 `동등성` 비교이며, 또 다른 의미로는 `메모리주소`값을 비교한다는 것이다.  

테스트를 해보자.

##### 잘못된 예시
~~~java
@Override
public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ParentV2Id that = (ParentV2Id) o;
//        return parentId1 == that.parentId1 && parentId2 == that.parentId2;
    return super.equals(o);
}
~~~
위의 예시대로 `복합키` 객체에서 `equals()`를 정의했다면, 아래 테스트결과는 `false`이고,  
위의 예시에서 주석대로 동등성비교가 아닌 동일성 비료를 헀다면 아래 테스트결과는 `true`이다.
~~~java
@Test
void equalsAndHashCode() {
    ParentV2Id id1 = new ParentV2Id(1, 1);
    ParentV2Id id2 = new ParentV2Id(1, 1);
    log.info("equals: {}", id1.equals(id2));
}
~~~

복합키를 가진 엔티티이더라도 키가 같다면 결국 하나의 엔티티만 `영속성 컨텍스트`에 저장되어야 한다.  
이를 가능하게 하려면 반드시 `equals()와 hashcode()` 메서드가 `동일성` 비교를 하도록 적절히 오버라이드해야 한다.  