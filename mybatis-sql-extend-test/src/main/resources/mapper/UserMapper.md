```
-- findList
select * from user_base
where [@sql findListWhereSql]
```

> sql 片段
```
-- findListWhereSql
 1=1 
[@and user_name like userName] 
[@and create_time  < createTime]
```