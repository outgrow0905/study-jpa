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



#### 다대다 단방향
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
과연 그럴까?  
만약 매핑테이블에 여러가지 정보가 추가된다면 어떻게 될까?  
예를 들어 매핑테이블에 `주문수량`이 추가되어야 한다면 어떨까?  