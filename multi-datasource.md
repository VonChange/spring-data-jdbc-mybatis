
```java
@Configuration
public class DbConfig {
    @Bean(name = "dataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource mainDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean(name = "orderDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.order")
    public DataSource oldDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean("dataSourceWrapper")
    public DataSourceWrapper dataSourceWrapper(@Qualifier("dataSource")DataSource dataSource) {
        return new DataSourceWrapper(dataSource,"dataSource");
    }
 

    @Bean("orderDataSourceWrapper")
    public DataSourceWrapper oldDataSourceWrapper(@Qualifier("orderDataSource")DataSource dataSource) {
        return new DataSourceWrapper(dataSource,"orderDataSource");
    }

}
```

```java
@DataSourceKey("orderDataSource")
public interface OrderQueryDao extends QueryRepository {
}
```