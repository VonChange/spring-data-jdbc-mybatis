# README 文档优化计划

## 背景与目标

当前 README 文档存在结构混乱、特性介绍不全、缺少快速入门等问题。本次优化目标是让文档更加清晰、完整、易于上手。

## 优化范围

- `README.zh-CN.md` - 中文文档（主要优化）
- `README.md` - 英文文档（同步优化）

## 不做清单

- 不修改代码实现
- 不修改其他 markdown 文档（如 easy-dynamic-sql.md 等）

## 文档结构设计

### 1. 项目简介
- 一句话核心价值
- 徽章（Maven Central、Stars）

### 2. 核心特性
- 轻量级 ORM
- MyBatis 动态 SQL（不依赖 MyBatis）
- SQL 写在 Markdown
- 方法名查询
- findByExample 扩展
- 批量操作
- 多数据源

### 3. 快速开始
- Maven 依赖
- 实体类定义
- Repository 接口
- Markdown SQL
- 启动类配置
- 使用示例

### 4. 使用指南
- 4.1 Repository 模式
- 4.2 QueryRepository（纯查询）
- 4.3 CrudClient / JdbcClient
- 4.4 方法名查询
- 4.5 Markdown SQL 语法
- 4.6 动态 SQL 简化语法
- 4.7 findByExample
- 4.8 批量操作
- 4.9 分页查询

### 5. 注解参考

### 6. 多数据源配置

### 7. 与官方 Spring Data JDBC 扩展方式

## 任务清单

- [x] 优化 README.zh-CN.md
- [x] 同步优化 README.md

## 验收标准

- 文档结构清晰，层次分明
- 包含完整的快速入门示例
- 特性介绍全面
- 代码示例可直接运行

