# API 文档说明

## 📚 概述

本项目使用 **Springdoc OpenAPI 3** 自动生成 API 文档，提供完整的 RESTful API 接口说明和在线测试功能。

## 🚀 访问方式

### 在线 API 文档

启动应用后，可以通过以下地址访问 API 文档：

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **OpenAPI YAML**: http://localhost:8080/api-docs.yaml

### 快速启动

```bash
# 启动应用
./mvnw spring-boot:run

# 或者
mvn spring-boot:run
```

## 📋 API 模块

### 1. 用户管理 (`/users`)

基础用户 CRUD 操作：

- `GET /users` - 获取所有用户
- `GET /users/{id}` - 根据 ID 获取用户
- `POST /users` - 创建新用户
- `PUT /users/{id}` - 更新用户信息
- `DELETE /users/{id}` - 删除用户

### 2. 用户档案 (`/api/users/profiles`)

用户详细档案信息管理：

- `GET /api/users/profiles` - 获取所有用户档案
- `GET /api/users/profiles/{id}` - 根据档案 ID 获取用户档案
- `GET /api/users/profiles/user/{userId}` - 根据用户 ID 获取用户档案
- `GET /api/users/profiles/gender/{gender}` - 根据性别获取用户档案
- `GET /api/users/profiles/age?minAge={min}&maxAge={max}` - 根据年龄范围获取用户档案
- `GET /api/users/profiles/occupation/{occupation}` - 根据职业获取用户档案
- `GET /api/users/profiles/search?keyword={keyword}` - 根据地址关键词搜索用户档案
- `POST /api/users/profiles` - 创建用户档案
- `PUT /api/users/profiles/{id}` - 更新用户档案
- `PUT /api/users/profiles/user/{userId}` - 根据用户 ID 更新用户档案
- `DELETE /api/users/profiles/{id}` - 删除用户档案
- `DELETE /api/users/profiles/user/{userId}` - 根据用户 ID 删除用户档案

### 3. 活动跟踪 (`/api/users/activities`)

用户活动轨迹记录和查询：

- `GET /api/users/activities` - 获取所有活动轨迹
- `GET /api/users/activities/{id}` - 根据 ID 获取活动轨迹
- `GET /api/users/activities/user/{userId}` - 根据用户 ID 获取活动轨迹
- `GET /api/users/activities/type/{activityType}` - 根据活动类型获取轨迹
- `GET /api/users/activities/device/{deviceType}` - 根据设备类型获取轨迹
- `GET /api/users/activities/time-range?startTime={start}&endTime={end}` - 根据时间范围获取轨迹
- `GET /api/users/activities/search?keyword={keyword}` - 根据位置关键词搜索轨迹
- `GET /api/users/activities/session/{sessionId}` - 根据会话 ID 获取轨迹
- `GET /api/users/activities/page?pageUrl={url}` - 根据页面 URL 获取轨迹
- `GET /api/users/activities/user/{userId}/recent?limit={limit}` - 获取用户最近的活动轨迹
- `POST /api/users/activities` - 创建活动轨迹
- `PUT /api/users/activities/{id}` - 更新活动轨迹
- `DELETE /api/users/activities/{id}` - 删除活动轨迹

### 4. 购买历史 (`/api/users/purchases`)

用户购买记录管理：

- `GET /api/users/purchases` - 获取所有购买记录
- `GET /api/users/purchases/{id}` - 根据 ID 获取购买记录
- `GET /api/users/purchases/user/{userId}` - 根据用户 ID 获取购买记录
- `GET /api/users/purchases/order/{orderNumber}` - 根据订单号获取购买记录
- `GET /api/users/purchases/product/{productId}` - 根据商品 ID 获取购买记录
- `GET /api/users/purchases/category/{category}` - 根据商品分类获取购买记录
- `GET /api/users/purchases/brand/{brand}` - 根据品牌获取购买记录
- `GET /api/users/purchases/payment-status/{paymentStatus}` - 根据支付状态获取购买记录
- `GET /api/users/purchases/order-status/{orderStatus}` - 根据订单状态获取购买记录
- `GET /api/users/purchases/time-range?startTime={start}&endTime={end}` - 根据时间范围获取购买记录
- `POST /api/users/purchases` - 创建购买记录
- `PUT /api/users/purchases/{id}` - 更新购买记录
- `DELETE /api/users/purchases/{id}` - 删除购买记录

## 🔧 配置说明

### OpenAPI 配置

项目在 `application.properties` 中配置了以下 OpenAPI 相关设置：

```properties
# OpenAPI/Swagger 配置
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.swagger-ui.filter=true
springdoc.show-actuator=false
```

### 自定义配置类

`OpenApiConfig.java` 提供了 API 文档的基本信息配置：

- API 标题和版本
- 联系信息
- 许可证信息
- 服务器环境配置
- API 标签分组

## 📝 使用示例

### 1. 创建用户

```bash
curl -X POST "http://localhost:8080/users" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "张三",
    "email": "zhangsan@example.com"
  }'
```

### 2. 获取用户列表

```bash
curl -X GET "http://localhost:8080/users"
```

### 3. 创建用户档案

```bash
curl -X POST "http://localhost:8080/api/users/profiles" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "age": 25,
    "gender": "男",
    "phoneNumber": "13800138000",
    "address": "北京市朝阳区",
    "occupation": "软件工程师",
    "bio": "热爱编程的技术人员"
  }'
```

## 🎯 特性

### 1. 自动生成文档
- 基于代码注解自动生成 API 文档
- 支持实时更新，代码变更后文档自动同步

### 2. 在线测试
- Swagger UI 提供在线 API 测试功能
- 支持参数输入和响应查看
- 支持不同 HTTP 方法测试

### 3. 详细的 API 说明
- 每个接口都有详细的描述和参数说明
- 包含请求/响应示例
- 错误码和状态码说明

### 4. 标签分组
- API 按功能模块分组显示
- 便于查找和使用相关接口

### 5. 数据模型文档
- 自动生成实体类的 Schema 文档
- 包含字段类型、描述和示例值

## 🔍 高级功能

### 1. 过滤和搜索
- Swagger UI 支持 API 过滤功能
- 可以按标签、操作类型等进行筛选

### 2. 多环境支持
- 配置了开发环境和生产环境的服务器地址
- 可以在不同环境间切换测试

### 3. 响应状态码
- 详细的 HTTP 状态码说明
- 包含成功和错误情况的处理

## 📖 最佳实践

1. **使用 Swagger 注解**：为所有 Controller 方法添加 `@Operation` 注解
2. **参数描述**：使用 `@Parameter` 为路径参数和请求参数添加描述
3. **响应文档**：使用 `@ApiResponses` 描述不同的响应情况
4. **数据模型**：在实体类中使用 `@Schema` 注解描述字段
5. **标签分组**：使用 `@Tag` 为 Controller 添加分组标签

## 🚨 注意事项

1. 确保应用已启动才能访问 API 文档
2. 在生产环境中考虑是否需要禁用 Swagger UI
3. 定期更新 API 文档注解，保持文档的准确性
4. 测试 API 时注意数据的有效性和安全性

## 📞 支持

如有问题或建议，请通过以下方式联系：

- GitHub Issues: [项目地址](https://github.com/songfei1983/play-spring-boot-api)
- Email: support@example.com