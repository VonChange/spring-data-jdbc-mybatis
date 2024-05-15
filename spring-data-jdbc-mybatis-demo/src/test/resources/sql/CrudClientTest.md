```
-- findBigData
select * from user_info
<where> 
[@and user_name like userName]
</where>
```

```
-- findList
select * from user_info
where  is_delete=0
and user_code = #{userCode}

```