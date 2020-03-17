# spring-data-mybatis-mini
spring data  jdbc mybatis-mini 实现

== Getting Started

1. 类似于mybatis plus 增加增删改(没有删除) 基础方法 
2. 底层是spring jdbc  但写法是常用的mybatis写法 无学习曲线
3. 抛弃繁琐的xml 所有sql 写在markdown文件里 便于书写和阅读 
4. update/save/insert/delete 开头方法是更新操作 
5. 支持分页 分页参数必须是第一个参数 同名+Count可优化分页
6. 支持读写分离(需要自定义) 多数源能变现实现但在微服务化潮流里尽量保证同一数据源
7. 对于 " > "," < "," >= "," <= "," <> "无需转义(两边需有空格 我会自动替换转义)
8. 提供其他if 判断和in查询简写方式(偷懒 >-<)
9. 支持mysql 本来也支持oracle的 但不打算再支持
10. 使用属于spring data 体系的 注解也是的

== 使用步骤 

1. 添加依赖 
2. @EnableMybatisMini
3. extends Repository<UserBaseDO, Long> 或 extends
   BaseQueryRepository(只查询)
 
 Here is a quick teaser of an application using Spring Data Repositories
 in Java:


``` 
import org.springframework.data.mybatis.mini.jdbc.repository.query.ConfigLocation;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

@ConfigLocation("sql.sql")
public interface UserBaseRepository extends Repository<UserBaseDO, Long> {

  List<UserBaseDO> findList(@Param("userName") String userName,
                          @Param("createTime") Date createTime);
  Page<UserBaseDO> findList(Pageable pageable, @Param("userName") String userName,@Param("createTime") Date createTime);
  String findUserName(@Param("userName") String userName);

  List<UserBaseDO> findListByIds(@Param("userName") String userName,
                           @Param("createTime") Date createTime,@Param("idList")List<Long> idList);

  int updateIsDelete(@Param("isDelete") Integer isDelete,@Param("id") Long id);
  
}
```

-- sql 包下 sql.md 识别```开头结尾的 -- 定义的同名方法



```
-- findList
select * from user_base
where user_name = #{userName}
and create_time  < #{createTime}
```

```
-- findUserName
SELECT user_name FROM user_base WHERE user_name = #{userName}
```


```
-- findListByIdsX
SELECT * FROM user_base WHERE 1=1 
<if test="null!=userName"> and user_name <> #{userName} </if>
<if test="null!=idList and idList.size>0">  and id in <foreach collection="idList" index="index" item="item" open="(" separator="," close=")">#{item}</foreach></if>
<if test="null!=createTime">  and create_time < #{createTime}  </if>
```

```
-- findListByIds
SELECT * FROM user_base WHERE 1=1 
{@and user_name <> userName}
{@and id in idList}
{@and create_time < createTime}
```

```
-- updateIsDelete
update user_base set is_delete = #{isDelete} where id =#{id}
```

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
-- findByFirstnameLike

SELECT * FROM person WHERE firstname LIKE CONCAT('%', #{lastname}, '%'))

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

=== Maven configuration

Add the Maven dependency:

```

<dependency>
  <groupId>com.vonchange.common</groupId>
  <artifactId>spring-data-mybatis-mini</artifactId>
  <version>1.2</version>
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

> 偷懒简化 if test 和in查询 识别 {@开头

1. {@and id in idList} 等于 <if test="null!=idList and idList.size>0">
  and id in <foreach collection="idList" index="index" item="item"
  open="(" separator="," close=")">#{item}</foreach></if> 
  
2. {@and user_name <> userName} 等于 <if test="null!=userName and
   ''!=userName"> and user_name <> #{userName} </if> 
   
3. in 查询List实体下的属性 {@and id in userList:id} 