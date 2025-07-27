#!/bin/bash

# OpenRTB API 综合测试脚本
# 合并了所有有效的API测试，删除了无效的端点测试

BASE_URL="http://localhost:8080"
BID_API_URL="$BASE_URL/api/v1/bid"
SAMPLE_REQUEST="src/main/resources/sample-bid-request.json"

echo "🚀 OpenRTB API 综合测试开始..."
echo "基础URL: $BASE_URL"
echo "======================================"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 测试函数
test_api() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo -e "${BLUE}测试: $description${NC}"
    echo "请求: $method $endpoint"
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "\n%{http_code}" "$endpoint")
    else
        if [ -n "$data" ]; then
            if [[ "$data" == @* ]]; then
                # 文件数据
                response=$(curl -s -w "\n%{http_code}" -X "$method" \
                    -H "Content-Type: application/json" \
                    -d "$data" \
                    "$endpoint")
            else
                # JSON字符串数据
                response=$(curl -s -w "\n%{http_code}" -X "$method" \
                    -H "Content-Type: application/json" \
                    -d "$data" \
                    "$endpoint")
            fi
        else
            response=$(curl -s -w "\n%{http_code}" -X "$method" \
                -H "Content-Type: application/json" \
                "$endpoint")
        fi
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" -eq 200 ] || [ "$http_code" -eq 201 ] || [ "$http_code" -eq 204 ]; then
        echo -e "${GREEN}✅ 成功 (HTTP $http_code)${NC}"
        if [ -n "$body" ] && [ "$body" != "" ]; then
            echo "响应: $(echo "$body" | jq -r '.' 2>/dev/null || echo "$body")"
        fi
    else
        echo -e "${RED}❌ 失败 (HTTP $http_code)${NC}"
        echo "错误: $body"
    fi
    echo "--------------------------------------"
}

# 检查服务器是否运行
echo -e "${YELLOW}检查服务器状态...${NC}"
if ! curl -s "$BID_API_URL/health" > /dev/null 2>&1; then
    echo -e "${RED}❌ 服务器未运行，请先启动应用程序${NC}"
    echo "启动命令: docker-compose up 或 mvn spring-boot:run"
    exit 1
fi
echo -e "${GREEN}✅ 服务器正在运行${NC}"
echo "======================================"

# 1. 测试健康检查和状态 API
echo -e "${YELLOW}🏥 测试健康检查和状态 API${NC}"

test_api "GET" "$BID_API_URL/health" "" "健康检查"
test_api "GET" "$BID_API_URL/status" "" "获取服务器状态"

# 2. 测试竞价请求 API
echo -e "${YELLOW}📋 测试竞价请求 API${NC}"

# 使用示例请求文件测试
if [ -f "$SAMPLE_REQUEST" ]; then
    echo "使用示例请求文件: $SAMPLE_REQUEST"
    
    # 读取文件内容并发送请求
    RESPONSE=$(curl -s -X POST "$BID_API_URL/request" \
        -H "Content-Type: application/json" \
        -d @"$SAMPLE_REQUEST")
    
    echo "竞价响应:"
    echo "$RESPONSE" | jq . 2>/dev/null || echo "$RESPONSE"
    
    # 提取竞价ID用于后续测试
    BID_ID=$(echo "$RESPONSE" | jq -r '.seatbid[0].bid[0].id // empty' 2>/dev/null)
    
    if [ -n "$BID_ID" ] && [ "$BID_ID" != "null" ] && [ "$BID_ID" != "" ]; then
        echo
        echo -e "${YELLOW}🏆 测试获胜通知...${NC}"
        test_api "POST" "$BID_API_URL/win/$BID_ID?winPrice=1.25" "" "发送获胜通知"
        
        echo -e "${YELLOW}📊 再次获取服务器状态（查看统计变化）...${NC}"
        test_api "GET" "$BID_API_URL/status" "" "获取更新后的服务器状态"
    else
        echo
        echo -e "${YELLOW}💔 测试损失通知（使用模拟竞价ID）...${NC}"
        test_api "POST" "$BID_API_URL/loss/test-bid-123?winPrice=1.50&lossReason=1" "" "发送损失通知"
    fi
else
    echo "示例请求文件不存在: $SAMPLE_REQUEST"
    echo "创建简单的测试请求..."
    
    # 创建简单的测试请求
    SIMPLE_REQUEST='{
  "id": "test-request-'$(date +%s)'",
  "imp": [
    {
      "id": "imp-1",
      "banner": {
        "w": 300,
        "h": 250
      },
      "bidfloor": 0.5
    }
  ],
  "site": {
    "id": "test-site",
    "domain": "test.com"
  },
  "device": {
    "ua": "Mozilla/5.0 (Test Browser)",
    "ip": "192.168.1.1"
  }
}'
    
    echo "使用简单测试请求:"
    echo "$SIMPLE_REQUEST" | jq .
    echo
    
    RESPONSE=$(curl -s -X POST "$BID_API_URL/request" \
        -H "Content-Type: application/json" \
        -d "$SIMPLE_REQUEST")
    
    echo "竞价响应:"
    echo "$RESPONSE" | jq . 2>/dev/null || echo "$RESPONSE"
fi

# 3. 可选：运行Maven测试套件
echo -e "${YELLOW}🧪 可选：运行Maven测试套件${NC}"
echo "注意：项目中没有专门的OpenRTB测试类，可以运行所有测试来验证整体功能"
echo "如需运行所有测试，请手动执行: ./mvnw test"
echo "如需运行特定控制器测试，请执行: ./mvnw test -Dtest=*ControllerTest"
echo -e "${YELLOW}⚠️  跳过自动测试执行以避免长时间等待${NC}"

echo "======================================"
echo -e "${GREEN}🎉 OpenRTB API 综合测试完成！${NC}"
echo
echo "📝 测试总结:"
echo "✅ 有效的API端点:"
echo "   - GET  /api/v1/bid/health     # 健康检查"
echo "   - GET  /api/v1/bid/status     # 服务器状态"
echo "   - POST /api/v1/bid/request    # 竞价请求"
echo "   - POST /api/v1/bid/win/{bidId} # 获胜通知"
echo "   - POST /api/v1/bid/loss/{bidId} # 损失通知"
echo
echo "❌ 已删除的无效端点:"
echo "   - /api/openrtb/* (这些端点在代码中不存在)"
echo
echo "💡 提示:"
echo "   - 确保应用已启动并监听在 8080 端口"
echo "   - 确保已安装 jq 工具用于 JSON 格式化"
echo "   - 可以通过修改 BASE_URL 变量来测试不同环境"
echo "   - 查看应用日志以获取详细的处理信息"
echo
echo "🚀 如果需要单独运行特定测试:"
echo "   ./mvnw test                                 # 运行所有测试"
echo "   ./mvnw test -Dtest=*ControllerTest          # 运行所有控制器测试"
echo "   ./mvnw test -Dtest=UserControllerTest       # 运行用户控制器测试"