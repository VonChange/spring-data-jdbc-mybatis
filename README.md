## spring-data-mybatis-mini
[english](README_en.md)

![](https://img.shields.io/maven-central/v/com.vonchange.common/spring-data-mybatis-mini.svg?label=Maven%20Central)
[![Gitter](https://badges.gitter.im/von_change/spring-data-mybatis-mini.svg)](https://gitter.im/von_change/spring-data-mybatis-mini?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![](https://img.shields.io/github/stars/vonchange/spring-data-mybatis-mini.svg?style=social)
](https://github.com/VonChange/spring-data-mybatis-mini)
[![](https://gitee.com/vonchange/spring-data-mybatis-mini/badge/star.svg?theme=dark)
](https://gitee.com/vonchange/spring-data-mybatis-mini)

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
11. 分页某些特性支持mysql,oracle 主支持mysql 可自定义方言实现其他数据库
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
  <!-- spring boot 2.x 是使用版本2.3.4 低版本比如1.5.x 使用版本1.9.2 -->
<dependency>
  <groupId>com.vonchange.common</groupId>
  <artifactId>spring-data-mybatis-mini</artifactId>
  <version>2.3.4</version>
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



> 偷懒简化 if test 和in查询 识别 \[@开头

>   \[@and id in idList] 等于

```
<if test="null!=idList and idList.size>0"> and id in <foreach
collection="idList" index="index" item="item" open="(" separator=","
close=")">#{item}</foreach></if>
  
  ```
  
>   \[@and user_name <> userName] 等于

```
<if test="null!=userName and ''!=userName"> and user_name <>
#{userName} </if>
   ```
   
3. in 查询List实体下的属性 \[@and id in userList:id]

4.  like 

 ```
 [@and user_name like userName] 等于 and user_name like CONCAT('%',?,'%')  
 [@and user_name like userName%] 等于 and user_name like  CONCAT(?,'%') 
 [@and user_name like userName%] 等于 and user_name like CONCAT('%','test')   
 
 ```
 
5. 其他非4个分隔

```
[@and id in #{idList:in} and user_name like #{userName:like}]
等于
<if test="@com.vonchange.mybatis.tpl.MyOgnl@isNotEmpty(idList) and @com.vonchange.mybatis.tpl.MyOgnl@isNotEmpty(userName) "> and id in <foreach collection="idList" index="index" item="item" open="(" separator="," close=")">#{item}</foreach> and user_name like  CONCAT('%',#{userName},'%')  </if>

 [@AND content -> '$.account' = #{bean.account}]
 等于
 <if test="null!=bean.account and ''!=bean.account">
 AND content -> '$.account' = #{bean.account}
 </if>

```

6. \[@sql XX] XX markdown文件XX名的sql片段

>  相关注解 

1. @ColumnNot 非字段注解 

2. InsertIfNull UpdateIfNull 插入或者更新为空时默认值 可使用函数

3. UpdateNotNull updateAllField方法NULL值忽略

4. ReadDataSource 指定某个方法读数据源 默认配置多数据源随机取 

```
 //自定义 读库数据源 不自定义默认所有你设置的数据源
    @Bean
    public ReadDataSources initReadDataSources(){
        return new ReadDataSources() {
            @Override
            public DataSource[] allReadDataSources() {
                return new DataSource[]{mainDataSource(),mainDataSource(),readDataSource()};
            }
        };
    }
```

> 批量更新插入

1. jdbc链接参数需加入rewriteBatchedStatements=true&allowMultiQueries=true

2. 提供insertBatch(默认第一行不为NULL的字段) 可在markdown里自定义sql
   无需关心List对象大小

3. 经测试简单数据插入1万耗时1s以内

4. 自定义实现(建议使用 更透明)

```
  
  @BatchUpdate(size = 5000)
  int batchInsert(List<UserBaseDO> list);
```

只需定义单条insert 语句

```
-- batchInsert
insert into user_base(`user_name`,`mobile_phone`,create_time) values
(#{userName},#{mobilePhone},#{createTime}) 

```


> 大数据量流式读取

1. 使用场景: 不用编写复杂分包逻辑,表数据大小,可关联表查 可直接 select * from 整个表
   不用关心内存爆调 流的方式读取
   
2. 使用例子 

> 定义方法
```
void findBigData(@Param("")AbstractPageWork<UserBaseDO> abstractPageWork,@Param("userName") String userName);
```
> 定义sql

```
-- findBigData
select * from user_base
<where> 
[@and user_name like userName]
</where>
```
> 使用demo

```
 AbstractPageWork<UserBaseDO> abstractPageWork = new AbstractPageWork<UserBaseDO>() {
            @Override
            protected void doPage(List<UserBaseDO> pageContentList, int pageNum, Map<String, Object> extData) {
                pageContentList.forEach(userBaseDO -> {
                    log.info("{}",userBaseDO.toString());
                });

            }

            @Override
            protected int getPageSize() {
                return 500;
            }
        };
       userBaseRepository.findBigData(abstractPageWork,"三");
       log.info("{} {} {}",abstractPageWork.getSize(),abstractPageWork.getTotalPages(),abstractPageWork.getTotalElements());
```
