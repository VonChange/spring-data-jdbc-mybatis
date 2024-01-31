== mybatis-spring-boot extend

### dependency
```
    <dependency>
        <groupId>com.vonchange.common</groupId>
        <artifactId>mybatis-sql-extend</artifactId>
        <version>${spring.mybatis.mini}</version>
    </dependency>
```
### config
[config](mybatis-sql-extend-test/src/main/java/com/vonchange/mybatis/test/config/SimpleLanguageDriver.java)
```
public class SimpleLanguageDriver extends XMLLanguageDriver implements LanguageDriver {
    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        String sqlInXml = MybatisSqlLanguageUtil.sqlInXml("mapper",script,new MySQLDialect());
        return super.createSqlSource(configuration, sqlInXml, parameterType);
    }
}
```

![例子](https://image.yonghuivip.com/20221031/4a9e97a668f84bbcbf8b8214630efb4d/sql.png)
> [UserMapper.md 文件](mybatis-sql-extend-test/src/main/resources/mapper/UserMapper.md)

```
-- 配置
mybatis:
  default-scripting-language-driver: com.vonchange.mybatis.test.config.SimpleLanguageDriver
  configuration:
    map-underscore-to-camel-case: true
 
```