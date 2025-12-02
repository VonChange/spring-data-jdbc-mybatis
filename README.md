# Spring Data JDBC MyBatis

[![Maven Central](https://img.shields.io/maven-central/v/com.vonchange.common/spring-data-jdbc-mybatis.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.vonchange.common%20a:spring-data-jdbc-mybatis)
[![GitHub stars](https://img.shields.io/github/stars/vonchange/spring-data-jdbc-mybatis.svg?style=social)](https://github.com/VonChange/spring-data-jdbc-mybatis)
[![Gitee stars](https://gitee.com/vonchange/spring-data-jdbc-mybatis/badge/star.svg?theme=dark)](https://gitee.com/vonchange/spring-data-jdbc-mybatis)

[简体中文](./README.zh-CN.md)

**Keep it simple** — A lightweight Spring Data JDBC extension with MyBatis dynamic SQL capabilities (without MyBatis dependency).

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🚀 **Lightweight** | No caching, lazy loading, or QueryDSL - simple and focused |
| 📝 **Markdown SQL** | Write SQL in Markdown files for better readability |
| 🔧 **Dynamic SQL** | MyBatis dynamic SQL capability without MyBatis dependency |
| 🎯 **Simplified Syntax** | `[@and name = value]` auto-generates null-checked conditions |
| 🔍 **Method Name Query** | Support `findByUserNameAndAge`, `findByAgeIn`, etc. |
| 📦 **findByExample** | Extended query by example with `Like`, `In`, `OrderBy` |
| ⚡ **Batch Operations** | Batch insert and update support |
| 🔌 **Multi-DataSource** | Simple multi-datasource configuration |

## 🚀 Quick Start

### 1. Add Dependency

```xml
<!-- Spring Boot 2.x -->
<dependency>
    <groupId>com.vonchange.common</groupId>
    <artifactId>spring-data-jdbc-mybatis</artifactId>
    <version>2.5.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-commons</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```

### 2. Define Entity

```java
@Table(name = "user_info")
public class UserInfoDO {
    @Id
    private Long id;
    private String userCode;
    @Column(name = "user_name")
    private String userName;
    private String mobileNo;
    private LocalDateTime createTime;
    @Version
    private Integer version;
    // getter/setter...
}
```

### 3. Define Repository

```java
public interface UserInfoRepository extends CrudExtendRepository<UserInfoDO, Long> {
    // Method name query - SQL auto-generated
    List<UserInfoDO> findByUserCodeIn(List<String> userCodes);
    
    // Markdown SQL - defined in corresponding md file
    List<UserInfoDO> findUserList(@Param("userName") String userName,
                                  @Param("createTime") LocalDateTime createTime);
}
```

### 4. Write Markdown SQL

In `resources/sql/UserInfoRepository.md`:

```sql
-- findUserList
SELECT * FROM user_info
<where>
[@and user_name like userName%]
<if test="null!=createTime">and create_time < #{createTime}</if>
</where>
```

### 5. Enable Repository

```java
@SpringBootApplication
@EnableJdbcRepositories
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 6. Usage

```java
@Service
public class UserService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    
    public void demo() {
        // Method name query
        List<UserInfoDO> users = userInfoRepository.findByUserCodeIn(Arrays.asList("u001", "u002"));
        
        // Markdown SQL query
        List<UserInfoDO> list = userInfoRepository.findUserList("John%", LocalDateTime.now());
        
        // CRUD operations
        UserInfoDO user = new UserInfoDO();
        user.setUserName("John");
        userInfoRepository.insert(user);  // Insert (non-null fields only)
        
        user.setUserName("Jane");
        userInfoRepository.update(user);  // Update (non-null fields only)
    }
}
```

---

## 📖 User Guide

### Repository Types

| Type | Description | Use Case |
|------|-------------|----------|
| `CrudExtendRepository<T, ID>` | Full CRUD + extended methods | Entities requiring CRUD |
| `QueryRepository` | Query only, no entity binding | Complex reports, cross-table queries |

#### CrudExtendRepository Methods

```java
// Basic CRUD (from CrudRepository)
Optional<T> findById(ID id);
Iterable<T> findAll();
boolean existsById(ID id);
void deleteById(ID id);

// Extended methods
int insert(T entity);                    // Insert non-null fields
int update(T entity);                    // Update non-null fields
int insertBatch(List<T> list, boolean);  // Batch insert
int updateBatch(List<T> list, boolean);  // Batch update

// findByExample extensions
List<T> findAll(X example);              // Query by example
Optional<T> findOne(X example);          // Query single
Page<T> findAll(X example, Pageable p);  // Paginated query
Long count(X example);                   // Count
```

#### QueryRepository Example

```java
// Definition
public interface ReportQueryDao extends QueryRepository {
    List<ReportDTO> findSalesReport(@Param("startDate") Date start, @Param("endDate") Date end);
}

// resources/sql/ReportQueryDao.md
-- findSalesReport
SELECT product_name, SUM(amount) as total
FROM orders
WHERE order_date BETWEEN #{startDate} AND #{endDate}
GROUP BY product_name
```

---

### CrudClient / JdbcClient

Besides Repository pattern, programmatic API is also available:

```java
@Service
public class UserService {
    @Autowired
    private CrudClient crudClient;
    
    // Method 1: Use sqlId to reference Markdown SQL
    public List<UserInfoDO> findByCode(String code) {
        return crudClient.sqlId("findByCode")
                .namespace(this)  // corresponds to sql/UserService.md
                .param("code", code)
                .query(UserInfoDO.class).list();
    }
    
    // Method 2: Inline SQL
    public List<UserInfoDO> findAll() {
        return crudClient.jdbc()
                .sql("SELECT * FROM user_info WHERE is_delete = 0")
                .query(UserInfoDO.class).list();
    }
    
    // Entity operations
    public void save(UserInfoDO user) {
        crudClient.insert(user);        // Insert
        crudClient.update(user);        // Update
        crudClient.insertBatch(list, false);  // Batch insert
    }
}
```

---

### Method Name Query

Auto-generate SQL based on method name:

| Keyword | Example | Generated SQL |
|---------|---------|---------------|
| And | `findByNameAndAge` | `WHERE name = ? AND age = ?` |
| Or | `findByNameOrAge` | `WHERE name = ? OR age = ?` |
| In | `findByAgeIn` | `WHERE age IN (?, ?)` |
| NotIn | `findByAgeNotIn` | `WHERE age NOT IN (?, ?)` |
| Like | `findByNameLike` | `WHERE name LIKE ?` |
| NotLike | `findByNameNotLike` | `WHERE name NOT LIKE ?` |
| Between | `findByAgeBetween` | `WHERE age BETWEEN ? AND ?` |
| Lt / Before | `findByAgeLt` | `WHERE age < ?` |
| Lte | `findByAgeLte` | `WHERE age <= ?` |
| Gt / After | `findByAgeGt` | `WHERE age > ?` |
| Gte | `findByAgeGte` | `WHERE age >= ?` |
| Not | `findByNameNot` | `WHERE name != ?` |
| OrderBy | `findByAgeOrderByNameDesc` | `WHERE age = ? ORDER BY name DESC` |

**Examples:**

```java
public interface UserRepository extends CrudExtendRepository<User, Long> {
    // WHERE user_code IN (?, ?)
    List<User> findByUserCodeIn(List<String> codes);
    
    // WHERE user_code IN (?, ?) ORDER BY create_time DESC
    List<User> findByUserCodeInOrderByCreateTimeDesc(List<String> codes);
    
    // WHERE create_time BETWEEN ? AND ?
    List<User> findByCreateTimeBetween(List<LocalDateTime> times);
    
    // Pagination - first parameter is Pageable
    Page<User> findPageByUserCodeIn(Pageable pageable, List<String> codes);
}
```

---

### Markdown SQL Syntax

SQL is defined in `resources/sql/{RepositoryName}.md`:

```markdown
## Query Examples

### Define columns
​```sql
-- column
id, user_code, user_name, mobile_no, create_time
​```

### Query method
​```sql
-- findUserList
SELECT [@id column] FROM user_info
<where>
[@and user_name like userName%]
[@and user_code in userCodes]
<if test="null!=createTime">and create_time < #{createTime}</if>
</where>
​```

### Reference other fragments
​```sql
-- findByCondition
SELECT * FROM user_info
<where>
[@id commonCondition]
</where>
​```

​```sql
-- commonCondition
and is_delete = 0
[@and status = status]
​```
```

**Syntax:**

| Syntax | Description |
|--------|-------------|
| `-- sqlId` | SQL fragment ID, corresponds to Repository method name |
| `[@id xxx]` | Reference other SQL fragment |
| `#{param}` | MyBatis parameter placeholder |
| `${param}` | Direct substitution (beware of SQL injection) |

---

### Simplified Dynamic SQL Syntax

Framework provides simplified dynamic SQL syntax that auto-generates MyBatis XML:

#### Condition `[@...]`

```sql
-- Original
[@and user_name = userName]

-- Equivalent to
<if test="null!=userName and ''!=userName">
    and user_name = #{userName}
</if>
```

#### No null check `[@@...]`

```sql
-- Original
[@@and user_code in userCodes]

-- Equivalent to (no null check, direct output)
and user_code in <foreach collection="userCodes" item="item" open="(" separator="," close=")">#{item}</foreach>
```

#### Like Query

```sql
-- Right wildcard
[@and user_name like userName%]

-- Left wildcard
[@and user_name like %userName]

-- Both sides
[@and user_name like userName]
```

#### In Query

```sql
-- Simplified
[@and id in idList]

-- Equivalent to
<if test="null!=idList and idList.size>0">
    and id in <foreach collection="idList" item="item" open="(" separator="," close=")">#{item}</foreach>
</if>
```

#### Compound Conditions

```sql
-- Multiple conditions (all must be satisfied to output)
[@and id in #{idList:in} and user_name like #{userName:like}]

-- Equivalent to
<if test="@com.vonchange.mybatis.tpl.MyOgnl@isNotEmpty(idList) and @com.vonchange.mybatis.tpl.MyOgnl@isNotEmpty(userName)">
    and id in <foreach...>...</foreach>
    and user_name like CONCAT('%', #{userName}, '%')
</if>
```

---

### findByExample Extension

Dynamic query using Example objects:

```java
// Define Example class
@Data
@Builder
public class UserExample {
    private String userNameLike;        // user_name LIKE ?
    private List<String> userCodeIn;    // user_code IN (?, ?)
    private Boolean createTimeDesc;     // ORDER BY create_time DESC
    private LocalDateTime createTimeLte; // create_time <= ?
}

// Usage
List<UserInfoDO> users = userRepository.findAll(
    UserExample.builder()
        .userCodeIn(Arrays.asList("u001", "u002"))
        .userNameLike("John%")
        .createTimeDesc(true)
        .build()
);

// Pagination
Page<UserInfoDO> page = userRepository.findAll(example, PageRequest.of(0, 10));

// Count
Long count = userRepository.count(example);
```

**Example Property Naming Rules:**

| Suffix | Description | Example |
|--------|-------------|---------|
| None | Equals | `userName` → `user_name = ?` |
| `Like` | Like query | `userNameLike` → `user_name LIKE ?` |
| `In` | IN query | `userCodeIn` → `user_code IN (...)` |
| `NotIn` | NOT IN | `statusNotIn` → `status NOT IN (...)` |
| `Lt` | Less than | `ageLt` → `age < ?` |
| `Lte` | Less than or equal | `ageLte` → `age <= ?` |
| `Gt` | Greater than | `ageGt` → `age > ?` |
| `Gte` | Greater than or equal | `ageGte` → `age >= ?` |
| `Desc` | Descending order | `createTimeDesc` → `ORDER BY create_time DESC` |
| `Asc` | Ascending order | `createTimeAsc` → `ORDER BY create_time ASC` |

---

### Batch Operations

```java
// Batch insert
List<UserInfoDO> users = new ArrayList<>();
for (int i = 0; i < 1000; i++) {
    users.add(UserInfoDO.builder().userName("user" + i).build());
}
userRepository.insertBatch(users, false);

// Batch update
userRepository.updateBatch(users, false);

// Custom batch update with @BatchUpdate annotation
@BatchUpdate
int batchUpdate(List<UserInfoDO> list);
```

**Note:** Batch operations require adding to database connection string:
```
rewriteBatchedStatements=true&allowMultiQueries=true
```

---

### Pagination & Big Data Query

#### Regular Pagination

```java
// Repository method
Page<UserInfoDO> findUserList(Pageable pageable, @Param("userName") String userName);

// Usage
Pageable pageable = PageRequest.of(0, 10);
Page<UserInfoDO> page = userRepository.findUserList(pageable, "John%");

System.out.println("Total: " + page.getTotalElements());
System.out.println("Pages: " + page.getTotalPages());
page.getContent().forEach(System.out::println);
```

#### Big Data Batch Processing

```java
AbstractPageWork<UserInfoDO> pageWork = new AbstractPageWork<UserInfoDO>() {
    @Override
    protected void doPage(List<UserInfoDO> list, int pageNum, Map<String, Object> extData) {
        // Batch processing logic
        list.forEach(user -> process(user));
    }
    
    @Override
    protected int getPageSize() {
        return 500;  // 500 per batch
    }
};

crudClient.sqlId("findBigData")
    .namespace(this)
    .param("status", 1)
    .queryBatch(UserInfoDO.class, pageWork);

System.out.println("Total processed: " + pageWork.getTotalElements());
```

---

## 📋 Annotation Reference

| Annotation | Source | Description |
|------------|--------|-------------|
| `@Table` | JPA | Specify table name |
| `@Id` | JPA / Spring Data | Primary key field |
| `@Column` | JPA | Specify column name |
| `@Version` | JPA / Spring Data | Optimistic lock version (Long/Integer) |
| `@Transient` | JPA / Spring Data | Non-persistent field |
| `@InsertOnlyProperty` | Framework | Only effective on insert, ignored on update |
| `@ReadOnlyProperty` | Spring Data | Read-only, ignored on both insert and update |
| `@InsertReturn` | Framework | Field returned after insert |
| `@BatchUpdate` | Framework | Mark batch update method |
| `@DataSourceKey` | Framework | Specify datasource |

---

## 🔌 Multi-DataSource Configuration

### 1. Configure DataSources

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
    public DataSource orderDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean("dataSourceWrapper")
    public DataSourceWrapper dataSourceWrapper(@Qualifier("dataSource") DataSource ds) {
        return new DataSourceWrapper(ds, "dataSource");
    }
    
    @Bean("orderDataSourceWrapper")
    public DataSourceWrapper orderDataSourceWrapper(@Qualifier("orderDataSource") DataSource ds) {
        return new DataSourceWrapper(ds, "orderDataSource");
    }
}
```

### 2. Specify DataSource

```java
@DataSourceKey("orderDataSource")
public interface OrderQueryDao extends QueryRepository {
    List<OrderDTO> findOrders(@Param("userId") Long userId);
}
```

---

## 🔄 Official Spring Data JDBC Extension

If you only want to extend official Spring Data JDBC with MyBatis dynamic SQL:

```java
@Configuration
public class MybatisQuerySupportConfig {
    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        return new MybatisJdbcTemplate(dataSource) {
            @Override
            protected Dialect dialect() {
                return new MySQLDialect();
            }
        };
    }
}
```

Usage:

```java
public interface UserRepository extends CrudRepository<User, Long> {
    @Query("user.queryByCode")  // Reference queryByCode in sql/user.md
    List<User> queryByCode(@Param("code") String code);
}
```

> **Note:** SpEL support in `@Query` requires Spring Data JDBC 3.0+ (JDK 17+)

---

## 📚 Related Documentation

- [Dynamic SQL Simplified Syntax](easy-dynamic-sql.md)
- [Method Name Query Guide](method-name-query.md)
- [Batch Update Guide](bach-update.md)
- [Multi-DataSource Configuration](multi-datasource.md)
- [CrudRepository Extension Guide](curd-repository.md)

---

## 📄 License

[Apache License 2.0](LICENSE)
