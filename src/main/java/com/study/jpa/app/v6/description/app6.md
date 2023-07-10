#### App v5
[`임베디드`](../../../ch7/description/1_embedded.md)를 어플리케이션에 적용해보자.  

~~~java
@Embeddable
public class AddressV6 {
    private String city;
    private String street;
    private String zipcode;
}

@Entity
public class UserV6 extends BaseEntityV6 {
    ...

//    private String city;
//    private String street;
//    private String zipcode;
    private AddressV6 address;
}

@Entity
public class DeliveryV6 extends BaseEntityV6 {
    ...

    //    private String city;
//    private String street;
//    private String zipcode;
    private AddressV6 address;
}
~~~

`유저와 배송` 테이블이 생성되는 구조는 이전과 똑같다.  
다만 코드 관점에서 조금 더 객체지향적으로 변경되었다.