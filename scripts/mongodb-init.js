// OpenRTB MongoDB 数据库初始化脚本
// 使用方法: mongo openrtb mongodb-init.js

// 切换到 openrtb 数据库
use openrtb;

print("开始初始化 OpenRTB 数据库...");

// ================================
// 1. 创建集合和索引
// ================================

// 1.1 竞价请求集合
print("创建 bid_requests 集合和索引...");
db.createCollection("bid_requests");

// 创建索引
db.bid_requests.createIndex({ "request_id": 1 }, { unique: true });
db.bid_requests.createIndex({ "timestamp": -1 });
db.bid_requests.createIndex({ "exchange_id": 1, "timestamp": -1 });
db.bid_requests.createIndex({ "bid_request.user.id": 1 });
db.bid_requests.createIndex({ "bid_request.device.geo.country": 1 });
db.bid_requests.createIndex({ "expires_at": 1 }, { expireAfterSeconds: 0 });
db.bid_requests.createIndex({ "bid_request.site.domain": 1, "timestamp": -1 });
db.bid_requests.createIndex({ "bid_request.device.devicetype": 1, "bid_request.device.os": 1 });

// 1.2 竞价响应集合
print("创建 bid_responses 集合和索引...");
db.createCollection("bid_responses");

db.bid_responses.createIndex({ "request_id": 1 }, { unique: true });
db.bid_responses.createIndex({ "timestamp": -1 });
db.bid_responses.createIndex({ "bid_response.seatbid.bid.cid": 1 });
db.bid_responses.createIndex({ "bid_result.winning_bid_id": 1 });
db.bid_responses.createIndex({ "expires_at": 1 }, { expireAfterSeconds: 0 });

// 1.3 广告活动集合
print("创建 campaigns 集合和索引...");
db.createCollection("campaigns");

db.campaigns.createIndex({ "campaign_id": 1 }, { unique: true });
db.campaigns.createIndex({ "advertiser_id": 1 });
db.campaigns.createIndex({ "status": 1 });
db.campaigns.createIndex({ "schedule.start_date": 1, "schedule.end_date": 1 });
db.campaigns.createIndex({ "targeting.geo.included_countries": 1 });
db.campaigns.createIndex({ "targeting.device.device_types": 1 });

// 1.4 用户画像集合
print("创建 user_profiles 集合和索引...");
db.createCollection("user_profiles");

db.user_profiles.createIndex({ "user_id": 1 }, { unique: true });
db.user_profiles.createIndex({ "demographics.age": 1, "demographics.gender": 1 });
db.user_profiles.createIndex({ "interests.category": 1, "interests.score": -1 });
db.user_profiles.createIndex({ "devices.device_id": 1 });
db.user_profiles.createIndex({ "locations.country": 1, "locations.city": 1 });
db.user_profiles.createIndex({ "expires_at": 1 }, { expireAfterSeconds: 0 });

// 1.5 广告位库存集合
print("创建 inventory 集合和索引...");
db.createCollection("inventory");

db.inventory.createIndex({ "placement_id": 1 }, { unique: true });
db.inventory.createIndex({ "publisher_id": 1 });
db.inventory.createIndex({ "site_id": 1 });
db.inventory.createIndex({ "placement_info.ad_formats": 1 });
db.inventory.createIndex({ "content.categories": 1 });
db.inventory.createIndex({ "pricing.floor_price": 1 });
db.inventory.createIndex({ "status": 1 });

// 1.6 竞价统计集合
print("创建 bid_statistics 集合和索引...");
db.createCollection("bid_statistics");

db.bid_statistics.createIndex({ "date": -1 });
db.bid_statistics.createIndex({ "dimensions.campaign_id": 1, "date": -1 });
db.bid_statistics.createIndex({ "dimensions.country": 1, "date": -1 });
db.bid_statistics.createIndex({ "dimensions.device_type": 1, "date": -1 });

// ================================
// 2. 插入示例数据
// ================================

print("插入示例数据...");

