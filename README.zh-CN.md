## spring-data-jdbc-mybatis 大道至简 返璞归真

[![](https://img.shields.io/badge/Blog-博客-blue.svg)](http://www.vonchange.com/doc/)
![](https://img.shields.io/maven-central/v/com.vonchange.common/spring-data-jdbc-mybatis.svg?label=Maven%20Central)
[![](https://img.shields.io/github/stars/vonchange/spring-data-jdbc-mybatis.svg?style=social)
](https://github.com/VonChange/spring-data-jdbc-mybatis)
[![](https://gitee.com/vonchange/spring-data-jdbc-mybatis/badge/star.svg?theme=dark)
](https://gitee.com/vonchange/spring-data-jdbc-mybatis) 

**简单点 开发的方法简单点 繁琐的功能请省略 你有不是个AI**
## spring data jdbc 扩展 mybatis 动态sql能力
### 官方spring data jdbc原生直接扩展 mybatis动态sql能力

使用方式和官方教程一样 引入spring-boot-starter-data-jdbc 即可
只需要配置扩展的NamedParameterJdbcTemplate 即可
```
@Configuration
public class MybatisQuerySupportConfig {
    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        return new MybatisJdbcTemplate(dataSource) {@Override protected Dialect dialect() {return new MySQLDialect();}};
    }
}
```
@Query 的ID 是user.md里面ID为queryByUserCode的mybatis sql片段
```
    @Query("user.queryByUserCode")
    List<UserDTO> queryByUserCode(@Param("userCode") String userCode);
```
具体使用参考spring-data-jdbc-demo  
但是 @Query spring 6(jdk17以上) 以上才支持SPEL 不支持实体参数
通过改代码可以解决(支持mybatis版本的) 但有代码侵入性  
无法直接 根据方法名 自动查找sql片段

### 更推荐自研版本spring data jdbc扩展 mybatis动态sql能力
* 底层 jdbcTemplate 复杂SQL才需要mybatis动态模板能力 无QueryDSL 提供crudClient 和jdbcClient

* 和spring data jdbc一样的追求简单,使用jdbcTemplate,调用jdbc。不提供缓存、延迟加载、QueryDSL等JPA或mybatis的许多特性。一个简单、有限、固执己见的ORM

* 扩展mybatis动态sql能力(不依赖mybatis!提取了动态sql代码),可以应对复杂sql,如果换其他模板引擎也是可以的,但有学习成本

* 复杂的SQL写在Markdown的代码片段中,不提供@Query和QueryDSL写法,但按方法名查找和扩展的findByExample可以应付大部分单表查询需求

* 简化mybatis动态sql写法 [easy-dynamic-sql.md](easy-dynamic-sql.md)

[UserInfoRepository.md](spring-data-jdbc-mybatis-demo%2Fsrc%2Fmain%2Fresources%2Fsql%2FUserInfoRepository.md)

```sql
-- findUser
SELECT  [@id column] FROM user_info
<where> 
[@@and id in #{idList:in} and user_name like #{userName:like}]
[@and user_name like userName%]
[@and id in idList]
<if test="null!=createTime">  and create_time < #{createTime}  </if>
</where>
```
* 扩展findByExample 按实体属性名查询扩展 入参是任意符合规范的实体 但请慎用 传入实体不可过多字段
```
userInfoMethodDao.findAll(UserExample.builder()
.userCodeIn(Arrays.asList("u001","u002"))
.userNameLike("ch%")
.createTimeDesc(true).build());
```

## 特性
* 支持按方法名查询  [method-name-query.md](method-name-query.md) 以及扩展版findByExample
* @Id @Table @Column @Version @Transient极少的注解
* 请使用CrudExtendRepository 新增insert update insertBatch updateBatch 扩展版findByExample
* 不提供@Query或QueryDSL,sql统一写在markdown文件里面
* 批量更新 [bach-update.md](bach-update.md)
* [多数据源.md](multi-datasource.md)

## Getting Started with JDBC mybatis

```java
public interface UserInfoRepository extends CrudExtendRepository<UserInfoDO, Long> {
    List<UserInfoDO> findByUserCodes(@Param("userCodes") List<String> userCodes);
    List<UserInfoDO> findUserBySearchParam(@Param("param") SearchParam searchParam); 
}
```
> 在 markdown 定义 [UserInfoRepository.md](spring-data-jdbc-mybatis-demo%2Fsrc%2Fmain%2Fresources%2Fsql%2FUserInfoRepository.md)

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