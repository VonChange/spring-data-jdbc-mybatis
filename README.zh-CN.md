# Spring Data JDBC MyBatis

[![Maven Central](https://img.shields.io/maven-central/v/com.vonchange.common/spring-data-jdbc-mybatis.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.vonchange.common%20a:spring-data-jdbc-mybatis)
[![GitHub stars](https://img.shields.io/github/stars/vonchange/spring-data-jdbc-mybatis.svg?style=social)](https://github.com/VonChange/spring-data-jdbc-mybatis)
[![Gitee stars](https://gitee.com/vonchange/spring-data-jdbc-mybatis/badge/star.svg?theme=dark)](https://gitee.com/vonchange/spring-data-jdbc-mybatis)

**大道至简，返璞归真** —— 一个轻量级的 Spring Data JDBC 扩展框架，提供 MyBatis 动态 SQL 能力（不依赖 MyBatis）。

> 简单点，开发的方法简单点，繁琐的功能请省略。

## 💡 为什么选择本框架？

### 独创亮点

**1. Markdown SQL** - SQL 写在 Markdown 中，可读性革命
```markdown
​```sql
-- findUserList
SELECT * FROM user_info
<where>[@and user_name like userName%]</where>
​```
```

**2. 简化动态 SQL** - 一行代码替代 MyBatis 6 行 XML
```sql
-- 本框架
[@and user_name like userName%]

-- 等价 MyBatis XML
<if test="null!=userName and ''!=userName">
    and user_name like CONCAT(#{userName}, '%')
</if>
```

**3. 零依赖动态 SQL** - 提取 MyBatis 动态 SQL 能力，无需引入 MyBatis

### 与同类框架对比

| 特性 | Spring Data JDBC | MyBatis | MyBatis-Plus | 本框架 |
|------|------------------|---------|--------------|--------|
| 动态 SQL | ❌ | ✅ XML | ✅ Wrapper | ✅ **简化语法** |
| SQL 管理 | @Query | XML | @Select | **Markdown** |
| 方法名查询 | ✅ | ❌ | ❌ | ✅ |
| findByExample | ❌ | ❌ | ✅ | ✅ **增强版** |
| 非空字段更新 | ❌ | ✅ | ✅ | ✅ |
| 学习成本 | 低 | 高 | 中 | **低** |
| 依赖量 | 少 | 多 | 多 | **少** |

## ✨ 核心特性

| 特性 | 说明 |
|------|------|
| 🚀 **轻量级** | 不提供缓存、延迟加载、QueryDSL，保持简单专注 |
| 📝 **Markdown SQL** | SQL 写在 Markdown 文件中，更便于编写和阅读 |
| 🔧 **动态 SQL** | 提取 MyBatis 动态 SQL 能力，不依赖 MyBatis 框架 |
| 🎯 **简化语法** | `[@and name = value]` 自动生成带判空的条件语句 |
| 🔍 **方法名查询** | 支持 `findByUserNameAndAge`、`findByAgeIn` 等 |
| 📦 **findByExample** | 扩展版按属性查询，支持 `Like`、`In`、`OrderBy` 等 |
| ⚡ **批量操作** | 支持批量插入、批量更新 |
| 🔌 **多数据源** | 简单的多数据源配置支持 |

## 🚀 快速开始

### 1. 添加依赖

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

### 2. 定义实体类

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

### 3. 定义 Repository

```java
public interface UserInfoRepository extends CrudExtendRepository<UserInfoDO, Long> {
    // 方法名查询 - 自动生成 SQL
    List<UserInfoDO> findByUserCodeIn(List<String> userCodes);
    
    // Markdown SQL - 在对应 md 文件中定义
    List<UserInfoDO> findUserList(@Param("userName") String userName,
                                  @Param("createTime") LocalDateTime createTime);
}
```

### 4. 编写 Markdown SQL

在 `resources/sql/UserInfoRepository.md` 中定义：

```sql
-- findUserList
SELECT * FROM user_info
<where>
[@and user_name like userName%]
<if test="null!=createTime">and create_time < #{createTime}</if>
</where>
```

### 5. 启用 Repository

```java
@SpringBootApplication
@EnableJdbcRepositories
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 6. 使用

```java
@Service
public class UserService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    
    public void demo() {
        // 方法名查询
        List<UserInfoDO> users = userInfoRepository.findByUserCodeIn(Arrays.asList("u001", "u002"));
        
        // Markdown SQL 查询
        List<UserInfoDO> list = userInfoRepository.findUserList("张%", LocalDateTime.now());
        
        // CRUD 操作
        UserInfoDO user = new UserInfoDO();
        user.setUserName("张三");
        userInfoRepository.insert(user);  // 插入（仅非空字段）
        
        user.setUserName("李四");
        userInfoRepository.update(user);  // 更新（仅非空字段）
    }
}
```

---

## 📖 使用指南

### Repository 类型

| 类型 | 说明 | 使用场景 |
|------|------|----------|
| `CrudExtendRepository<T, ID>` | 完整 CRUD + 扩展方法 | 需要增删改查的实体 |
| `QueryRepository` | 仅查询，无实体绑定 | 复杂报表、跨表查询 |

#### CrudExtendRepository 方法

```java
// 基础 CRUD（继承自 CrudRepository）
Optional<T> findById(ID id);
Iterable<T> findAll();
boolean existsById(ID id);
void deleteById(ID id);

// 扩展方法
int insert(T entity);                    // 插入非空字段
int update(T entity);                    // 更新非空字段
int insertBatch(List<T> list, boolean);  // 批量插入
int updateBatch(List<T> list, boolean);  // 批量更新

// findByExample 扩展
List<T> findAll(X example);              // 按 Example 查询
Optional<T> findOne(X example);          // 查询单条
Page<T> findAll(X example, Pageable p);  // 分页查询
Long count(X example);                   // 统计数量
```

#### QueryRepository 示例

```java
// 定义
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

除了 Repository 模式，还提供编程式 API：

```java
@Service
public class UserService {
    @Autowired
    private CrudClient crudClient;
    
    // 方式一：使用 sqlId 引用 Markdown SQL
    public List<UserInfoDO> findByCode(String code) {
        return crudClient.sqlId("findByCode")
                .namespace(this)  // 对应 sql/UserService.md
                .param("code", code)
                .query(UserInfoDO.class).list();
    }
    
    // 方式二：直接写 SQL
    public List<UserInfoDO> findAll() {
        return crudClient.jdbc()
                .sql("SELECT * FROM user_info WHERE is_delete = 0")
                .query(UserInfoDO.class).list();
    }
    
    // 实体操作
    public void save(UserInfoDO user) {
        crudClient.insert(user);        // 插入
        crudClient.update(user);        // 更新
        crudClient.insertBatch(list, false);  // 批量插入
    }
}
```

---

### 方法名查询

支持根据方法名自动生成 SQL：

| 关键字 | 示例 | 生成 SQL |
|--------|------|----------|
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

**示例：**

```java
public interface UserRepository extends CrudExtendRepository<User, Long> {
    // WHERE user_code IN (?, ?)
    List<User> findByUserCodeIn(List<String> codes);
    
    // WHERE user_code IN (?, ?) ORDER BY create_time DESC
    List<User> findByUserCodeInOrderByCreateTimeDesc(List<String> codes);
    
    // WHERE create_time BETWEEN ? AND ?
    List<User> findByCreateTimeBetween(List<LocalDateTime> times);
    
    // 分页 - 第一个参数为 Pageable
    Page<User> findPageByUserCodeIn(Pageable pageable, List<String> codes);
}
```

---

### Markdown SQL 语法

SQL 定义在 `resources/sql/{RepositoryName}.md` 文件中：

```markdown
## 查询示例

### 定义列
​```sql
-- column
id, user_code, user_name, mobile_no, create_time
​```

### 查询方法
​```sql
-- findUserList
SELECT [@id column] FROM user_info
<where>
[@and user_name like userName%]
[@and user_code in userCodes]
<if test="null!=createTime">and create_time < #{createTime}</if>
</where>
​```

### 引用其他片段
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

**语法说明：**

| 语法 | 说明 |
|------|------|
| `-- sqlId` | SQL 片段 ID，对应 Repository 方法名 |
| `[@id xxx]` | 引用其他 SQL 片段 |
| `#{param}` | MyBatis 参数占位符 |
| `${param}` | 直接替换（注意 SQL 注入风险） |

---

### 动态 SQL 简化语法

框架提供简化的动态 SQL 语法，自动生成 MyBatis XML：

#### 条件语句 `[@...]`

```sql
-- 原始写法
[@and user_name = userName]

-- 等价于
<if test="null!=userName and ''!=userName">
    and user_name = #{userName}
</if>
```

#### 不判空 `[@@...]`

```sql
-- 原始写法
[@@and user_code in userCodes]

-- 等价于（不判空，直接输出）
and user_code in <foreach collection="userCodes" item="item" open="(" separator="," close=")">#{item}</foreach>
```

#### Like 查询

```sql
-- 右模糊
[@and user_name like userName%]

-- 左模糊  
[@and user_name like %userName]

-- 全模糊
[@and user_name like userName]
```

#### In 查询

```sql
-- 简化写法
[@and id in idList]

-- 等价于
<if test="null!=idList and idList.size>0">
    and id in <foreach collection="idList" item="item" open="(" separator="," close=")">#{item}</foreach>
</if>
```

#### 复合条件

```sql
-- 多条件组合（所有条件都满足才输出）
[@and id in #{idList:in} and user_name like #{userName:like}]

-- 等价于
<if test="@com.vonchange.mybatis.tpl.MyOgnl@isNotEmpty(idList) and @com.vonchange.mybatis.tpl.MyOgnl@isNotEmpty(userName)">
    and id in <foreach...>...</foreach>
    and user_name like CONCAT('%', #{userName}, '%')
</if>
```

---

### findByExample 扩展

通过 Example 对象进行动态查询：

```java
// 定义 Example 类
@Data
@Builder
public class UserExample {
    private String userNameLike;        // user_name LIKE ?
    private List<String> userCodeIn;    // user_code IN (?, ?)
    private Boolean createTimeDesc;     // ORDER BY create_time DESC
    private LocalDateTime createTimeLte; // create_time <= ?
}

// 使用
List<UserInfoDO> users = userRepository.findAll(
    UserExample.builder()
        .userCodeIn(Arrays.asList("u001", "u002"))
        .userNameLike("张%")
        .createTimeDesc(true)
        .build()
);

// 分页
Page<UserInfoDO> page = userRepository.findAll(example, PageRequest.of(0, 10));

// 统计
Long count = userRepository.count(example);
```

**Example 属性命名规则：**

| 后缀 | 说明 | 示例 |
|------|------|------|
| 无后缀 | 等于 | `userName` → `user_name = ?` |
| `Like` | 模糊查询 | `userNameLike` → `user_name LIKE ?` |
| `In` | IN 查询 | `userCodeIn` → `user_code IN (...)` |
| `NotIn` | NOT IN | `statusNotIn` → `status NOT IN (...)` |
| `Lt` | 小于 | `ageLt` → `age < ?` |
| `Lte` | 小于等于 | `ageLte` → `age <= ?` |
| `Gt` | 大于 | `ageGt` → `age > ?` |
| `Gte` | 大于等于 | `ageGte` → `age >= ?` |
| `Desc` | 降序排序 | `createTimeDesc` → `ORDER BY create_time DESC` |
| `Asc` | 升序排序 | `createTimeAsc` → `ORDER BY create_time ASC` |

---

### 批量操作

```java
// 批量插入
List<UserInfoDO> users = new ArrayList<>();
for (int i = 0; i < 1000; i++) {
    users.add(UserInfoDO.builder().userName("user" + i).build());
}
userRepository.insertBatch(users, false);

// 批量更新
userRepository.updateBatch(users, false);

// 使用 @BatchUpdate 注解的自定义批量更新
@BatchUpdate
int batchUpdate(List<UserInfoDO> list);
```

**注意：** 批量操作需要在数据库连接字符串中添加：
```
rewriteBatchedStatements=true&allowMultiQueries=true
```

---

### 分页与大数据查询

#### 普通分页

```java
// Repository 方法
Page<UserInfoDO> findUserList(Pageable pageable, @Param("userName") String userName);

// 使用
Pageable pageable = PageRequest.of(0, 10);
Page<UserInfoDO> page = userRepository.findUserList(pageable, "张%");

System.out.println("总数: " + page.getTotalElements());
System.out.println("总页数: " + page.getTotalPages());
page.getContent().forEach(System.out::println);
```

#### 大数据分批处理

```java
AbstractPageWork<UserInfoDO> pageWork = new AbstractPageWork<UserInfoDO>() {
    @Override
    protected void doPage(List<UserInfoDO> list, int pageNum, Map<String, Object> extData) {
        // 分批处理逻辑
        list.forEach(user -> process(user));
    }
    
    @Override
    protected int getPageSize() {
        return 500;  // 每批 500 条
    }
};

crudClient.sqlId("findBigData")
    .namespace(this)
    .param("status", 1)
    .queryBatch(UserInfoDO.class, pageWork);

System.out.println("处理总数: " + pageWork.getTotalElements());
```

---

## 📋 注解参考

| 注解 | 来源 | 说明 |
|------|------|------|
| `@Table` | JPA | 指定表名 |
| `@Id` | JPA / Spring Data | 主键字段 |
| `@Column` | JPA | 指定列名 |
| `@Version` | JPA / Spring Data | 乐观锁版本号（支持 Long/Integer） |
| `@Transient` | JPA / Spring Data | 非持久化字段 |
| `@InsertOnlyProperty` | 框架提供 | 仅插入时有效，更新时忽略 |
| `@ReadOnlyProperty` | Spring Data | 只读属性，插入和更新都忽略 |
| `@InsertReturn` | 框架提供 | 插入后返回的字段 |
| `@BatchUpdate` | 框架提供 | 标记批量更新方法 |
| `@DataSourceKey` | 框架提供 | 指定数据源 |

---

## 🔌 多数据源配置

### 1. 配置数据源

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

### 2. 指定数据源

```java
@DataSourceKey("orderDataSource")
public interface OrderQueryDao extends QueryRepository {
    List<OrderDTO> findOrders(@Param("userId") Long userId);
}
```

---

## 🔄 官方 Spring Data JDBC 扩展方式

如果你想在官方 Spring Data JDBC 基础上仅扩展 MyBatis 动态 SQL 能力，可以使用以下方式：

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

使用：

```java
public interface UserRepository extends CrudRepository<User, Long> {
    @Query("user.queryByCode")  // 引用 sql/user.md 中的 queryByCode
    List<User> queryByCode(@Param("code") String code);
}
```

> **注意：** `@Query` 中 SpEL 表达式支持需要 Spring Data JDBC 3.0+ (JDK 17+)

---

## 📚 相关文档

- [动态 SQL 简化语法详解](easy-dynamic-sql.md)
- [方法名查询完整说明](method-name-query.md)
- [批量更新说明](bach-update.md)
- [多数据源配置](multi-datasource.md)
- [CrudRepository 扩展说明](curd-repository.md)

---

## 📄 License

[Apache License 2.0](LICENSE)
