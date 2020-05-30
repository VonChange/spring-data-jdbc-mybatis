> 查询用户列表
```
-- findListX

select * from user_base
where user_name = #{userName}
and create_time  < #{createTime}
```

> 查询用户列表 含sql 片段

```
-- findList
select * from user_base
where {@sql findListWhereSql}
```

> sql 片段
```
-- findListWhereSql
user_name = #{userName} and 1=1
and create_time  < #{createTime}
```

> 查询用户名 返回1个字段的情况 比如查询行数等
```
-- findUserName
SELECT user_name FROM user_base WHERE user_name = #{userName}
```


> 根据Id列表查询列表 
```
-- findListByIds
SELECT * FROM user_base 
<where> 
[@and id in #{idList:in} and user_name like #{userName:like}]
[@and user_name like userName%]
[@and id in idList]
<if test="null!=createTime">  and create_time < #{createTime}  </if>
</where>

```

> 根据Id列表查询列表 简写if 和in查询
```
-- findListByIds
SELECT * FROM user_base <where> 
[@and user_name <> userName]
[@and id in idList]
[@and create_time < createTime]
</where>
```

> 更新方法 update 开头

```
-- updateIsDelete
update user_base set is_delete = #{isDelete} where id =#{id}
```

