#!/bin/bash

# E2E测试设置验证脚本
# 用于验证CI环境中的e2e测试配置是否正确

set -e

echo "🔍 验证E2E测试设置..."

# 检查前端依赖
echo "📦 检查前端依赖..."
cd frontend
if [ ! -d "node_modules" ]; then
    echo "❌ 前端依赖未安装，正在安装..."
    npm ci
else
    echo "✅ 前端依赖已安装"
fi

# 检查Playwright浏览器
echo "🌐 检查Playwright浏览器..."
if ! npx playwright --version > /dev/null 2>&1; then
    echo "❌ Playwright未安装"
    exit 1
fi

echo "🔧 安装Playwright浏览器..."
npx playwright install --with-deps

# 检查配置文件
echo "⚙️ 检查配置文件..."
if [ ! -f "playwright.config.ts" ]; then
    echo "❌ Playwright配置文件不存在"
    exit 1
fi

if [ ! -d "e2e" ]; then
    echo "❌ e2e测试目录不存在"
    exit 1
fi

echo "✅ E2E测试设置验证完成"
echo "💡 在CI环境中，确保："
echo "   1. 后端服务器在localhost:8080运行"
echo "   2. 前端开发服务器在localhost:3000运行"
echo "   3. 设置CI=true环境变量"
echo "   4. 设置BROWSER=none防止打开浏览器"

cd ..
echo "🎯 可以运行以下命令进行完整测试："
echo "   1. 启动后端: java -jar target/*.jar &"
echo "   2. 启动前端: cd frontend && BROWSER=none npm start &"
echo "   3. 运行测试: cd frontend && npm run test:e2e"