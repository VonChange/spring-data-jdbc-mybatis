# spring-data-mybatis-mini
spring data  jdbc mybatis-mini 实现

> 1. 动态sql写在 markdown里 容易书写和阅读

> 2. 做减法 只是对spring jdbc的一层简单封装

> 3. 简化 mybatis if test空和in查询写法

== Getting Started

1. 类似于mybatis plus 增加增删改(没有删除) 批量更新插入等基础方法
2. 底层是spring jdbc 无缓存 但写法是常用的mybatis模版写法 无学习曲线
3. 抛弃繁琐的xml 所有sql 写在markdown文件里 便于书写和阅读
   默认位置sql包下repository接口名.md @ConfigLocation 可自定义位置
4. 自定义更新 update/save/insert/delete 开头方法是更新操作 
5. 支持分页 分页参数必须是第一个参数 
6. 对于 " > "," < "," >= "," <= "," <> "无需转义(两边需有空格 我会自动替换转义)
7. 提供if判断和in查询简写方式(偷懒 >-<)
8. 注解属于spring data jpa 体系的
9. {@sql XX} XX markdown文件XX名的sql片段
10. 查询返回实体 不需要必须是DO 如果没特殊规范
    也可直接返回VO层实体(抛弃繁琐的DO->DTO->VO 偷懒轻喷)
11. 支持批量更新（jdbc链接参数需加入rewriteBatchedStatements=true&allowMultiQueries=true）
12. 分页某些特性支持mysql 本来也支持oracle的 但不打算再支持 

== 其他特性 无特殊需要可不用关心 

1. 分页 可自定义同名+Count的sql 优化分页 
2. 支持读写分离 根据业务逻辑添加@ReadDataSource在方法名上 默认配置多数据源随机取
   可自定义
3. 多数源也能实现但在微服务化潮流里尽量保证同一数据源(不提供说明支持方法)
4. 读写分离 参数mybatis-mini.isReadExcludePrimary 默认不排除主库 设置成true
   除主库外读
   
== 与mybatis 和 mybatis-plus jpa 比较

1. sql写在markdown文件里 写起来舒服 便于阅读
2. 无需resultType resultMap 复杂指定(mybatis xml啰嗦的配置) 只需定义方法名
3. 不允许查询sql放到@Select 和 @Query上 jpa复杂sql不容易书写还乱(虽然也能实现)
   保持mybatis风格写在文件里 便于维护
4. 无缓存 无根据方法名生成sql(需要你思考 不透明) 无条件构造器(EntityWrapper)
   类似的鸡肋功能 查询就该是sql 纯纯的jdbc
5. 使用简单 约定大于配置 默认配置基本都满足


== 使用步骤

1. 添加依赖 
2. @EnableMybatisMini
3. extends BaseRepository<UserBaseDO, Long> 或 extends
   BaseQueryRepository(只查询) 
4. 使用例子demo项目[spring-data-mybatis-mini-demo](https://github.com/VonChange/spring-data-mybatis-mini-demo/blob/master/src/test/java/com/vonchange/nine/demo/dao/UserBaseRepositoryTest.java)
 


 
 
 Here is a quick teaser of an application using Spring Data
Repositories in Java:

=== Maven configuration

Add the Maven dependency:

```
  <!-- spring boot 2.x 是使用版本2.2.1 1.5.x 使用版本1.8.3 -->
<dependency>
  <groupId>com.vonchange.common</groupId>
  <artifactId>spring-data-mybatis-mini</artifactId>
  <version>2.2.1</version>
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

> 默认sql 包下同名吧UserBaseRepository.md 识别```开头结尾的 -- 定义的同名方法
> 参见[UserBaseRepository.md](UserBaseRepository.md)



> 实体类 定义ID 和TABLE 名
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

//添加 EnableMybatisMini 注解 
@EnableMybatisMini
@SpringBootApplication 
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
} 
```



> 偷懒简化 if test 和in查询 识别 {@开头

1. {@and id in idList} 等于 

```
<if test="null!=idList and idList.size>0"> and id in <foreach
collection="idList" index="index" item="item" open="(" separator=","
close=")">#{item}</foreach></if>
  
  ```
  
2. {@and user_name <> userName} 等于 

```
<if test="null!=userName and ''!=userName"> and user_name <>
#{userName} </if>
   ```
   
3. in 查询List实体下的属性 {@and id in userList:id} 

4.  like 

 ```
 {@and user_name like userName} 等于 and user_name like CONCAT('%',?,'%')  
 {@and user_name like userName%} 等于 and user_name like  CONCAT(?,'%') 
  {@and user_name like userName%} 等于 and user_name like CONCAT('%','test')   
 
 ```