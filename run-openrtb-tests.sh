#!/bin/bash

# OpenRTB 测试运行脚本
# 用于运行所有 OpenRTB 相关的集成测试

echo "🚀 开始运行 OpenRTB 集成测试..."
echo "======================================"

# 运行所有 OpenRTB 测试
echo "📋 运行所有 OpenRTB 测试..."
./mvnw test -Dtest="*OpenRTB*"

if [ $? -eq 0 ]; then
    echo "✅ 所有 OpenRTB 测试通过！"
else
    echo "❌ 测试失败，请检查日志"
    exit 1
fi

echo "======================================"
echo "🎉 OpenRTB 测试完成！"

# 可选：运行特定测试
echo ""
echo "💡 提示：你也可以运行特定的测试："
echo "   ./mvnw test -Dtest=OpenRTBIntegrationTest    # 集成测试"
echo "   ./mvnw test -Dtest=OpenRTBEndToEndTest      # 端到端测试"