// 2.1 示例广告活动
db.campaigns.insertMany([
  {
    campaign_id: "campaign_001",
    advertiser_id: "adv_001",
    name: "春季电子产品促销",
    status: "active",
    budget: {
      total_budget: 50000.00,
      daily_budget: 1000.00,
      spent_total: 12500.00,
      spent_today: 250.00,
      currency: "USD"
    },
    targeting: {
      geo: {
        included_countries: ["US", "CA", "UK"],
        excluded_countries: [],
        included_regions: ["NY", "CA", "TX"],
        included_cities: ["New York", "Los Angeles", "Chicago"]
      },
      device: {
        device_types: [1, 4, 5],
        operating_systems: ["iOS", "Android"],
        browsers: ["Chrome", "Safari", "Firefox"]
      },
      audience: {
        age_range: { min: 25, max: 55 },
        genders: ["M", "F"],
        interests: ["technology", "electronics", "gadgets"]
      },
      time: {
        days_of_week: [1, 2, 3, 4, 5],
        hours_of_day: [9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20]
      }
    },
    bidding: {
      bid_strategy: "cpm",
      max_bid: 3.00,
      base_bid: 1.20,
      bid_adjustments: {
        mobile: 1.3,
        weekend: 0.7,
        premium_inventory: 1.8
      }
    },
    creatives: [{
      creative_id: "creative_001",
      format: "banner",
      width: 300,
      height: 250,
      html: "<a href='http://example.com/spring-sale'><img src='http://cdn.example.com/spring-banner.jpg' width='300' height='250' alt='Spring Sale'/></a>",
      click_url: "http://example.com/spring-sale",
      impression_trackers: ["http://tracker.example.com/imp?cid=campaign_001"],
      click_trackers: ["http://tracker.example.com/click?cid=campaign_001"]
    }],
    frequency_cap: {
      impressions_per_user_per_day: 5,
      impressions_per_user_per_hour: 2
    },
    schedule: {
      start_date: new Date("2024-03-01T00:00:00Z"),
      end_date: new Date("2024-05-31T23:59:59Z"),
      timezone: "America/New_York"
    },
    created_at: new Date(),
    updated_at: new Date(),
    created_by: "admin_001"
  },
  {
    campaign_id: "campaign_002",
    advertiser_id: "adv_002",
    name: "夏季旅游推广",
    status: "active",
    budget: {
      total_budget: 30000.00,
      daily_budget: 800.00,
      spent_total: 5600.00,
      spent_today: 120.00,
      currency: "USD"
    },
    targeting: {
      geo: {
        included_countries: ["US", "CA"],
        excluded_countries: [],
        included_regions: ["FL", "CA", "HI"],
        included_cities: ["Miami", "San Diego", "Honolulu"]
      },
      device: {
        device_types: [1, 2, 4, 5],
        operating_systems: ["iOS", "Android", "Windows"],
        browsers: ["Chrome", "Safari", "Edge"]
      },
      audience: {
        age_range: { min: 22, max: 65 },
        genders: ["M", "F"],
        interests: ["travel", "vacation", "adventure", "beach"]
      },
      time: {
        days_of_week: [1, 2, 3, 4, 5, 6, 7],
        hours_of_day: [6, 7, 8, 9, 18, 19, 20, 21, 22]
      }
    },
    bidding: {
      bid_strategy: "cpm",
      max_bid: 2.50,
      base_bid: 0.80,
      bid_adjustments: {
        mobile: 1.1,
        weekend: 1.4,
        premium_inventory: 1.6
      }
    },
    creatives: [{
      creative_id: "creative_002",
      format: "banner",
      width: 728,
      height: 90,
      html: "<a href='http://travel.example.com/summer'><img src='http://cdn.travel.example.com/summer-banner.jpg' width='728' height='90' alt='Summer Travel Deals'/></a>",
      click_url: "http://travel.example.com/summer",
      impression_trackers: ["http://tracker.travel.example.com/imp?cid=campaign_002"],
      click_trackers: ["http://tracker.travel.example.com/click?cid=campaign_002"]
    }],
    frequency_cap: {
      impressions_per_user_per_day: 3,
      impressions_per_user_per_hour: 1
    },
    schedule: {
      start_date: new Date("2024-06-01T00:00:00Z"),
      end_date: new Date("2024-08-31T23:59:59Z"),
      timezone: "America/Los_Angeles"
    },
    created_at: new Date(),
    updated_at: new Date(),
    created_by: "admin_002"
  }
]);

