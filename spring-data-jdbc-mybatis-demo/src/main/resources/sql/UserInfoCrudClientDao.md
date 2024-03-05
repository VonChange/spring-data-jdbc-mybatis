```
-- findList
select * from user_info
where  is_delete=0
and user_code = #{userCode}

```