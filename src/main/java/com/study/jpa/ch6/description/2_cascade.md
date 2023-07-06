#### 영속성 전이: CASCADE
영속성 전이에 대해서 알아보자.  
먼저 부모-자식 관계의 엔티티를 만들어보자.

~~~java
@Entity
public class MyParentV1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "parent")
    private List<MyChildV1> children;
}

@Entity
public class MyChildV1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private MyParentV1 parent;
}
~~~

이제 자식 두개와 부모 하나의 엔티티를 저장해보자.  
먼저 `parent`를 영속상태로 만든 뒤에 `child`에 `parent`를 주입하여 저장한다.

~~~java
@Test
void withoutCascade() {
    template(manager -> {
        MyParentV1 parent = new MyParentV1();
        manager.persist(parent);

        MyChildV1 child1 = new MyChildV1();
        child1.setParent(parent);
        manager.persist(child1);

        MyChildV1 child2 = new MyChildV1();
        child2.setParent(parent);
        manager.persist(child2);
    });
}
~~~

이제 `parent`에 `CASCADE` 옵션을 적용해보자.  
`parent`만 수정하면 된다.

~~~java
@Entity
public class MyParentV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    private List<MyChildV2> children;
}
~~~

이제 다시 데이터를 저장해보자.  
`CASCADE`를 설정한 `parent` 엔티티 `persist` 한번에 모든 데이터가 저장된다.  
사실 코드를 보면 크게 줄어든 것은 없다.  
단지 영속화하는 부분만 편하게 해주는 기능정도로 알고 넘어가자.

~~~java
@Test
void cascadePersist() {
    template(manager -> {
        MyChildV2 child1 = new MyChildV2();
        MyChildV2 child2 = new MyChildV2();

        MyParentV2 parent = new MyParentV2();
        child1.setParent(parent); // 저장 안하면 child 테이블에 insert 할떄에 parent fk가 null로 저장된다.
        child2.setParent(parent);
        parent.setChildren(List.of(child1, child2));

        manager.persist(parent);
    });
}
~~~

![cascade1](img/cascade1.png)  

`CascadeType`을 보니 삭제도 있을 것이다.  
이것도 해보고 넘어가자.  
아래와 같이 `CascadeType`을 하나 더 추가하면 된다.

~~~java
@OneToMany(mappedBy = "parent", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
private List<MyChildV2> children;
~~~

먼저 `CascadeType.REMOVE` 설정을 하기 전에 엔티티삭제를 수행해보자.  
아래의 코드는 오류가 발생한다.  
`parent`와 매핑된 `child`가 있는데 `parent`를 제거할 수 없다는 `SQLIntegrityConstraintViolationException` 오류이다.  

`CascadeType.REMOVE`를 설정하고 수행하면 깔끔하게 성공한다.  
삭제하려는 `parent`와 매핑된 `child`를 먼저 제거하고 그 다음에 `parent`를 삭제해주기 때문이다. 

~~~java
@Test
void cascadeRemove() {
    template(manager -> {
        MyChildV2 child1 = new MyChildV2();
        MyChildV2 child2 = new MyChildV2();

        MyParentV2 parent = new MyParentV2();
        child1.setParent(parent);
        child2.setParent(parent);
        parent.setChildren(List.of(child1, child2));

        // persist
        manager.persist(parent);

        // remove
        manager.remove(parent);
    });
}
~~~


#### 고아객체
jpa에서는 영속성 컨텍스트에 등록된 부모객체로부터 연관관계가 끊어진 자식엔티티를 자동으로 제거해주는 기능이 있다.  
먼저, 이 기능을 사용하지 않고 자식엔티티를 삭제해보자.

~~~java
@Test
void withoutOrphan() {
    final MyParentV2 parent = new MyParentV2();

    template(manager -> {
        MyChildV2 child1 = new MyChildV2();
        MyChildV2 child2 = new MyChildV2();

        child1.setParent(parent);
        child2.setParent(parent);
        parent.setChildren(List.of(child1, child2));

        // persist
        manager.persist(parent);
    });

    template(manager -> {
        MyParentV2 findParent = manager.find(MyParentV2.class, parent.getId());
        manager.remove(findParent.getChildren().get(0));
    });
}
~~~

이처럼 `child` 엔티티를 직접 조회하여 삭제해야한다.  
`parent`는 `child` 의 `연관관계 주인`이 아니기때문에 `parent`에서 `child`를 꺼내서 삭제한다 한들 `delete` 쿼리는 수행되지 않는다.  
이러한 기능을 편리하게 해주는 기능이 고아객체제거 기능이다.  

`parent`에 `고아객체제거` 기능을 붙여보자.

~~~java
@Entity
public class MyParentV3 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<MyChildV3> children;
}
~~~

이제 테스트를 수행해보자.  
연관관계의 주인도 아닌 `parent`에서 `child`를 제거하면 `delete`가 수행되면 성공이다.  

~~~java
@Entity
public class MyParentV3 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<MyChildV3> children;
}
~~~

테스트코드는 아래와 같다.

~~~java
@Test
void orphanRemoval() {
    final MyParentV3 parent = new MyParentV3();

    template(manager -> {
        MyChildV3 child1 = new MyChildV3();
        MyChildV3 child2 = new MyChildV3();

        child1.setParent(parent);
        child2.setParent(parent);
        parent.setChildren(List.of(child1, child2));

        // persist
        manager.persist(parent);
    });

    template(manager -> {
        MyParentV3 findParent = manager.find(MyParentV3.class, parent.getId());
        findParent.getChildren().remove(0);
    });
}
~~~

`orphanRemoval`은 어디서나 사용할수는 없다.  
`멤버`와 `팀`의 관계를 생각해보자.  
여기서 `parent` 역할은 `팀`이 될 것이다.  
`orphanRemoval`을 `팀`의 `회원리스트`에 설정하게 되면 위의 예시와 같이 잘 동작할 것이다.  
그러나 `orphanRemoval`을 `회원`에 설정한다면 어떻게 될까?  
`회원엔티티`에서 `팀`을 제거하면 `팀` 데이터가 삭제될까?  
너무나 위험한 기능이다. 특정 `멤버`에서 `팀`을 제거한다고해서 `팀` 데이터를 삭제한다면 삭제하는 `팀`에 소속된 다른 `멤버`들은 더미데이터가 된다.  
따라서 jpa는 그러한 기능을 제한적으로 제공한다.  
`orphanRemoval`을 `@OneToMany, @ManyToMany`에만 제공하는 이유이다.