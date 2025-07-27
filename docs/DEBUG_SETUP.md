# 本地开发Debug模式设置

本文档说明如何在本地开发环境中启用UI和API server的debug日志级别。

## API Server (Spring Boot) Debug设置

### 配置文件修改

已修改 `src/main/resources/application.properties` 文件，设置以下debug日志级别：

```properties
# 日志配置
logging.level.fei.song.play_spring_boot_api=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework=DEBUG
logging.level.org.hibernate=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

### 启动API Server

```bash
# 在项目根目录下启动Spring Boot应用
./mvnw spring-boot:run

# 或者使用IDE运行主类
# fei.song.play_spring_boot_api.PlaySpringBootApiApplication
```

### Debug日志内容

启用debug模式后，API server将输出：
- 详细的HTTP请求和响应信息
- SQL查询语句和参数绑定
- Spring框架内部处理流程
- 应用程序自定义日志
- 数据库操作详情

## UI (React) Debug设置

### 环境变量配置

已创建 `frontend/.env.development` 文件：

```env
# React开发环境配置
# 启用详细日志输出
REACT_APP_LOG_LEVEL=debug

# 启用React开发者工具的详细模式
REACT_APP_DEBUG=true

# 显示更多的webpack构建信息
GENERATE_SOURCEMAP=true

# 启用热重载的详细日志
FAST_REFRESH=true

# 显示ESLint警告
ESLINT_NO_DEV_ERRORS=false

# 启用TypeScript类型检查
TSC_COMPILE_ON_ERROR=true

# 浏览器控制台显示更多信息
REACT_APP_VERBOSE=true
```

### 启动UI应用

```bash
# 进入frontend目录
cd frontend

# 安装依赖（如果还没有安装）
npm install

# 启动开发服务器（自动启用debug模式）
npm start

# 或者显式启动debug模式
npm run start:debug
```

### 日志工具使用

已创建 `frontend/src/utils/logger.ts` 日志工具，支持：

```typescript
import { logger } from '../utils/logger';

// 不同级别的日志
logger.debug('调试信息');
logger.info('一般信息');
logger.warn('警告信息');
logger.error('错误信息');

// API调用日志（已自动集成到axios拦截器中）
logger.apiCall('GET', '/api/users');
logger.apiResponse('GET', '/api/users', response);
logger.apiError('POST', '/api/users', error);
```

### Debug日志内容

启用debug模式后，UI将在浏览器控制台输出：
- 所有API请求和响应的详细信息
- 请求头和响应头
- 错误详情和堆栈跟踪
- 组件渲染和状态变化（如果添加相应日志）
- Webpack构建信息

## 验证Debug模式

### API Server验证

1. 启动API server后，查看控制台输出
2. 应该看到详细的Spring Boot启动日志
3. 访问任何API端点，应该看到详细的HTTP请求处理日志

### UI验证

1. 启动React应用后，打开浏览器开发者工具
2. 在Console标签页中应该看到 `[DEBUG]` 前缀的日志
3. 执行任何API操作，应该看到详细的请求和响应日志

## 生产环境注意事项

⚠️ **重要提醒**：

- Debug模式会产生大量日志输出，仅用于本地开发
- 部署到生产环境前，请确保将日志级别改回 `INFO` 或 `WARN`
- 生产环境不应包含 `.env.development` 文件中的debug配置

## 故障排除

### API Server日志不显示

1. 检查 `application.properties` 文件是否正确修改
2. 确认使用的是开发环境配置文件
3. 重启Spring Boot应用

### UI日志不显示

1. 检查 `.env.development` 文件是否存在
2. 确认环境变量 `REACT_APP_LOG_LEVEL=debug`
3. 重启React开发服务器
4. 清除浏览器缓存

### 日志过多影响性能

1. 可以调整日志级别为 `INFO` 减少输出
2. 或者只启用特定包的debug日志
3. 使用浏览器开发者工具的过滤功能