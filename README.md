# spring-data-jdbc-mybatis

![](https://img.shields.io/maven-central/v/com.vonchange.common/spring-data-jdbc-mybatis.svg?label=Maven%20Central)
[![](https://img.shields.io/github/stars/vonchange/spring-data-jdbc-mybatis.svg?style=social)
](https://github.com/VonChange/spring-data-jdbc-mybatis)

**spring data jdbc extend mybatis dynamic sql**
## What Is This?
* It aims at being conceptually easy. In order to achieve this it does NOT offer caching, lazy loading,QueryDSL, write behind or many other features of JPA. This makes  a simple, limited, opinionated ORM.

* use mybatis dynamic SQL(not dependency mybatis),it is good for complex SQL

* SQL is  written in Markdown


[UserInfoRepository.md](spring-data-jdbc-mybatis-demo%2Fsrc%2Fmain%2Fresources%2Fsql%2FUserInfoRepository.md)

```sql
-- findUserByIds
SELECT  [@id column] FROM user_base 
<where> 
[@@and id in #{idList:in} and user_name like #{userName:like}]
[@and user_name like userName%]
[@and id in idList]
<if test="null!=createTime">  and create_time < #{createTime}  </if>
</where>
```
## see  [easy-dynamic-sql.md](easy-dynamic-sql.md)

* extend findByExample,findByBeanProperties [UserExample.java](spring-data-jdbc-mybatis-demo%2Fsrc%2Ftest%2Fjava%2Fcom%2Fvonchange%2Fnine%2Fdemo%2Fdao%2FUserExample.java)
```
userInfoMethodDao.findAll(UserExample.builder()
.userCodeIn(Arrays.asList("u001","u002"))
.userNameLike("ch%")
.createTimeDesc(true).build());
```

## Features
* method name query [method-name-query.md](method-name-query.md)
* @Id @Table @Column
* recommend CrudExtendRepository not CrudRepository,because [curd-repository.md](curd-repository.md)
* not support @Query or QueryDSL, sql must be written in markdown
* batch update [bach-update.md](bach-update.md)
* [multi-datasource.md](multi-datasource.md)

## Getting Started with JDBC mybatis

[UserInfoRepository.java](spring-data-jdbc-mybatis-demo%2Fsrc%2Fmain%2Fjava%2Fcom%2Fvonchange%2Fnine%2Fdemo%2Fdao%2FUserInfoRepository.java)
```java
public interface UserInfoRepository extends CrudExtendRepository<UserInfoDO, Long> {
    List<UserInfoDO> findByUserCodes(@Param("userCodes") List<String> userCodes);
    List<UserInfoDO> findUserBySearchParam(@Param("param") SearchParam searchParam);
}
```
> define sql in markdown [UserInfoRepository.md](spring-data-jdbc-mybatis-demo%2Fsrc%2Fmain%2Fresources%2Fsql%2FUserInfoRepository.md)

> need  @EnableJdbcRepositories
```java
@SpringBootApplication
@EnableJdbcRepositories
public class JdbcMybatisTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(JdbcMybatisTestApplication.class, args);
    }
}
```
> maven
```
<!-- spring boot 2.x -->
<dependency>
  <groupId>com.vonchange.common</groupId>
  <artifactId>spring-data-jdbc-mybatis</artifactId>
  <version>${version}</version>
</dependency>
<dependency>
       <groupId>org.springframework.data</groupId>
       <artifactId>spring-data-commons</artifactId>
 </dependency>
 <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-jdbc</artifactId>
 </dependency>

```



## official spring data jdbc extend mybatis dynamic sql

see spring-data-jdbc-demo

configuration
```
@Configuration
public class MybatisQuerySupportConfig {
    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        return new MybatisJdbcTemplate(dataSource) {@Override protected Dialect dialect() {return new MySQLDialect();}};
    }
}
```
use
```
    @Query("user.queryByUserCode")
    List<UserDTO> queryByUserCode(@Param("userCode") String userCode);
```
but SpEL support became available with Spring Data JDBC 3.0 RC1 