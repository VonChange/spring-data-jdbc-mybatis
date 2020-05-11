
推荐使用 spring data mybatis mini
[github地址](https://github.com/VonChange/spring-data-mybatis-mini)
[gitee地址](https://gitee.com/vonchange/spring-data-mybatis-mini)
> 1. 抛弃繁琐的xml 只使用mybatis模版引擎即动态sql能力 sql写在markdown文件里
>    便于书写和阅读

> 2. 底层jdbc使用spring jdbc

> 3. 简化mybatis动态sql写法(可混用-写法还是mybatis那套) 比如
```
{@and id in idList} 等于
<if test="null!=idList and idList.size>0"> and id in <foreach
collection="idList" index="index" item="item" open="(" separator=","
close=")">#{item}</foreach></if>
```

== 与mybatis,mybatis-plus,jpa,hibernate 比较

1. sql写在markdown文件里 写起来舒服 便于阅读
2. 无需resultType resultMap 复杂指定(mybatis xml啰嗦的配置) 只需定义方法名
3. 不允许查询sql放到@Select 和 @Query上 jpa复杂sql不容易书写还乱(虽然也能实现)
   保持mybatis风格写在文件里 统一管理 便于维护
4. 无缓存 缓存很容易 但更新是个难题 大多使用mybatis的公司都不会开启一级,二级缓存
   使用不当容易引起脏读 不如使用 SpringCache 等上层方案 自己控制
5. 不会像jpa根据方法名生成sql(需要你学习思考,复杂点名字老长,不透明)
6. 无hibernate条件构造器(EntityWrapper,Criteria Query) 查询就该是sql
   配合动态sql能力 减少学习成本
7. 由于markdown 文件表述能力 无法像mybatis那样使用resultMap可以关联结果集映射
   我推荐的是如果没有复杂逻辑 多表join返回映射一个新的实体可直接透传到视图层VO
   或者自己代码里拼接 麻烦点但可控 
8. 比较大多是无的功能 但细想这些都不是必须的甚至鸡肋 老老实实简单简单做个纯粹的ORM框架
