## spring-data-mybatis-mini
[简体中文-more](README_zh.md) 

**Is equivalent to spring data jdbc + mybatis template dynamic query**

**Make It Easy**

1. discard cumbersome XML only use mybatis template engine namely
   dynamic SQL,SQL written in markdown file easier to write and read,SQL
   can be unified management

2. sql query based on spring jdbc,not mybatis

3. CRUD,Bulkupdate insert,support Page,Multiple data sources,Separation 
   of reading and writing

4. Simplify the dynamic SQL usage of mybatis (mix and use - the same as
   mybatis) eg:

```
[@and id in idList] equal
<if test="null!=idList and idList.size>0"> and id in <foreach
collection="idList" index="index" item="item" open="(" separator=","
close=")">#{item}</foreach></if>
```
![例子](mini.png)

== mybatis-sql-extend extend mybatis-spring-boot only Simplify the
dynamic SQL usage of mybatis and SQL written in markdown file easier to
write and read

```
-- depend see mybatis-sql-extend-test
    <dependency>
        <groupId>com.vonchange.common</groupId>
        <artifactId>mybatis-sql-extend</artifactId>
        <version>${spring.mybatis.mini}</version>
    </dependency>
```
```
public class SimpleLanguageDriver extends XMLLanguageDriver implements LanguageDriver {
    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        String sqlInXml = MybatisSqlLanguageUtil.sqlInXml("mapper",script,new MySQLDialect());
        return super.createSqlSource(configuration, sqlInXml, parameterType);
    }
}
```
```
    @Select("@UserMapper.findList")
    List<UserBaseDO> findList(@Param("userName") String userName,
                              @Param("createTime") LocalDateTime createTime);
```
> UserMapper.md 
```
-- findList
select * from user_base
[@sql findListWhereSql]
```

> sql 
```
-- findListWhereSql
<where>
[@@and user_name like userName] 
[@and create_time  < createTime]
</where>
```
```
-- config
mybatis:
  default-scripting-language-driver: com.vonchange.mybatis.test.config.SimpleLanguageDriver
  configuration:
    map-underscore-to-camel-case: true
 
```
== Getting Started

1. Add the Maven dependency
2. @EnableMybatisMini
3. extends BaseRepository<UserBaseDO, Long> or extends
   BaseQueryRepository(only query) 
4. demo[spring-data-mybatis-mini-demo](https://github.com/VonChange/spring-data-mybatis-mini-demo/blob/master/src/test/java/com/vonchange/nine/demo/dao/UserBaseRepositoryTest.java)
 
 Here is a quick teaser of an application using Spring Data mybatis mini
 in Java:

=== Maven configuration

Add the Maven dependency:

```
  <!-- spring boot 2.x use 2.3.6,lower eg 1.5.x use 1.9.4 -->
<dependency>
  <groupId>com.vonchange.common</groupId>
  <artifactId>spring-data-mybatis-mini</artifactId>
  <version>2.3.6</version>
</dependency>

<dependency>
       <groupId>org.springframework.data</groupId>
       <artifactId>spring-data-commons</artifactId>
 </dependency>
 <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-jdbc</artifactId>
 </dependency>
<dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.15</version>
</dependency>
```
```
//add @EnableMybatisMini
@EnableMybatisMini
@SpringBootApplication 
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
} 
```
``` 
import org.springframework.data.mybatis.mini.jdbc.repository.query.ConfigLocation;
import org.springframework.data.mybatis.mini.jdbc.repository.support.BaseRepository;
import org.springframework.data.repository.query.Param;

public interface UserBaseRepository extends BaseRepository<UserBaseDO, Long> {
  @ReadDataSource
  List<UserBaseDO> findList(@Param("userName") String userName,
                          @Param("createTime") Date createTime);
  Page<UserBaseDO> findList(Pageable pageable, @Param("userName") String userName,@Param("createTime") Date createTime);
  String findUserName(@Param("userName") String userName);

  List<UserBaseVO> findListByIds(@Param("userName") String userName,
                           @Param("createTime") Date createTime,@Param("idList")List<Long> idList);

  int updateIsDelete(@Param("isDelete") Integer isDelete,@Param("id") Long id);
  
}
```

> The same name under the default SQL package UserBaseRepository.md
> Identify the method with the same name defined at the beginning and
> the end of ```

> eg[UserBaseRepository.md](https://github.com/VonChange/spring-data-mybatis-mini/blob/master/UserBaseRepository.md)



>  domain entity
```
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "user_base")
public class UserBaseDO {
    @Id
    private Long id;
    private String userName;
    private String  firstPhone;

}
```


```

@Service
public class MyService {
  @Resource
  private final UserBaseRepository userBaseRepository;

  public void doWork() {
     List<UserBaseDO> userBaseDOList = userBaseRepository.findList("test",new Date());
 }
}


```



> Simplify the dynamic SQL usage of mybatis, \[@ begin

>   \[@and id in idList] 等于

```
<if test="null!=idList and idList.size>0"> and id in <foreach
collection="idList" index="index" item="item" open="(" separator=","
close=")">#{item}</foreach></if>
  
  ```
  
>   \[@and user_name <> userName] Is equivalent to

```
<if test="null!=userName and ''!=userName"> and user_name <>
#{userName} </if>
   ```
   
3. in List \[@and id in userList:id]

4.  like 

 ```
 [@and user_name like userName] ] is equivalent to  and user_name like CONCAT('%',?,'%')  
 [@and user_name like userName%] is equivalent to and user_name like  CONCAT(?,'%') 
 [@and user_name like userName%] is equivalent to and user_name like CONCAT('%','test')   
 
 ```
 
5. other

```
[@AND C.DESCRIPTION LIKE #{bean.description:like}  or C.title like #{bean.description:like}]
is equivalent to
<if test="null!=bean.description and ''!=bean.description">
AND C.DESCRIPTION LIKE  CONCAT('%',#{bean.description},'%')    or C.title like CONCAT('%',#{bean.description},'%')
</if>
 [@AND content -> '$.account' = #{bean.account}]
is equivalent to
 <if test="null!=bean.account and ''!=bean.account">
 AND content -> '$.account' = #{bean.account}
 </if>

```

6. \[@sql XX] XX markdown XX named sql fragment


