# spring-data-mybatis-mini
spring data  jdbc mybatis-mini 实现

== Getting Started

Here is a quick teaser of an application using Spring Data Repositories in Java:


``` 
@ConfigLocation("sql.sql")
public interface PersonRepository extends BaseRepository<Person, Long> {

  List<Person> findByLastname(String lastname);

  List<Person> findByFirstnameLike(String firstname);
}
```

-- sql 包下 sql.md


```
-- findByLastname 

SELECT * FROM person WHERE firstname = #{lastname}

```


```
-- findByFirstnameLike

SELECT * FROM person WHERE firstname LIKE CONCAT('%', #{lastname}, '%'))

```

```

@Service
public class MyService {
  @Resource
  private final PersonRepository repository;

  public void doWork() {
    List<Person> lastNameResults = repository.findByLastname("Schauder");
    List<Person> firstNameResults = repository.findByFirstnameLike("Je%");
 }
}
//添加 EnableMybatisMini 注解 @EnableMybatisMini()
@SpringBootApplication 
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
} 
```

=== Maven configuration

Add the Maven dependency:

```
<dependency>
  <groupId>com.vonchange.common</groupId>
  <artifactId>spring-data-mybatis-mini</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```
