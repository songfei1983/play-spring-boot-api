# 🏗️ 项目架构图使用指南

本项目提供了多种方式来查看和生成架构图，使用免费的开源工具。

## 📁 架构图文件

### 1. `architecture-diagram.md`
包含完整的Mermaid语法架构图代码，包括：
- 整体系统架构
- OpenRTB竞价流程
- 数据流架构
- 部署架构
- 技术栈说明

### 2. `architecture-viewer.html`
可在浏览器中直接查看的交互式架构图查看器，包含：
- 美观的用户界面
- 多个架构视图切换
- 响应式设计
- 技术栈详细信息

## 🔧 查看架构图的方法

### 方法1: 浏览器直接查看 (推荐)
```bash
# 在项目根目录下，用浏览器打开HTML文件
open architecture-viewer.html
# 或者双击 architecture-viewer.html 文件
```

### 方法2: 在线Mermaid编辑器
1. 访问 [Mermaid Live Editor](https://mermaid.live/)
2. 复制 `architecture-diagram.md` 中的Mermaid代码
3. 粘贴到编辑器中查看
4. 可以导出为PNG、SVG等格式

### 方法3: VS Code插件
1. 安装 "Mermaid Preview" 插件
2. 打开 `architecture-diagram.md` 文件
3. 使用 `Ctrl+Shift+P` (Windows/Linux) 或 `Cmd+Shift+P` (Mac)
4. 搜索 "Mermaid: Preview" 并执行

### 方法4: GitHub查看
- GitHub原生支持Mermaid图表渲染
- 直接在GitHub仓库中查看 `architecture-diagram.md` 文件

### 方法5: 其他工具
- **Draw.io**: 支持Mermaid导入 - [app.diagrams.net](https://app.diagrams.net/)
- **Typora**: Markdown编辑器，支持Mermaid - [typora.io](https://typora.io/)
- **Obsidian**: 知识管理工具，支持Mermaid - [obsidian.md](https://obsidian.md/)

## 📊 架构图说明

### 1. 整体系统架构
展示了完整的分层架构：
- **API Gateway Layer**: Nginx + Spring Boot API
- **Application Layer**: Controllers (用户模块 + 广告模块)
- **Service Layer**: 业务服务层
- **Infrastructure Layer**: 数据访问层
- **Database Layer**: MongoDB + H2数据库

### 2. OpenRTB竞价流程
详细展示了实时竞价的完整流程：
- Ad Exchange发送竞价请求
- 广告位过滤和反欺诈检测
- 竞价算法计算和预算控制
- 返回竞价响应

### 3. 数据流架构
展示了数据在系统中的流动：
- 实时数据处理
- 批量数据处理
- 机器学习集成
- 缓存策略

### 4. 部署架构
展示了生产环境的部署方案：
- 负载均衡
- 应用集群
- 数据库集群
- 监控系统

## 🛠️ 技术栈

### 后端技术
- **Spring Boot 3.x**: 主要框架
- **MongoDB**: OpenRTB数据存储
- **H2**: 用户数据存储
- **Redis**: 缓存层
- **Maven**: 构建工具
- **Docker**: 容器化

### 前端技术
- **React 19.x**: 前端框架
- **Nginx**: Web服务器
- **Playwright**: E2E测试

### 开发工具
- **OpenAPI 3.0**: API文档
- **Prometheus + Grafana**: 监控
- **JUnit 5**: 单元测试

## 🎯 架构特点

### 1. 分层架构 (Layered Architecture)
- 清晰的职责分离
- 易于维护和扩展
- 符合DDD设计原则

### 2. 模块化设计
- **用户模块**: 用户管理、画像、活动跟踪
- **广告模块**: OpenRTB竞价、预算控制、反欺诈

### 3. 微服务就绪
- 模块边界清晰
- 独立的数据存储
- RESTful API设计

### 4. 高性能设计
- 异步处理机制
- 多级缓存策略
- 数据库优化
- 负载均衡支持

### 5. 可观测性
- 全面的日志记录
- 性能监控指标
- 健康检查端点
- 错误追踪机制

## 🚀 快速开始

1. **查看架构图**:
   ```bash
   # 在浏览器中打开交互式查看器
   open architecture-viewer.html
   ```

2. **编辑架构图**:
   - 修改 `architecture-diagram.md` 中的Mermaid代码
   - 在 [Mermaid Live Editor](https://mermaid.live/) 中预览
   - 更新 `architecture-viewer.html` 中的图表代码

3. **导出架构图**:
   - 使用Mermaid Live Editor导出PNG/SVG
   - 使用VS Code插件导出
   - 截图保存

## 📝 更新架构图

当项目架构发生变化时，请同时更新：
1. `architecture-diagram.md` - Mermaid源码
2. `architecture-viewer.html` - 交互式查看器
3. `ARCHITECTURE_GUIDE.md` - 本说明文档

## 🤝 贡献

欢迎提交架构改进建议：
1. Fork项目
2. 创建特性分支
3. 提交架构图更新
4. 发起Pull Request

## 📞 支持

如果在使用架构图时遇到问题：
1. 检查浏览器是否支持Mermaid
2. 确保网络连接正常（需要加载CDN资源）
3. 尝试刷新页面
4. 查看浏览器控制台错误信息

---

**注意**: 所有架构图工具都是免费的开源工具，无需付费即可使用。