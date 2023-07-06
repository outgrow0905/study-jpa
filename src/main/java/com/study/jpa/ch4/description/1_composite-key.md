#### 식별관계 복합 키 매핑
[composite-key.md](../../description/composite-key.md)에서  
복합키를 구성하는 `@IdClass`와 `@EmbeddedId`를 학습했다.  
예시들이 전부 `비식별관계`였으므로, 이번에는 조금더 까다로운 `식별관계`를 구현해보자.  
 

##### IdClass
`Parent, Child` 엔티티부터 살펴보자.  
기존 비식별관계와 다른점은 `Child`의 `parent`에도 `@Id`가 붙었다.  
식별관계이기 때문에 `PK`에 포함되기 때문이다.  

`ChildV3Id`도 특별할 것 없지만 한가지 주의할 점은 `ChildV3Id` 복합키의 멤버변수의 타입이 테이블생성에 참조된다는 것이다.  
만약 `parent`의 타입을 `String`으로해도 오류는 발생하지 않지만, `Child` 테이블의 `PK`중 하나인 `parentId`는 `varchar` 타입으로 생성된다.  
따라서 식별관계의 엔티티의 `@Id` 타입을 잘 확인할 필요가 있다.
~~~java
@Entity
public class ParentV3 {
    @Id
    @Column(name = "PARENT_ID")
    private int parentId;
    private String name;
}

@IdClass(ChildV3Id.class)
@Entity
public class ChildV3 {
    @Id
    @Column(name = "CHILD_ID")
    private int childId;

    @Id
    @ManyToOne
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "PARENT_ID")
    private ParentV3 parent;
}

@AllArgsConstructor
@NoArgsConstructor
public class ChildV3Id implements Serializable {
    private int parent;
    private int childId;

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

`Child`와 `식별관계`인 `GrandChild`를 생성하고 마무리하자.  
`GrandChildV3Id`에는 `ChildV3Id`가 그대로 참조되어서 `복합키`를 구성하고 있다.  
`복합키`의 구성이 또다른 `복합키`라면 `ChildV3Id`에서 `parentId`의 타입을 주의해야 했던것을 신경쓰지 않아도된다. 

~~~java
@IdClass(GrandChildV3Id.class)
@Entity
public class GrandChildV3 {
    @Id
    @Column(name = "GRAND_CHILD_ID")
    private int grandChildId;

    @Id
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID", referencedColumnName = "PARENT_ID"),
            @JoinColumn(name = "CHILD_ID", referencedColumnName = "CHILD_ID")
    })
    private ChildV3 child;
}

@AllArgsConstructor
@NoArgsConstructor
public class GrandChildV3Id implements Serializable {
    private int grandChildId;
    private ChildV3Id child;

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


##### EmbeddedId
`@EmbeddedId`로 필수관계 복합키를 매핑해보자.  
Parent는 아래와 같이 단순한 엔티티이다.

~~~java
@Entity
public class ParentV4 {
    @Id
    @Column(name = "PARENT_ID")
    private int parentId;
    private String name;
}
~~~

`Child`를 만들어보자. 별거 없으니 `ChildV4Id`를 살펴보자.  
`childId, parentId` 두개가 `Child` 엔티티의 복합키라는 것을 알겠다.  
다만 `parentId`는 `@Column`이 없는대신 `Child` 엔티티에 `@MapsId("parentId")`으로 매핑관계가 명시되고 있다.

~~~java
@Entity
public class ChildV4 {

    @EmbeddedId
    private ChildV4Id childId;

    @MapsId("parentId")
    @ManyToOne
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "PARENT_ID")
    private ParentV4 parent;
}

@Embeddable
public class ChildV4Id implements Serializable {
    @Column(name = "CHILD_ID")
    private int childId;

    private int parentId;

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

`GrandChild`는 아래와 같다.

~~~java
@Entity
public class GrandChildV4 {
    @EmbeddedId
    private GrandChildV4Id grandChildId;

    @MapsId("childId")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID", referencedColumnName = "PARENT_ID"),
            @JoinColumn(name = "CHILD_ID", referencedColumnName = "CHILD_ID")
    })
    private ChildV4 child;
}

@Embeddable
public class GrandChildV4Id implements Serializable {
    @Column(name = "GRAND_CHILD_ID")
    private int grandChildId;
    private ChildV4Id childId;

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



#### conclusion
책에서는 `식별관계`보다는 `비식별관계`를 권장한다.  
`비식별관계`로 하면 `부모, 자식관계`라 할지라도 `자식`에서 키가 `1`개만 생성하면 되기 때문이다.  
그리고 그 키는 아무런 의미없는 시퀀스가 될 것이다.    
이렇게 관리하면 `복합키`의 존재를 없앨 수 있기 때문에 만들어야하는 클래스도 줄어든다.  
`복합키` 클래스를 아예 만들지 않아도 되기 때문이다.

하지만 반대로 `식별관계`를 썼을떄의 장점도 분명히 존재한다.  
`부모, 자식, 손자` 관계가 늘어날수록 예를 들어 `손자` 테이블은 최소 `3`개의 컬럼으로 이루어진 `복합키`를 가져야 한다.  
하지만 데이터를 조금 더 유용하게 관리할 수 있다.  

`주문`과 `주문상품`의 관계를 생각해보자.  
`식별관계`로 관리한다면 `주문상품`의 `복합키`는 `1-1, 1-2, 1-3, 2-1, 2-2, 2-3` 이렇게 관리가 된다.  
`비식별관계`로 관리한다면 `주문상품`의 데이터는 `1-1, 2-2, 3-1, 4-2, 5-1, 6-2` 이렇게 관리가 될 수 있다.  
결국 `비식별관계`에서 `주문`의 키값이 `1`인 데이터를 조회하면 `1-1, 3-1. 5-1` 이렇게 앞에 있는 `1, 3, 5`는 가비지 데이터가 된다.  
하지만 `식별관계`의 데이터는 `수량`을 나타낼 수 있는 유효한 데이터가 된다.

