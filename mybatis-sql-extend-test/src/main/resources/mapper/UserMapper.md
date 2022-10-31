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
-- findListOrg
select * from user_base
 <where>
and user_name like CONCAT('%',#{userName},'%') 
<if test="null!=createTime"> 
[@and create_time  < createTime]
</if>
</where>

```