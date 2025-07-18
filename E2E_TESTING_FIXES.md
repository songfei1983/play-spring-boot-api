# E2E测试修复说明

## 问题描述

Pull Request的GitHub Actions中的e2e测试失败，主要原因是CI配置中的服务器启动配置不正确。

## 发现的问题

### 1. 前端服务器配置问题
- **问题**: CI中构建了前端静态文件但没有启动前端开发服务器
- **影响**: Playwright测试期望前端在`localhost:3000`运行，但实际上没有服务器提供前端文件
- **解决方案**: 在CI中启动前端开发服务器而不是构建静态文件

### 2. Playwright配置冲突
- **问题**: Playwright配置中的`webServer`设置会在CI环境中与手动启动的服务器冲突
- **影响**: 可能导致端口冲突或重复启动服务器
- **解决方案**: 在CI环境中禁用Playwright的`webServer`配置

### 3. 浏览器自动打开问题
- **问题**: 在CI环境中，前端开发服务器可能尝试自动打开浏览器
- **影响**: 在无头环境中会导致错误
- **解决方案**: 设置`BROWSER=none`环境变量

## 修复内容

### 1. 更新CI配置 (`.github/workflows/ci.yml`)

```yaml
# 移除前端构建步骤，改为启动开发服务器
- name: Start frontend dev server
  working-directory: ./frontend
  run: |
    BROWSER=none npm start &
    echo $! > frontend.pid
    # 等待前端服务启动
    timeout 120 bash -c 'until curl -f http://localhost:3000 2>/dev/null; do echo "Waiting for frontend..."; sleep 5; done'
    echo "Frontend server started successfully"

# 更新服务器停止步骤
- name: Stop servers
  if: always()
  run: |
    if [ -f backend.pid ]; then
      kill $(cat backend.pid) || true
    fi
    if [ -f frontend/frontend.pid ]; then
      kill $(cat frontend/frontend.pid) || true
    fi
```

### 2. 更新Playwright配置 (`frontend/playwright.config.ts`)

```typescript
/* Run your local dev server before starting the tests */
...(process.env.CI ? {} : {
  webServer: {
    command: 'npm start',
    url: 'http://localhost:3000',
    reuseExistingServer: false,
  },
})
```

### 3. 创建验证脚本 (`scripts/verify-e2e-setup.sh`)

提供了一个验证脚本来检查e2e测试环境设置是否正确。

## 验证修复

### 本地验证

1. 启动后端服务器：
   ```bash
   ./mvnw clean package -DskipTests
   java -jar target/*.jar &
   ```

2. 启动前端开发服务器：
   ```bash
   cd frontend
   BROWSER=none npm start &
   ```

3. 运行e2e测试：
   ```bash
   npm run test:e2e
   ```

### CI环境验证

修复后的CI流程：
1. ✅ 安装依赖
2. ✅ 安装Playwright浏览器
3. ✅ 构建后端JAR
4. ✅ 启动后端服务器 (localhost:8080)
5. ✅ 启动前端开发服务器 (localhost:3000)
6. ✅ 运行Playwright测试
7. ✅ 上传测试报告
8. ✅ 清理服务器进程

## 技术细节

### CORS配置
后端已正确配置CORS，允许前端在`localhost:3000`访问后端API：

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
```

### 健康检查
后端提供健康检查端点`/actuator/health`，CI使用此端点确认服务器启动成功。

### 环境变量
- `CI=true`: 标识CI环境，用于Playwright配置
- `BROWSER=none`: 防止前端开发服务器尝试打开浏览器

## 预期结果

修复后，e2e测试应该能够：
1. ✅ 在CI环境中正确启动前后端服务器
2. ✅ 成功运行所有Playwright测试
3. ✅ 正确处理前后端API通信
4. ✅ 生成并上传测试报告
5. ✅ 清理所有启动的进程

## 故障排除

如果e2e测试仍然失败，请检查：

1. **服务器启动**: 确认后端和前端服务器都成功启动
2. **端口冲突**: 确认8080和3000端口没有被其他进程占用
3. **网络连接**: 确认前端能够访问后端API
4. **测试超时**: 检查是否需要增加测试超时时间
5. **依赖版本**: 确认所有依赖版本兼容

运行验证脚本进行诊断：
```bash
bash scripts/verify-e2e-setup.sh
```