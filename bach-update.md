
> > need rewriteBatchedStatements=true&allowMultiQueries=true
```java
public interface UserInfoRepository extends CrudJdbcRepository<UserInfoDO, Long> {
    @BatchUpdate
    int batchUpdate(List<UserInfoDO> list);
}
```