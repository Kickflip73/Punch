# 创数据实验室考勤打卡系统

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.1.9-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-Latest-red.svg)](https://redis.io/)
[![JWT](https://img.shields.io/badge/JWT-0.9.0-orange.svg)](https://jwt.io/)

## 项目简介

创数据实验室考勤打卡系统是一个基于Spring Boot开发的现代化实验室考勤管理系统。本系统旨在提供便捷、高效的实验室成员考勤管理，支持多角色权限控制、实时打卡记录、考勤统计等功能。

## 技术栈

- **后端框架**: Spring Boot 2.1.9
- **安全框架**: Spring Security + JWT
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **ORM**: MyBatis
- **API文档**: Swagger 3.0
- **对象存储**: 阿里云OSS
- **其他工具**: 
  - Lombok
  - FastJSON
  - Druid连接池
  - logback日志

## 主要功能

### 1. 用户管理
- 多角色支持（管理员、学生）
- JWT令牌认证
- 角色权限控制

### 2. 考勤管理
- 实时打卡记录
- 考勤时间设置
- 考勤统计报表
- 异常考勤处理

### 3. 公告管理
- 公告发布
- 公告更新
- 公告查看

### 4. 数据统计
- 个人考勤统计
- 团队考勤报表
- 数据可视化展示

## 快速开始

### 环境要求
- JDK 1.8+
- Maven 3.0+
- MySQL 8.0+
- Redis

### 配置说明
1. 克隆项目
```bash
git clone https://github.com/your-username/punch.git
```

2. 配置数据库
- 创建数据库
- 执行 `src/main/resources/punch_2023.5.25.sql` 初始化数据库

3. 配置application.yml
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
```

4. 运行项目
```bash
mvn spring-boot:run
```

## 项目结构
```
src/main/java/com/yrmjhtdjxh/punch/
├── config/         # 配置类
├── web/           # 控制器
├── service/       # 服务层
├── domain/        # 实体类
├── mapper/        # MyBatis映射器
├── security/      # 安全相关
├── redis/         # Redis缓存
├── util/          # 工具类
└── aspect/        # AOP切面
```

## 开发团队

- 创数据实验室研发团队

## 注意事项

1. 请勿提交敏感配置信息
2. 遵循代码规范和提交规范
3. 重要更新请及时同步文档

## 更新日志

### v1.0.0 (2023.5.25)
- 项目初始化
- 基础功能实现
- 数据库结构优化

## 许可证

[MIT License](LICENSE)

## 联系我们

- 项目负责人：刘煜燃[3065242502]
- 技术支持：[3065242502@qq.com]

---
© 2023 创数据实验室. All Rights Reserved.
