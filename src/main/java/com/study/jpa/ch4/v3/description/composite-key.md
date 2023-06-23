#### 식별관계 복합 키 매핑
[composite-key.md](../../v1/description/composite-key.md)에서  
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
