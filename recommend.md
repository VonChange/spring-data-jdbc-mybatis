
推荐使用 spring data mybatis mini
[github地址](https://github.com/VonChange/spring-data-mybatis-mini)
[gitee地址](https://gitee.com/vonchange/spring-data-mybatis-mini)

**等同于spring data jdbc + mybatis 动态sql能力**

**大道至简 极致效率 麻雀虽小 五脏俱全**

1. 抛弃繁琐的xml 只使用mybatis模版引擎即动态sql能力 sql写在markdown文件里
   更容易书写和阅读 sql能统一管理查看

2. 底层基于springJdbc 而不是mybatis 更直接纯粹

3. 提供单表增删改(没有删除) 批量更新插入等基础方法 支持分页 多数据源 读写分离

4. mybatis最大优点就是sql模版引擎
   我也有且仅有使用这部分功能(对于使用过mybatis的无学习成本) 但底层使用springJDBC
   更简单直接 
5. 简化mybatis动态sql写法(可混用-写法还是mybatis那套) 比如

```
[@and id in idList] 等于
<if test="null!=idList and idList.size>0"> and id in <foreach
collection="idList" index="index" item="item" open="(" separator=","
close=")">#{item}</foreach></if>
```
![例子](https://gitee.com/vonchange/spring-data-mybatis-mini/raw/master/mini.png)



== 新增mybatis-spring-boot实现只简化mybatis动态sql写法和sql写在markdown文件里

```
-- 依赖 详情见mybatis-sql-extend-test模块
    <dependency>
        <groupId>com.vonchange.common</groupId>
        <artifactId>mybatis-sql-extend</artifactId>
        <version>${spring.mybatis.mini}</version>
    </dependency>
```
```
// mybatis plus 扩展 MybatisXMLLanguageDriver 配置 mybatis-plus.configuration.default-scripting-language
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
> UserMapper.md 文件
```
-- findList
select * from user_base
[@sql findListWhereSql]
```

> sql 片段
```
-- findListWhereSql
<where>
[@@and user_name like userName] 
[@and create_time  < createTime]
</where>
```
```
-- 配置
mybatis:
  default-scripting-language-driver: com.vonchange.mybatis.test.config.SimpleLanguageDriver
  configuration:
    map-underscore-to-camel-case: true
 
```

== why not spring data jdbc,jpa,hibernate,mybaits,mybatis-plus等

1. 基于spring data jdbc理念但扩展使用mybatis动态sql能力 对于复杂点查询支持更好
2. 相比jpa 底层使用hibernate(当然也能sql) 只有sql 基于spring jdbc
   无jpa根据方法名(复杂点需要你学习思考,名字老长,不透明) 简单没有黑魔法 学习成本低
   sql写在markdown里,纯jdbc更易于调优
3. 相比mybatis 没有cache,复杂join映射实体,无resultType,resultMap配置
   扩展单表CRUD 只用他的动态sql能力的模版引擎和sql放到文件管理思想 去繁就简
   取其优点抛弃鸡肋功能
4. 相比mybatis-plus等扩展mybatis框架 他们做的越来越像hibernate,jpa
   搞Criteria那套 基本脱离mybatis优点 
5. 查询只提供一个选择 就是sql写在markdown文件里 不会提供类似hibernate Criteria
   多种选择说是灵活但项目多种有多种实现写法 你会有打人的冲动 
6. 缓存可以用SpringCache等上层方案 
7. 查询只能映射单一实体(VO,DO,DTO均可 支持继承) 但现在推荐减少JOIN 推荐代码里join
   后期会尝试写新的组件sqlHelper方式简化
 

== Getting Started

1. 提供单表增删改(没有物理删除) 批量更新插入等基础方法
2. 抛弃繁琐的xml 所有sql 写在markdown文件里 便于书写和阅读
   默认位置sql包下repository接口名.md @ConfigLocation 可自定义位置
3. 自定义更新 update/save/insert/delete 开头方法是更新操作 
4. 支持分页 分页参数必须是第一个参数 
5. 对于 " > "," < "," >= "," <= "," <> "无需转义(两边需有空格 我会自动替换转义)
6. 提供if判断和in查询简写方式(偷懒 >-<)
7. 注解属于spring data jpa 体系的
8. 支持sql片段 \[@sql XX] XX markdown文件XX名的sql片段
9. 查询返回实体 不需要必须是DO 如果没特殊规范
   也可直接返回VO层实体(抛弃繁琐的DO->DTO->VO 偷懒轻喷)
10. 支持批量更新插入（jdbc链接参数需加入rewriteBatchedStatements=true&allowMultiQueries=true）
11. 分页某些特性支持mysql,oracle 主支持mysql
12. 使用简单 约定大于配置 默认配置基本都满足
13. 支持LocalDateTime LocalTime jdk8更方便的时间类型

== 其他特性 无特殊需要可不用关心 

1. 分页 可自定义同名+Count的sql 优化分页 
2. 支持读写分离 根据业务逻辑添加@ReadDataSource在方法名上 默认配置多数据源随机取
   可自定义
3. 多数源支持但在微服务化潮流里尽量保证同一数据源
   

== 使用步骤基本同jpa,spring data jdbc

1. 添加依赖 
2. @EnableMybatisMini
3. extends BaseRepository<UserBaseDO, Long> 或 extends
   BaseQueryRepository(只查询) 
4. 使用例子demo项目[spring-data-mybatis-mini-demo](https://github.com/VonChange/spring-data-mybatis-mini-demo/blob/master/src/test/java/com/vonchange/nine/demo/dao/UserBaseRepositoryTest.java)
 


 
 
 Here is a quick teaser of an application using Spring Data
mybatis mini in Java:

=== Maven configuration

Add the Maven dependency:

```
  <!-- spring boot 2.x 是使用版本2.3.3 低版本比如1.5.x 使用版本1.9.1 -->
<dependency>
  <groupId>com.vonchange.common</groupId>
  <artifactId>spring-data-mybatis-mini</artifactId>
  <version>2.2.3</version>
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
//添加 EnableMybatisMini 注解 
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

> 默认sql 包下同名吧UserBaseRepository.md 识别```开头结尾的 -- 定义的同名方法
> 参见[UserBaseRepository.md](https://github.com/VonChange/spring-data-mybatis-mini/blob/master/UserBaseRepository.md)



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

```

 

