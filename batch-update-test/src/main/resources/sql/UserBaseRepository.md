> 查询用户列表
```
-- findListX

select * from user_base
where user_name = #{userName}
and create_time  <= #{createTime}
```

```
-- findListBase
select * from user_base  where user_name = #{userName}
```


```
-- findOne
select * from user_base
where user_name = #{userName}
```
> 查询用户列表 含sql 片段

```
-- findList
select * from user_base
where 
user_name = #{userName} 
[@@and is_delete = isDelete.value]
[@@and create_time  < createTime]

```

```
-- findListVo
select * from user_base
where [@id findListWhereSql]
```

```
-- findListByBean
select * from user_base
<where> 
[@@and user_name like param.userName]
[@and user_name like param.userName%]
[@and create_time  <= param.createTime]
</where>
```




> sql 片段
```
-- findListWhereSql
user_name = #{userName} and 1=1
[@and create_time  < createTime]
```

> 查询用户名 返回1个字段的情况 比如查询行数等
```
-- findUserName
SELECT user_name FROM user_base 
WHERE user_name = #{userName}
```


> 根据Id列表查询列表 
```
-- findListByIdsx
SELECT * FROM user_base 
<where>
<if test="null!=userName"> and user_name <> #{userName} </if>
<if test="null!=idList and idList.size>0">  and id in <foreach collection="idList" index="index" item="item" open="(" separator="," close=")">#{item}</foreach></if>
<if test="null!=createTime">  and create_time < #{createTime}  </if>
</where>

```

>  [@and create_time < createTime]

> 根据Id列表查询列表 简写if 和in查询 可混用
```
-- findListByIds
SELECT * FROM user_base 
<where> 
[@@and id in #{idList:in} and user_name like #{userName:like}]
[@and user_name like userName%]
[@and id in idList]
<if test="null!=createTime">  and create_time < #{createTime}  </if>
</where>
```

> 更新方法 update 开头

```
-- updateIsDelete
update user_base set is_delete = #{isDelete} where id =#{id}
```

```
-- batchUpdate
update user_base set is_delete = IFNULL(#{isDelete},is_delete),user_name =#{userName} where id =#{id}
```


```
-- batchInsert
insert into user_base(`user_name`,`mobile_phone`,create_time) values
(#{userName},#{mobilePhone},#{createTime}) 
```

```
-- updateTest
<foreach collection="list" index="index" item="item" >
insert into user_base(`user_name`,`mobile_phone`) values (#{item.userName},#{item.firstPhone});
</foreach>

```

```
-- insertBatchNormal
insert into user_base(`user_name`,`mobile_phone`,create_time) values 
<foreach collection="list" index="index" item="item" separator="," >
(#{item.userName},#{item.mobilePhone},#{item.createTime})
</foreach>

```

```
-- insertBatchNormalX
insert into user_base(`id`,`code`,`user_name`,`mobile_phone`,`is_delete`,`create_time`,`update_time`,`head_image_data`) values
<foreach collection="list" index="index" item="item" separator="," >
(IFNULL(#{item.id},`id`),IFNULL(#{item.code},`code`),IFNULL(#{item.userName},`user_name`),IFNULL(#{item.mobilePhone},`mobile_phone`)
,IFNULL(#{item.isDelete},`is_delete`),IFNULL(#{item.createTime},now()),IFNULL(#{item.updateTime},now()),IFNULL(#{item.headImageData},`head_image_data`))
</foreach>
```


```
-- findBigData
select * from user_base
<where> 
[@and user_name like userName]
</where>
```


```
-- findLongList
select id from user_base
```

```
-- findInList

select * from user_base
where 1=1
[@@and user_name in userNames]
[@@and is_delete in  isDeletes]
```