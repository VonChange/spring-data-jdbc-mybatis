```sql
-- queryByUserCode
select * from user_info where user_code = #{userCode}
```

```sql
-- queryByBean
select * from user_info 
<where>
  [@and user_code = user.userCode]
  [@and user_name like user.userName%]
 </where>
```

```sql
-- queryByUserCodes
select * from user_info where 1=1 [@@and user_code in userCodes]
```