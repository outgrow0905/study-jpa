#### ManyToMany
`다대다` 관계를 알아보자.  
`주문`과 `상품`의 관계를 알아보자.  
하나의 `주문`에 여러개의 `상품`을 포함할 수 있으니, `주문` 입장에서 `상품`과의 관계는 `일대다`이다.  
`상품` 입장에서는 반대로 여러개의 `주문`에 매핑될 수 있다.  
예를 들어 특정 나이키신발이 여러개의 `주문`에 매핑될 수 있다.  
`상품`입장에서 `주문`과의 관계는 `일대다`이다.  



#### 설계
##### 데이터베이스 설계
데이터베이스에 설계한다면 총 `3`개의 테이블이 필요할 것이다.  
`다대다` 관계이니 `주문` 테이블에 `상품` 테이블의 키값을 매핑할 수 없고,  
반대로 `상품` 테이블에 `주문`의 키값을 매핑할 수 없기 때문이다.  
따라서 `주문` 테이블, `상품` 테이블, 그리고 `주문`과 `상품` 각각의 키값을 합쳐 `복합키`로 사용하는 `주문상품` 매핑테이블이 필요하다.  



##### 객체 설계
객체를 설계할 때에는 `주문상품` 매핑테이블이 필요하지 않을 것 같다.  
`주문` 테이블에는 `상품`의 리스트를 참조하면 되고, `상품` 테이블에는 `주문`의 리스트를 참조하면 되기 때문이다.  



#### 다대다 단방향, 다대다 양방향
`주문`과 `상품`의 엔티티부터 구성해보자.  

