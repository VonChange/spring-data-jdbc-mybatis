
推荐使用 spring data mybatis mini
[github地址](https://github.com/VonChange/spring-data-mybatis-mini)
[gitee地址](https://gitee.com/vonchange/spring-data-mybatis-mini)

**等同于spring data jdbc + mybatis 动态sql能力**

**大道至简**

1. 抛弃繁琐的xml 只使用mybatis模版引擎即动态sql能力 sql写在markdown文件里
   更容易书写和阅读 sql能统一管理查看

2. 底层基于springJdbc 而不是mybatis 更直接纯粹

3. 提供单表增删改(没有删除) 批量更新插入等基础方法 支持分页 读写分离

4. mybatis最大优点就是sql模版引擎
   我也有且仅有使用这部分功能(对于使用过mybatis的无学习成本)
   但底层使用springJDBC才是王道

5. 简化mybatis动态sql写法(可混用-写法还是mybatis那套) 比如

```
{@and id in idList} 等于
<if test="null!=idList and idList.size>0"> and id in <foreach
collection="idList" index="index" item="item" open="(" separator=","
close=")">#{item}</foreach></if>
```
![例子](mini.png)

== 与mybatis,jpa,hibernate,mybatis-plus等 比较


1. 比mybatis,mybatis-plus 无繁琐的xml配置 sql写在markdown文件里 写起来更快速
   还便于阅读
2. 无需resultType resultMap 复杂指定(mybatis xml啰嗦的配置) 只需定义方法名
3. 不允许查询sql放到@Select 和 @Query上 jpa复杂sql不容易书写还乱(虽然也能实现)
   保持mybatis风格写在文件里 统一管理 便于维护
4. 无缓存 缓存很容易 但更新是个难题 大多使用mybatis的公司都不会开启一级,二级缓存
   使用不当容易引起脏读 不如使用 SpringCache 等上层方案 自己控制
5. 无jpa根据方法名 (复杂点需要你学习思考,名字老长,不透明)
6. 无hibernate条件构造器(EntityWrapper,Criteria Query) 查询就该是sql
   配合动态sql能力 减少学习成本
7. 由于markdown 文件表述能力 无法像mybatis那样使用resultMap可以关联结果集映射
   我推荐的是如果没有复杂逻辑 多表join返回映射一个新的实体可直接透传到视图层VO
   或者自己代码里拼接 麻烦点但可控 
8. 比mybatis有简单的crud 比jpa使用jdbc+动态sql
   比mybatis-plus(越来越像hibernate,jpa 搞Criteria那套 还是mybatis吗
   sql还是要统一管理到文件)更简单 不使用xml 使用jdbc
9. 比较大多是无的功能 但细想这些都不是必须的甚至鸡肋 老老实实简单简单做个纯粹的ORM框架
