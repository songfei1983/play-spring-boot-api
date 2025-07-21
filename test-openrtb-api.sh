#!/bin/bash

# OpenRTB API 测试脚本
# 用于测试 OpenRTB 数据持久化模块的基本功能

BASE_URL="http://localhost:8080/api/openrtb"

echo "🚀 OpenRTB API 测试开始..."
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
        response=$(curl -s -w "\n%{http_code}" "$BASE_URL$endpoint")
    else
        response=$(curl -s -w "\n%{http_code}" -X "$method" \
            -H "Content-Type: application/json" \
            -d "$data" \
            "$BASE_URL$endpoint")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -eq 200 ] || [ "$http_code" -eq 201 ]; then
        echo -e "${GREEN}✅ 成功 (HTTP $http_code)${NC}"
        echo "响应: $(echo "$body" | jq -r '.' 2>/dev/null || echo "$body")"
    else
        echo -e "${RED}❌ 失败 (HTTP $http_code)${NC}"
        echo "错误: $body"
    fi
    echo "--------------------------------------"
}

# 检查服务器是否运行
echo -e "${YELLOW}检查服务器状态...${NC}"
if ! curl -s "$BASE_URL" > /dev/null 2>&1; then
    echo -e "${RED}❌ 服务器未运行，请先启动应用程序${NC}"
    echo "启动命令: mvn spring-boot:run"
    exit 1
fi
echo -e "${GREEN}✅ 服务器正在运行${NC}"
echo "======================================"

# 1. 测试竞价请求 API
echo -e "${YELLOW}📋 测试竞价请求 API${NC}"

# 创建竞价请求
bid_request_data='{
  "requestId": "test_req_001",
  "timestamp": "2024-12-01T10:00:00",
  "sourceIp": "192.168.1.100",
  "exchangeId": "exchange_001",
  "status": "PENDING",
  "processingTime": 50,
  "bidRequest": {
    "id": "test_req_001",
    "imp": [{
      "id": "1",
      "banner": {
        "w": 300,
        "h": 250
      }
    }]
  }
}'

test_api "POST" "/bid-requests" "$bid_request_data" "创建竞价请求"

# 获取竞价请求
test_api "GET" "/bid-requests/test_req_001" "" "获取竞价请求"

# 获取超时请求
test_api "GET" "/bid-requests/timeout?maxProcessingTime=100" "" "获取超时请求"

# 2. 测试竞价响应 API
echo -e "${YELLOW}📊 测试竞价响应 API${NC}"

# 创建竞价响应
bid_response_data='{
  "responseId": "test_resp_001",
  "requestId": "test_req_001",
  "timestamp": "2024-12-01T10:00:01",
  "processingTime": 45,
  "bidResponse": {
    "id": "test_req_001",
    "seatbid": [{
      "bid": [{
        "id": "bid_001",
        "impid": "1",
        "price": 1.50
      }]
    }]
  },
  "bidResult": {
    "winningBidId": "bid_001",
    "winningCampaignId": "campaign_001",
    "winningPrice": 1.50
  }
}'

test_api "POST" "/bid-responses" "$bid_response_data" "创建竞价响应"

# 获取竞价响应
test_api "GET" "/bid-responses/request/test_req_001" "" "按请求ID获取竞价响应"

# 3. 测试广告活动 API
echo -e "${YELLOW}🎯 测试广告活动 API${NC}"

# 创建广告活动
campaign_data='{
  "campaignId": "campaign_001",
  "name": "测试广告活动",
  "advertiserId": "advertiser_001",
  "status": "ACTIVE",
  "startDate": "2024-12-01T00:00:00",
  "endDate": "2024-12-31T23:59:59",
  "budget": {
    "totalBudget": 100000,
    "dailyBudget": 5000,
    "spentBudget": 1000
  },
  "bidding": {
    "bidStrategy": "CPC",
    "maxBid": 2.00,
    "targetCPA": 10.00
  },
  "targeting": {
    "geo": {
      "countries": ["US", "CA"],
      "regions": ["California", "New York"]
    },
    "device": {
      "deviceTypes": ["mobile", "desktop"],
      "os": ["iOS", "Android"]
    },
    "audience": {
      "ageRange": {
        "min": 18,
        "max": 65
      },
      "interests": ["technology", "sports"]
    }
  }
}'

test_api "POST" "/campaigns" "$campaign_data" "创建广告活动"

# 获取广告活动
test_api "GET" "/campaigns/campaign_001" "" "获取广告活动"

# 获取活跃活动
test_api "GET" "/campaigns/active" "" "获取活跃广告活动"

# 4. 测试用户画像 API
echo -e "${YELLOW}👤 测试用户画像 API${NC}"

