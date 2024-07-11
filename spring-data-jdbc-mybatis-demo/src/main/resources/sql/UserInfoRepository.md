## query  example

### base query columns
```
-- column
id,user_code,user_name,mobile_no,address,create_time,update_time,is_delete
```

```
-- findByUserCodes
select id,user_code,user_name a_userName,mobile_no,address from user_info  <where>
[@@and user_code in userCodes]</where>
```


```
-- findUserNameByCode
select user_name from user_info  where user_code = #{userCode} [@id isDelete]
```

```
-- isDelete
and is_delete=0
```

```
-- isDeleteOracle
and is_delete=1
```

```
-- findUserList
select [@id column] from user_info
where  is_delete=0
[@and user_name like userName%]
[@@and user_code in userCodes]
<if test="null!=createTime">  and create_time < #{createTime}  </if>

```


```
-- findUserBySearchParam
select * from user_info
<where> 
[@id whereSql]
</where>
${param.sort}
```

```sql
-- whereSql
[@@and user_name like param.userName]
[@and user_code in  param.userCodes]
[@and create_time  <= param.createTime]
```




```
-- findList
SELECT  [@id column] FROM user_info 
<where> 
[@@and id in #{idList:in} and user_name like #{userName:like}]
[@and user_name like userName%]
[@and id in idList]
<if test="null!=createTime">  and create_time < #{createTime}  </if>
</where>
```

> update

```
-- updateIsDelete
update user_info set is_delete = #{isDelete} where id =#{id}
```

```
-- batchUpdate
update user_info set is_delete = IFNULL(#{isDelete},is_delete),user_name =#{userName} where id =#{id}
```


```
-- findBigData
select * from user_info
<where> 
[@and user_name like userName]
</where>
```

