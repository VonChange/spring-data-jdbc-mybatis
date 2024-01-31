## spring-data-jdbc-mybatis

[![](https://img.shields.io/badge/Blog-博客-blue.svg)](http://www.vonchange.com/doc/mini.html)
![](https://img.shields.io/maven-central/v/com.vonchange.common/spring-data-jdbc-mybatis.svg?label=Maven%20Central)
[![](https://img.shields.io/github/stars/vonchange/spring-data-jdbc-mybatis.svg?style=social)
](https://github.com/VonChange/spring-data-jdbc-mybatis)
[![](https://gitee.com/vonchange/spring-data-jdbc-mybatis/badge/star.svg?theme=dark)
](https://gitee.com/vonchange/spring-data-jdbc-mybatis)

**spring data jdbc extend mybatis dynamic sql**
### What Is This?
* It aims at being conceptually easy. In order to achieve this it does NOT offer caching, lazy loading, write behind or many other features of JPA. This makes  a simple, limited, opinionated ORM.

* use mybatis dynamic SQL,it is good for complex SQL

* SQL is  written in Markdown


[UserBaseRepository.md](spring-data-jdbc-mybatis-test/src/test/resources/sql/UserBaseRepository.md)

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
### see  [easy-dynamic-sql.md](easy-dynamic-sql.md)
### Getting Started with JDBC mybatis


```java
public interface UserInfoRepository extends CrudRepository<UserInfoDO, Long> {
    List<UserInfoDO> findListByUserCode(@Param("userCode") String userCode);
    List<UserInfoDO> findUserBySearchParam(@Param("param") SearchParam searchParam); 
}
```
> define sql in markdown [UserInfoRepository.md](spring-data-jdbc-mybatis-test%2Fsrc%2Ftest%2Fresources%2Fsql%2FUserInfoRepository.md)

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


