
> [@and id in idList]
``` xml
<if test="null!=idList and idList.size>0"> and id in <foreach
collection="idList" index="index" item="item" open="(" separator=","
close=")">#{item}</foreach></if>
```

> [@@and id in idList]

``` xml
and id in <foreach
collection="idList" index="index" item="item" open="(" separator=","
close=")">#{item}</foreach>
```

[@and user_name = userName]
```xml
<if test="null!=userName and ''!=userName"> 
    and user_name = #{userName} 
</if>
```
[@and user_name like userName%]
```xml
<if test="null!=userName and ''!=userName">
    and user_name like  CONCAT(?,'%')
</if>
```

> [@id column]

```
-- column
id,code,user_name,mobile_phone,address,create_time
```

[@and id in #{idList:in} and user_name like #{userName:like}]
```xml
<if test="@com.vonchange.mybatis.tpl.MyOgnl@isNotEmpty(idList) and @com.vonchange.mybatis.tpl.MyOgnl@isNotEmpty(userName) "> 
    and id in 
    <foreach collection="idList" index="index" item="item" open="(" separator="," close=")">
    #{item}</foreach> 
    and user_name like  CONCAT('%',#{userName},'%')  
</if>
```