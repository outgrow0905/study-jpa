#### Basic Annotation
기본 어노테이션부터 알아보자.  



##### @Column
~~~java
public class MemberV2 {
    @Column(name = "NAME", nullable = false, length = 10)
    private String username;
    
    ...
}
~~~
위와 같이 `@Column`에서 `nullable`이나 `length`와 같이 컬럼속성을 부여할 수 있다.  
위와 같이 설정을 한다면 `DDL` 자동생성에서 `NAME varchar(10) not null` 와 같이 반영된다.



##### @Unique
테이블 생성시에 `unique key`를 반영할 수도 있다.  
~~~java
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "NAME_AGE_UNIQUE", columnNames = {"NAME", "AGE"})})
public class MemberV2 {
    ...
}
~~~

위와 같이 설정을 한다면 테이블생성 후에 아래와 같은 `DDL`이 수행된다.

~~~sql
alter table MemberV2
add constraint NAME_AGE_UNIQUE unique (NAME, age)
~~~



##### DDL annotation
위와 같은 컬럼제약이나 유니크 키 설정은 JPA를 사용하면서 자동으로 `DDL`을 만들어서 사용하는 환경에서만 유용하다.  
위와 같은 설정을 하더라도 `DDL` 생성 이후에는 아무런 역할을 하지 않는다.  
컬럼제한을 `10`으로 했지만 `20`으로 넣어도 런타임에서 오류가 날 뿐이다.  
유니크키를 설정헀지만 유니크컬럼을 누락하고 데이터를 넣어도 런타임에서 오류가 날 뿐이다.  

그럼에도 위와 같은 설정은 해주는게 좋다.  
코드만 보고도 테이블의 속성이나 제약조건들을 쉽게 알 수 있기 때문이다.