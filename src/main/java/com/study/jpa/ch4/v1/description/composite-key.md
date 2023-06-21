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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParentV1Id that = (ParentV1Id) o;
        return parentId1 == that.parentId1 && parentId2 == that.parentId2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentId1, parentId2);
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