 
- [欢迎点击star github地址](https://github.com/VonChange/spring-data-jdbc-mybatis) 
- [欢迎点击star gitee地址](https://gitee.com/vonchange/spring-data-jdbc-mybatis)

**简单点 开发的方法简单点 繁琐的功能请省略 你有不是个AI**
### spring data jdbc 扩展 mybatis 动态sql能力
##### 官方spring data jdbc原生直接扩展 mybatis动态sql能力

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

##### 仿spring 6+ jdbcClient实现 更推荐crudClient
##### 更推荐自研版本spring data jdbc扩展 mybatis动态sql能力
* 底层 jdbcTemplate 复杂SQL才需要mybatis动态模板能力 无QueryDSL/queryMapper 提供crudClient 和jdbcClient

* 和spring data jdbc一样的追求简单,使用jdbcTemplate,调用jdbc。不提供缓存、延迟加载、QueryDSL等JPA或mybatis的许多特性。一个简单、有限的ORM

* 扩展并兼容mybatis动态sql能力(不依赖mybatis!提取了动态sql代码),可以应对复杂sql,如果换其他模板引擎(后续可以加)也是可以的,但有学习成本

* 复杂的SQL写在Markdown的代码片段中,不提供@Query和QueryDSL写法,但按方法名查找和扩展的findByExample可以应付大部分单表查询需求

* 简化mybatis动态sql写法 [easy-dynamic-sql.md](easy-dynamic-sql.md)

#####  这真是缺点?

- 缺点一：无QueryDSL/QueryWrapper 我是个懒人 既然service层里可以写QueryWrapper 为啥不可以写在service 为啥要重用？  
  要我独立个dao/Repository把数据层下放？想啥呢?!   
  我这个框架就只能在dao/Repository层 写确实太麻烦了 复杂的sql还得建个markdown文件
- 缺点二：没有数据缓存 审计 数据脱敏 IService接口 就不能替我做了吗 非得在应用层实现  
其实我这给了spring缓存框架等用武之地 自己实现可以增加代码量和工作量 在还没被ai替代之前 薅羊毛