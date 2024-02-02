## spring-data-jdbc-mybatis 大道至简 返璞归真

[![](https://img.shields.io/badge/Blog-博客-blue.svg)](http://www.vonchange.com/doc/)
![](https://img.shields.io/maven-central/v/com.vonchange.common/spring-data-jdbc-mybatis.svg?label=Maven%20Central)
[![](https://img.shields.io/github/stars/vonchange/spring-data-jdbc-mybatis.svg?style=social)
](https://github.com/VonChange/spring-data-jdbc-mybatis)
[![](https://gitee.com/vonchange/spring-data-jdbc-mybatis/badge/star.svg?theme=dark)
](https://gitee.com/vonchange/spring-data-jdbc-mybatis) 

**spring data jdbc 扩展 mybatis 动态sql能力**
## What Is This?
* 和spring data jdbc一样的追求简单,使用jdbcTemplate,调用jdbc。不提供缓存、延迟加载、QueryDSL等JPA或mybatis的许多特性。一个简单、有限、固执己见的ORM

* 使用mybatis动态sql能力(不依赖mybatis!),可以应对复杂sql

* SQL统一写在Markdown里,不提供@Query或QueryDSL


[UserInfoRepository.md](spring-data-jdbc-mybatis-test%2Fsrc%2Ftest%2Fresources%2Fsql%2FUserInfoRepository.md)

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
## 更多扩展写法 ==>  [easy-dynamic-sql.md](easy-dynamic-sql.md)
## 特性
### 不提供@Query或QueryDSL,sql统一写在markdown文件里面
### 批量更新
> need rewriteBatchedStatements=true&allowMultiQueries=true
```java
public interface UserInfoRepository extends CrudRepository<UserInfoDO, Long> {
    @BatchUpdate
    int batchUpdate(List<UserInfoDO> list);
}
```
### [多数据源.md](multi-datasource.md)

## Getting Started with JDBC mybatis


```java
public interface UserInfoRepository extends CrudRepository<UserInfoDO, Long> {
    List<UserInfoDO> findListByUserCode(@Param("userCode") String userCode);
    List<UserInfoDO> findUserBySearchParam(@Param("param") SearchParam searchParam); 
}
```
> 在 markdown 定义 [UserInfoRepository.md](spring-data-jdbc-mybatis-test%2Fsrc%2Ftest%2Fresources%2Fsql%2FUserInfoRepository.md)

> 只需要  @EnableJdbcRepositories 注解
```java
@SpringBootApplication
@EnableJdbcRepositories
public class JdbcMybatisTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(JdbcMybatisTestApplication.class, args);
    }
}
```
> maven 支持spring boot 2.x
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