~~~java
@Entity
public class MyOrderV1 {
    @Id
    @Column(name = "ORDER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderName;

    @ManyToMany
    @JoinTable(
            name = "MyOrderV1_MyProductV1",
            joinColumns = @JoinColumn(name = "ORDER_ID"),
            inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID"))
    List<MyProductV1> products;
}

@Entity
public class MyProductV1 {
    @Id
    @Column(name = "PRODUCT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String productName;

    @ManyToMany(mappedBy = "products")
    List<MyOrderV1> orders;
}
~~~

테스트코드는 아래와 같다.  

~~~java
@Test
void insert1() {
    template(manager -> {
        MyProductV1 product1 = new MyProductV1();
        product1.setProductName("product name1");
        manager.persist(product1);

        MyOrderV1 order1 = new MyOrderV1();
        order1.setOrderName("order name1");
        order1.setProducts(List.of(product1));
        manager.persist(order1);
    });

    template(manager -> {
        MyProductV1 product1 = manager.find(MyProductV1.class, 1);
        MyOrderV1 order1 = manager.find(MyOrderV1.class, 1);

        assertEquals(product1.getOrders().get(0), order1);
        assertEquals(order1.getProducts().get(0), product1);
    });

}
~~~

`다대다`구성에서 매핑테이블을 같는 구성이 끝났다.  
실제로는 매핑 테이블이 있지만 코드에서는 매핑하는 객체도 없이 깔끔하게 정리를 하였다.  

과연 그럴까?  
만약 매핑테이블에 여러가지 정보가 추가된다면 어떻게 될까?  
예를 들어 매핑테이블에 `주문수량`이 추가되어야 한다면 어떨까?  



#### 다대다 연결 엔티티 (복합키 사용)
실무에서는 매핑테이블이 매핑하는 테이블의 키값만으로 테이블을 구성할 가능성은 거의 없다.  
위의 예시에서 `MyOrderV1_MyProductV1` 테이블에 `ORDER_ID, PRODUCT_ID` 두 컬럼 만 존재하는 이상적인 경우는 거의 없을 것이라는 이야기이다.  
위의 예시에서 매핑테이블이 `주문수량`도 넣고 `주문일시`도 넣어보자.  
이런 경우에는 위의 예시처럼 `@JoinTable`기능을 사용할수 없고, 결국 `매핑 엔티티`까지 만들어야 한다.  
`주문, 상품` 입장에서 새로 생긴 `매핑 엔티티`와의 관계는 모두 `일대다`이다.

`주문` 엔티티부터 수정해보자.  
이제는 `매핑앤티티`의 메모리주소를 가지도록 변경해야 한다.  
`상품` 엔티티에서는 `매핑엔티티`를 참조하도록 굳이 하지 말자. 서비스상 필요없다고 간주한다.  

~~~java
@Entity
public class MyOrderV2 {
    @Id
    @Column(name = "ORDER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderName;

    @OneToMany(mappedBy = "order")
    List<MyOrderV2MyProductV2> orderProducts;
}

@Entity
public class MyProductV2 {
    @Id
    @Column(name = "PRODUCT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String productName;
}
~~~

`MyOrderV2MyProductV2` 매핑엔티티가 필요하므로 새로 만들어보자.  
특이한 부분으로 `@Id`가 2개의 변수에 붙어있고, 동시에 `@JoinColumn`으로 외래키의 주인임을 알 수 있다.

~~~java
@Entity
@IdClass(MemberProductIdV2.class)
public class MyOrderV2MyProductV2 {
    @Id
    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private MyOrderV2 order;

    @Id
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private MyProductV2 product;

    private int count;
    private LocalDateTime orderDate;
}
~~~

처음보는 부분은 `@IdClass`이다.  
`MyOrderV2MyProductV2`의 복합키가 각각 `int`가 아닌 `실제 엔티티`를 참조하고 있기 때문에 별도로 키를 관리하는 클래스가 필요한 것이다.  

`MyOrderV2MyProductV2`의 코드는 아래와 같다.  

~~~java
public class MemberProductIdV2 implements Serializable {
    private Integer order;
    private Integer product;

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

테스트코드로 잘 작동하는지 확인해보자.  
요약하면 `@IdClass`를 제외하고는 `ManyToOne`과 동일하기 때문에 어렵게 생각할 것 없다.

~~~java
@Test
void insert2() {
    template(manager -> {
        MyOrderV2 order1 = new MyOrderV2();
        order1.setOrderName("order name1");
        manager.persist(order1);

        MyProductV2 product1 = new MyProductV2();
        product1.setProductName("product name1");
        manager.persist(product1);

        MyOrderV2MyProductV2 orderProducts = new MyOrderV2MyProductV2();
        orderProducts.setOrder(order1);
        orderProducts.setProduct(product1);
        orderProducts.setCount(2);
        orderProducts.setOrderDate(LocalDateTime.now());
        manager.persist(orderProducts);
    });

    template(manager -> {
        MyProductV2 product1 = manager.find(MyProductV2.class, 1);
        MyOrderV2 order1 = manager.find(MyOrderV2.class, 1);

        assertEquals(order1.getOrderProducts().get(0).getProduct(), product1);
    });
}
~~~

`복합키`를 사용하기가 너무 복잡하다는 생각이 안드는가?  
데이터베이스의 `매핑테이블`에서는 우리는 사실 한가지 선택지가 더 있었다.  
`복합키`를 사용하는 것이 아닌 `PK`를 사용하는 것이었다.  
`기본키`를 사용하여 이를 개선해보자.  



#### 다대다 연결엔티티 (PK 사용)
데이터베이스에서 `매핑테이블`에 `PK`를 넣어서 코드를 구성해보자.  
실제로는 `복합키` 구성으로 사용하는 경우가 더 많다고 생각은 한다.  
그래도 코드가 얼마나 간결해지는지 확인은 해보자.  

~~~java
@Entity
public class MyOrderV3 {
    @Id
    @Column(name = "ORDER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderName;

    @OneToMany(mappedBy = "order")
    List<MyOrderV3MyProductV3> orderProducts;
}

@Entity
public class MyProductV3 {
    @Id
    @Column(name = "PRODUCT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String productName;
}

@Entity
public class MyOrderV3MyProductV3 {

    @Id
    @Column(name = "ORDER_PRODUCT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private MyOrderV3 order;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private MyProductV3 product;

    private int count;
    private LocalDateTime orderDate;
}
~~~
매핑앤티티 `MyOrderV3MyProductV3`를 보니 코드가 많이 간결해지긴 했다.  
`@Id`도 한군데 붙어있고, `@IdClass` 어노테이션도 제거할 수 있었다.  
두 가지를 살펴보니 이는 선택의 문제이다.  
데이터베이스에 복합키를 구성하여 실제 테이블을 깔끔하게 관리하는 대신, 코드 복잡도를 높이는 것과  
데이터베이스에 복합키 대신 PK를 구성하면서 실제 테이블에 필요없는(?) 컬럼을 넣고, 코드를 간결하게 관리하는 것중에 선택할 수 있을 것이다.  
(스프링부트 jpa에서 대안이 있을 것이라 본다.)