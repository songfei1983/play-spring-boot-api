#!/bin/bash

# OpenRTB 广告竞价系统测试脚本

BASE_URL="http://localhost:8080/api/v1/bid"
SAMPLE_REQUEST="src/main/resources/sample-bid-request.json"

echo "=== OpenRTB 广告竞价系统测试 ==="
echo

# 检查服务器健康状态
echo "1. 检查服务器健康状态..."
curl -s "$BASE_URL/health" | jq .
echo
echo

# 获取服务器状态
echo "2. 获取服务器状态..."
curl -s "$BASE_URL/status" | jq .
echo
echo

# 发送竞价请求
echo "3. 发送竞价请求..."
if [ -f "$SAMPLE_REQUEST" ]; then
    echo "使用示例请求文件: $SAMPLE_REQUEST"
    RESPONSE=$(curl -s -X POST "$BASE_URL/request" \
        -H "Content-Type: application/json" \
        -d @"$SAMPLE_REQUEST")
    
    echo "竞价响应:"
    echo "$RESPONSE" | jq .
    
    # 提取竞价ID用于后续测试
    BID_ID=$(echo "$RESPONSE" | jq -r '.seatbid[0].bid[0].id // empty')
    
    if [ -n "$BID_ID" ] && [ "$BID_ID" != "null" ]; then
        echo
        echo "4. 测试获胜通知..."
        curl -s -X POST "$BASE_URL/win/$BID_ID?winPrice=1.25" \
            -H "Content-Type: application/json"
        echo "获胜通知已发送"
        echo
        
        echo "5. 再次获取服务器状态（查看统计变化）..."
        curl -s "$BASE_URL/status" | jq .
        echo
    else
        echo
        echo "4. 测试损失通知（使用模拟竞价ID）..."
        curl -s -X POST "$BASE_URL/loss/test-bid-123?winPrice=1.50&lossReason=1" \
            -H "Content-Type: application/json"
        echo "损失通知已发送"
        echo
    fi
else
    echo "示例请求文件不存在: $SAMPLE_REQUEST"
    echo "创建简单的测试请求..."
    
    cat > /tmp/simple-bid-request.json << EOF
{
  "id": "test-request-$(date +%s)",
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
}
EOF
    
    echo "使用简单测试请求:"
    cat /tmp/simple-bid-request.json | jq .
    echo
    
    RESPONSE=$(curl -s -X POST "$BASE_URL/request" \
        -H "Content-Type: application/json" \
        -d @/tmp/simple-bid-request.json)
    
    echo "竞价响应:"
    echo "$RESPONSE" | jq .
    
    # 清理临时文件
    rm -f /tmp/simple-bid-request.json
fi

echo
echo "=== 测试完成 ==="
echo
echo "提示:"
echo "- 确保应用已启动并监听在 8080 端口"
echo "- 确保已安装 jq 工具用于 JSON 格式化"
echo "- 可以通过修改 BASE_URL 变量来测试不同环境"
echo "- 查看应用日志以获取详细的处理信息"