// 2.2 示例用户画像
db.user_profiles.insertMany([
  {
    user_id: "user_001",
    demographics: {
      age: 32,
      gender: "M",
      income_level: "high",
      education: "graduate",
      marital_status: "married"
    },
    interests: [
      { category: "technology", score: 0.9, last_updated: new Date() },
      { category: "sports", score: 0.7, last_updated: new Date() },
      { category: "finance", score: 0.6, last_updated: new Date() }
    ],
    behavior: {
      page_views: [
        { domain: "techcrunch.com", category: "technology", timestamp: new Date() },
        { domain: "espn.com", category: "sports", timestamp: new Date() }
      ],
      purchase_history: [
        { category: "electronics", amount: 1299.99, timestamp: new Date() },
        { category: "books", amount: 45.99, timestamp: new Date() }
      ],
      app_usage: [
        { app_id: "com.apple.news", usage_time: 1800, timestamp: new Date() },
        { app_id: "com.espn.score", usage_time: 900, timestamp: new Date() }
      ]
    },
    devices: [{
      device_id: "device_001",
      device_type: "mobile",
      os: "iOS",
      last_seen: new Date()
    }],
    locations: [{
      country: "US",
      region: "NY",
      city: "New York",
      lat: 40.7128,
      lon: -74.0060,
      timestamp: new Date()
    }],
    frequency_data: {
      daily_impressions: {
        "2024-01-15": 3,
        "2024-01-14": 5,
        "2024-01-13": 2
      },
      campaign_impressions: {
        "campaign_001": 2,
        "campaign_002": 1
      }
    },
    created_at: new Date(),
    updated_at: new Date(),
    expires_at: new Date(Date.now() + 90 * 24 * 60 * 60 * 1000) // 90天后过期
  },
  {
    user_id: "user_002",
    demographics: {
      age: 28,
      gender: "F",
      income_level: "medium",
      education: "college",
      marital_status: "single"
    },
    interests: [
      { category: "travel", score: 0.8, last_updated: new Date() },
      { category: "fashion", score: 0.9, last_updated: new Date() },
      { category: "food", score: 0.7, last_updated: new Date() }
    ],
    behavior: {
      page_views: [
        { domain: "booking.com", category: "travel", timestamp: new Date() },
        { domain: "vogue.com", category: "fashion", timestamp: new Date() }
      ],
      purchase_history: [
        { category: "clothing", amount: 299.99, timestamp: new Date() },
        { category: "travel", amount: 899.99, timestamp: new Date() }
      ],
      app_usage: [
        { app_id: "com.booking", usage_time: 2400, timestamp: new Date() },
        { app_id: "com.instagram", usage_time: 3600, timestamp: new Date() }
      ]
    },
    devices: [{
      device_id: "device_002",
      device_type: "mobile",
      os: "Android",
      last_seen: new Date()
    }],
    locations: [{
      country: "US",
      region: "CA",
      city: "Los Angeles",
      lat: 34.0522,
      lon: -118.2437,
      timestamp: new Date()
    }],
    frequency_data: {
      daily_impressions: {
        "2024-01-15": 2,
        "2024-01-14": 4,
        "2024-01-13": 1
      },
      campaign_impressions: {
        "campaign_002": 3
      }
    },
    created_at: new Date(),
    updated_at: new Date(),
    expires_at: new Date(Date.now() + 90 * 24 * 60 * 60 * 1000)
  }
]);