# 创建用户画像
user_profile_data='{
  "userId": "user_001",
  "demographics": {
    "age": 28,
    "gender": "M",
    "location": {
      "country": "US",
      "region": "California",
      "city": "San Francisco"
    }
  },
  "interests": ["technology", "sports", "travel"],
  "behavior": {
    "lastActiveTime": "2024-12-01T09:30:00",
    "sessionCount": 150,
    "avgSessionDuration": 1800
  },
  "purchaseHistory": {
    "totalPurchases": 25,
    "totalAmount": 125000,
    "avgOrderValue": 5000,
    "lastPurchaseDate": "2024-11-28T14:20:00"
  },
  "deviceInfo": {
    "primaryDevice": "mobile",
    "os": "iOS",
    "browser": "Safari"
  }
}'

test_api "POST" "/user-profiles" "$user_profile_data" "创建用户画像"

# 获取用户画像
test_api "GET" "/user-profiles/user_001" "" "获取用户画像"

# 获取高价值用户
test_api "GET" "/user-profiles/high-value?minPurchaseAmount=100000" "" "获取高价值用户"

# 5. 测试广告位库存 API
echo -e "${YELLOW}📦 测试广告位库存 API${NC}"

# 创建广告位库存
inventory_data='{
  "slotId": "slot_001",
  "publisherId": "publisher_001",
  "siteId": "site_001",
  "status": "ACTIVE",
  "specs": {
    "adType": "banner",
    "dimensions": {
      "width": 300,
      "height": 250
    },
    "formats": ["image", "html"]
  },
  "pricing": {
    "floorPrice": 0.50,
    "currency": "USD"
  },
  "quality": {
    "viewabilityRate": 0.85,
    "clickThroughRate": 0.02,
    "fraudScore": 0.05
  }
}'

test_api "POST" "/inventory" "$inventory_data" "创建广告位库存"

# 获取广告位库存
test_api "GET" "/inventory/slot_001" "" "获取广告位库存"

# 获取活跃广告位
test_api "GET" "/inventory/active" "" "获取活跃广告位"

# 6. 测试统计数据 API
echo -e "${YELLOW}📈 测试统计数据 API${NC}"

# 创建统计数据
statistics_data='{
  "date": "2024-12-01",
  "hour": 10,
  "campaignId": "campaign_001",
  "publisherId": "publisher_001",
  "placementId": "slot_001",
  "bidStats": {
    "totalBids": 1000,
    "wonBids": 150,
    "winRate": 0.15
  },
  "revenueStats": {
    "totalRevenue": 75000,
    "avgCpm": 5.00,
    "avgCpc": 1.50
  },
  "performanceStats": {
    "impressions": 150,
    "clicks": 3,
    "conversions": 1,
    "ctr": 0.02,
    "cvr": 0.33
  }
}'

test_api "POST" "/statistics" "$statistics_data" "创建统计数据"

# 获取统计数据
test_api "GET" "/statistics/date/2024-12-01" "" "按日期获取统计数据"

# 7. 测试业务逻辑 API
echo -e "${YELLOW}🔧 测试业务逻辑 API${NC}"

# 竞价匹配
test_api "GET" "/matching/campaigns?userId=user_001&slotId=slot_001&country=US&deviceType=mobile" "" "竞价匹配"

# 更新统计
test_api "POST" "/statistics/update?campaignId=campaign_001&publisherId=publisher_001&slotId=slot_001&bidWon=true&bidPrice=1500&country=US&deviceType=mobile" "" "更新竞价统计"

# 8. 测试缓存管理 API
echo -e "${YELLOW}🗄️ 测试缓存管理 API${NC}"

# 预热缓存
test_api "POST" "/cache/warmup" "" "预热缓存"

# 清除缓存
test_api "POST" "/cache/clear" "" "清除缓存"

# 9. 测试分析统计 API
echo -e "${YELLOW}📊 测试分析统计 API${NC}"

# 获取总请求数
test_api "GET" "/analytics/total-requests/2024-12-01" "" "获取总竞价请求数"

# 获取总收入
test_api "GET" "/analytics/total-revenue?startDate=2024-12-01&endDate=2024-12-01" "" "获取总收入"

# 获取活动展示数
test_api "GET" "/analytics/campaign-impressions/campaign_001" "" "获取活动总展示数"

# 获取发布商点击数
test_api "GET" "/analytics/publisher-clicks/publisher_001" "" "获取发布商总点击数"

echo "======================================"
echo -e "${GREEN}🎉 OpenRTB API 测试完成！${NC}"
echo "如果看到错误，请检查:"
echo "1. 应用程序是否正在运行"
echo "2. MongoDB 和 Redis 是否已启动"
echo "3. 数据库连接配置是否正确"
echo "4. 端口 8080 是否可用"