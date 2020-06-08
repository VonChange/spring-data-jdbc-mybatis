
推荐使用 spring data mybatis mini
[github地址](https://github.com/VonChange/spring-data-mybatis-mini)
[gitee地址](https://gitee.com/vonchange/spring-data-mybatis-mini)

**等同于spring data jdbc + mybatis 动态sql能力**

**大道至简 麻雀虽小 五脏俱全**

1. 抛弃繁琐的xml 只使用mybatis模版引擎即动态sql能力 sql写在markdown文件里
   更容易书写和阅读 sql能统一管理查看

2. 底层基于springJdbc 而不是mybatis 更直接纯粹

3. 提供单表增删改(没有删除) 批量更新插入等基础方法 支持分页 读写分离

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
![例子](mini.png)
== why not spring data jdbc,jpa,hibernate,mybaits,mybatis-plus等

1. 基于spring data jdbc理念但扩展使用mybatis动态sql能力 对于复杂点查询支持更好
2. 相比jpa 底层使用hibernate(当然也能sql) 只有sql 基于spring jdbc
   无jpa根据方法名(复杂点需要你学习思考,名字老长,不透明) 简单没有黑魔法 学习成本低
   sql写在markdown里,纯jdbc更易于调优
3. 比价mybatis 没有cache,复杂join映射实体,无resultType,resultMap配置
   扩展单表CRUD 只用他的动态sql能力的模版引擎和sql放到文件管理思想 去繁就简
   取其优点抛弃鸡肋功能
4. 相比mybatis-plus等扩展mybatis框架 他们做的越来越像hibernate,jpa
   搞Criteria那套 基本脱离mybatis优点 
5. 查询只提供一个选择 就是sql写在markdown文件里 不会提供类似hibernate Criteria
   多种选择说是灵活但项目多种有多种实现写法 你会有打人的冲动 
6. 缓存可以用SpringCache等上层方案 
7. 查询只能映射单一实体(VO,DO,DTO均可) 但现在推荐减少JOIN 推荐代码里join 后期会尝试写新的组件sqlHelper方式简化
 