// 2.3 示例广告位库存
db.inventory.insertMany([
  {
    placement_id: "placement_001",
    publisher_id: "pub_001",
    site_id: "site_001",
    placement_info: {
      name: "首页顶部横幅",
      description: "新闻网站首页顶部横幅广告位",
      ad_formats: ["banner"],
      sizes: [
        { width: 728, height: 90 },
        { width: 970, height: 250 }
      ],
      position: "above_fold",
      visibility: "high"
    },
    pricing: {
      floor_price: 1.20,
      currency: "USD",
      pricing_model: "cpm",
      private_deals: [{
        deal_id: "deal_001",
        buyer_id: "buyer_001",
        price: 2.50
      }]
    },
    traffic: {
      daily_impressions: 500000,
      peak_hours: [8, 9, 10, 12, 13, 17, 18, 19, 20],
      audience_demographics: {
        age_distribution: { "18-24": 0.15, "25-34": 0.35, "35-44": 0.25, "45-54": 0.15, "55+": 0.10 },
        gender_distribution: { "M": 0.55, "F": 0.45 },
        geo_distribution: { "US": 0.60, "CA": 0.15, "UK": 0.10, "AU": 0.08, "Other": 0.07 }
      }
    },
    content: {
      categories: ["IAB12", "IAB1"], // News, Arts & Entertainment
      keywords: ["news", "politics", "world", "business"],
      language: "en",
      content_rating: "general"
    },
    technical: {
      supported_apis: ["MRAID", "VPAID"],
      ssl_required: true,
      javascript_enabled: true,
      cookie_support: true
    },
    status: "active",
    created_at: new Date(),
    updated_at: new Date()
  },
  {
    placement_id: "placement_002",
    publisher_id: "pub_002",
    site_id: "site_002",
    placement_info: {
      name: "侧边栏中矩形",
      description: "科技博客侧边栏中矩形广告位",
      ad_formats: ["banner"],
      sizes: [
        { width: 300, height: 250 },
        { width: 336, height: 280 }
      ],
      position: "above_fold",
      visibility: "medium"
    },
    pricing: {
      floor_price: 0.80,
      currency: "USD",
      pricing_model: "cpm",
      private_deals: []
    },
    traffic: {
      daily_impressions: 200000,
      peak_hours: [9, 10, 11, 14, 15, 16, 20, 21],
      audience_demographics: {
        age_distribution: { "18-24": 0.20, "25-34": 0.40, "35-44": 0.25, "45-54": 0.10, "55+": 0.05 },
        gender_distribution: { "M": 0.70, "F": 0.30 },
        geo_distribution: { "US": 0.50, "CA": 0.12, "UK": 0.15, "DE": 0.08, "Other": 0.15 }
      }
    },
    content: {
      categories: ["IAB19"], // Technology & Computing
      keywords: ["technology", "programming", "software", "gadgets"],
      language: "en",
      content_rating: "general"
    },
    technical: {
      supported_apis: ["MRAID"],
      ssl_required: true,
      javascript_enabled: true,
      cookie_support: true
    },
    status: "active",
    created_at: new Date(),
    updated_at: new Date()
  }
]);

// 2.4 示例竞价统计数据
var now = new Date();
var oneHourAgo = new Date(now.getTime() - 60 * 60 * 1000);
var twoHoursAgo = new Date(now.getTime() - 2 * 60 * 60 * 1000);

db.bid_statistics.insertMany([
  {
    date: twoHoursAgo,
    dimensions: {
      campaign_id: "campaign_001",
      placement_id: "placement_001",
      country: "US",
      device_type: "mobile",
      os: "iOS"
    },
    metrics: {
      bid_requests: 2500,
      bid_responses: 2000,
      impressions: 380,
      clicks: 28,
      conversions: 4,
      spend: 456.00,
      revenue: 560.00,
      avg_bid: 1.20,
      max_bid: 2.80,
      win_rate: 0.19,
      cpm: 1.20,
      cpc: 16.29,
      cpa: 114.00,
      ctr: 0.074,
      cvr: 0.143
    },
    created_at: new Date()
  },
  {
    date: oneHourAgo,
    dimensions: {
      campaign_id: "campaign_002",
      placement_id: "placement_002",
      country: "US",
      device_type: "desktop",
      os: "Windows"
    },
    metrics: {
      bid_requests: 1800,
      bid_responses: 1500,
      impressions: 225,
      clicks: 18,
      conversions: 2,
      spend: 180.00,
      revenue: 216.00,
      avg_bid: 0.80,
      max_bid: 1.60,
      win_rate: 0.15,
      cpm: 0.80,
      cpc: 10.00,
      cpa: 90.00,
      ctr: 0.080,
      cvr: 0.111
    },
    created_at: new Date()
  }
]);

print("数据库初始化完成!");
print("\n集合统计:");
print("- campaigns: " + db.campaigns.count() + " 条记录");
print("- user_profiles: " + db.user_profiles.count() + " 条记录");
print("- inventory: " + db.inventory.count() + " 条记录");
print("- bid_statistics: " + db.bid_statistics.count() + " 条记录");
print("\n索引统计:");
print("- bid_requests 索引数: " + db.bid_requests.getIndexes().length);
print("- bid_responses 索引数: " + db.bid_responses.getIndexes().length);
print("- campaigns 索引数: " + db.campaigns.getIndexes().length);
print("- user_profiles 索引数: " + db.user_profiles.getIndexes().length);
print("- inventory 索引数: " + db.inventory.getIndexes().length);
print("- bid_statistics 索引数: " + db.bid_statistics.getIndexes().length);