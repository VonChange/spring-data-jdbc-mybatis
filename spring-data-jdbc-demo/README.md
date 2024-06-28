configuration
```
@Configuration
public class MybatisQuerySupportConfig {
    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        return new MybatisJdbcTemplate(dataSource) {@Override protected Dialect dialect() {return new MySQLDialect();}};
    }
}
```
dependency
```xml
 <dependency>
      <groupId>com.vonchange.common</groupId>
       <artifactId>jdbc-mybatis</artifactId>
       <version>${jdbc.mybatis}</version>
</dependency>

```
use
```
    @Query("user.queryByUserCode")
    List<UserDTO> queryByUserCode(@Param("userCode") String userCode);
```