import { User, UserProfile, ActivityTrack, PurchaseHistory, Campaign } from './api';

// Mock数据
const mockUsers: User[] = [
  { id: 1, name: '张三', email: 'zhangsan@example.com' },
  { id: 2, name: '李四', email: 'lisi@example.com' },
  { id: 3, name: '王五', email: 'wangwu@example.com' },
];

const mockProfiles: UserProfile[] = [
  {
    id: 1,
    userId: 1,
    age: 25,
    gender: '男',
    birthday: '1998-01-01',
    phoneNumber: '13800138001',
    address: '北京市朝阳区',
    occupation: '软件工程师',
    bio: '热爱编程的开发者',
    avatarUrl: 'https://example.com/avatar1.jpg',
    createdAt: '2023-01-01T00:00:00Z',
    updatedAt: '2023-01-01T00:00:00Z'
  },
  {
    id: 2,
    userId: 2,
    age: 30,
    gender: '女',
    birthday: '1993-05-15',
    phoneNumber: '13800138002',
    address: '上海市浦东新区',
    occupation: '产品经理',
    bio: '专注用户体验设计',
    avatarUrl: 'https://example.com/avatar2.jpg',
    createdAt: '2023-01-02T00:00:00Z',
    updatedAt: '2023-01-02T00:00:00Z'
  }
];

const mockActivities: ActivityTrack[] = [
  {
    id: 1,
    userId: 1,
    activityType: 'LOGIN',
    description: '用户登录',
    longitude: 116.4074,
    latitude: 39.9042,
    location: '北京市',
    ipAddress: '192.168.1.1',
    deviceType: 'Desktop',
    operatingSystem: 'Windows 10',
    browser: 'Chrome',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    sessionId: 'session123',
    pageUrl: '/login',
    referrer: '/',
    duration: 5000,
    extraData: '{}',
    createdAt: '2023-01-01T10:00:00Z',
    timestamp: '2023-01-01T10:00:00Z'
  }
];

const mockPurchases: PurchaseHistory[] = [
  {
    id: 1,
    userId: 1,
    orderNumber: 'ORD20230001',
    productId: 101,
    productName: 'iPhone 15',
    category: '电子产品',
    brand: 'Apple',
    sku: 'IP15-128GB-BLK',
    quantity: 1,
    unitPrice: 5999.00,
    totalPrice: 5999.00,
    discountAmount: 0,
    actualPrice: 5999.00,
    paymentMethod: '支付宝',
    paymentStatus: '已支付',
    orderStatus: '已完成',
    deliveryAddress: '北京市朝阳区xxx街道',
    deliveryMethod: '快递',
    courierCompany: '顺丰速运',
    trackingNumber: 'SF1234567890',
    rating: 5,
    review: '非常满意',
    channel: '官网',
    purchaseTime: '2023-01-01T12:00:00Z',
    paymentTime: '2023-01-01T12:05:00Z',
    shipmentTime: '2023-01-02T09:00:00Z',
    completionTime: '2023-01-03T15:00:00Z',
    createdAt: '2023-01-01T12:00:00Z',
    updatedAt: '2023-01-03T15:00:00Z'
  }
];

const mockCampaigns: Campaign[] = [
  {
    id: '1',
    campaignId: 'CAMP001',
    advertiserId: 'ADV001',
    name: '春季促销活动',
    status: 'active',
    budget: {
      totalBudget: 10000,
      dailyBudget: 500,
      spentTotal: 2500,
      spentToday: 150,
      currency: 'CNY'
    },
    bidding: {
      bidStrategy: 'cpm',
      maxBid: 10,
      baseBid: 5
    },
    schedule: {
      startDate: '2024-03-01',
      endDate: '2024-03-31',
      timezone: 'UTC'
    },
    createdAt: '2024-01-15T10:00:00Z',
    updatedAt: '2024-01-15T10:00:00Z'
  },
  {
    id: '2',
    campaignId: 'CAMP002',
    advertiserId: 'ADV002',
    name: '夏季新品推广',
    status: 'paused',
    budget: {
      totalBudget: 15000,
      dailyBudget: 750,
      spentTotal: 5000,
      spentToday: 0,
      currency: 'CNY'
    },
    bidding: {
      bidStrategy: 'cpc',
      maxBid: 2,
      baseBid: 1
    },
    schedule: {
      startDate: '2024-06-01',
      endDate: '2024-08-31',
      timezone: 'UTC'
    },
    createdAt: '2024-05-01T10:00:00Z',
    updatedAt: '2024-05-01T10:00:00Z'
  },
  {
    id: '3',
    campaignId: 'CAMP003',
    advertiserId: 'ADV001',
    name: '双十一大促',
    status: 'completed',
    budget: {
      totalBudget: 50000,
      dailyBudget: 2000,
      spentTotal: 50000,
      spentToday: 0,
      currency: 'CNY'
    },
    bidding: {
      bidStrategy: 'cpa',
      maxBid: 50,
      baseBid: 30
    },
    schedule: {
      startDate: '2023-11-01',
      endDate: '2023-11-15',
      timezone: 'UTC'
    },
    createdAt: '2023-10-01T10:00:00Z',
    updatedAt: '2023-11-15T23:59:59Z'
  }
];

// 模拟网络延迟
const delay = (ms: number = 500) => new Promise(resolve => setTimeout(resolve, ms));

// 模拟API响应
const mockResponse = <T>(data: T) => ({
  data,
  status: 200,
  statusText: 'OK',
  headers: {},
  config: {}
});

// Mock用户管理API
export const mockUserApi = {
  getAllUsers: async () => {
    await delay();
    return mockResponse(mockUsers);
  },
  
  getUserById: async (id: number) => {
    await delay();
    const user = mockUsers.find(u => u.id === id);
    if (!user) throw new Error('User not found');
    return mockResponse(user);
  },
  
  createUser: async (user: User) => {
    await delay();
    const newUser = { ...user, id: Math.max(...mockUsers.map(u => u.id || 0)) + 1 };
    mockUsers.push(newUser);
    return mockResponse(newUser);
  },
  
  updateUser: async (id: number, user: User) => {
    await delay();
    const index = mockUsers.findIndex(u => u.id === id);
    if (index === -1) throw new Error('User not found');
    mockUsers[index] = { ...user, id };
    return mockResponse(mockUsers[index]);
  },
  
  deleteUser: async (id: number) => {
    await delay();
    const index = mockUsers.findIndex(u => u.id === id);
    if (index === -1) throw new Error('User not found');
    mockUsers.splice(index, 1);
    return mockResponse({});
  }
};

// Mock用户档案API
export const mockUserProfileApi = {
  getAllProfiles: async () => {
    await delay();
    return mockResponse(mockProfiles);
  },
  
  getProfileById: async (id: number) => {
    await delay();
    const profile = mockProfiles.find(p => p.id === id);
    if (!profile) throw new Error('Profile not found');
    return mockResponse(profile);
  },
  
  getProfileByUserId: async (userId: number) => {
    await delay();
    const profile = mockProfiles.find(p => p.userId === userId);
    if (!profile) throw new Error('Profile not found');
    return mockResponse(profile);
  },
  
  getProfilesByGender: async (gender: string) => {
    await delay();
    const profiles = mockProfiles.filter(p => p.gender === gender);
    return mockResponse(profiles);
  },
  
  getProfilesByAge: async (minAge: number, maxAge: number) => {
    await delay();
    const profiles = mockProfiles.filter(p => 
      p.age && p.age >= minAge && p.age <= maxAge
    );
    return mockResponse(profiles);
  },
  
  getProfilesByOccupation: async (occupation: string) => {
    await delay();
    const profiles = mockProfiles.filter(p => p.occupation === occupation);
    return mockResponse(profiles);
  },
  
  searchProfilesByAddress: async (keyword: string) => {
    await delay();
    const profiles = mockProfiles.filter(p => 
      p.address?.toLowerCase().includes(keyword.toLowerCase())
    );
    return mockResponse(profiles);
  },
  
  createProfile: async (profile: UserProfile) => {
    await delay();
    const newProfile = { 
      ...profile, 
      id: Math.max(...mockProfiles.map(p => p.id || 0)) + 1,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
    mockProfiles.push(newProfile);
    return mockResponse(newProfile);
  },
  
  updateProfile: async (id: number, profile: UserProfile) => {
    await delay();
    const index = mockProfiles.findIndex(p => p.id === id);
    if (index === -1) throw new Error('Profile not found');
    mockProfiles[index] = { 
      ...profile, 
      id, 
      updatedAt: new Date().toISOString() 
    };
    return mockResponse(mockProfiles[index]);
  },
  
  updateProfileByUserId: async (userId: number, profile: UserProfile) => {
    await delay();
    const index = mockProfiles.findIndex(p => p.userId === userId);
    if (index === -1) throw new Error('Profile not found');
    mockProfiles[index] = { 
      ...profile, 
      userId, 
      updatedAt: new Date().toISOString() 
    };
    return mockResponse(mockProfiles[index]);
  },
  
  deleteProfile: async (id: number) => {
    await delay();
    const index = mockProfiles.findIndex(p => p.id === id);
    if (index === -1) throw new Error('Profile not found');
    mockProfiles.splice(index, 1);
    return mockResponse({});
  },
  
  deleteProfileByUserId: async (userId: number) => {
    await delay();
    const index = mockProfiles.findIndex(p => p.userId === userId);
    if (index === -1) throw new Error('Profile not found');
    mockProfiles.splice(index, 1);
    return mockResponse({});
  }
};

// Mock活动跟踪API
export const mockActivityTrackApi = {
  getAllActivities: async () => {
    await delay();
    return mockResponse(mockActivities);
  },
  
  getActivityById: async (id: number) => {
    await delay();
    const activity = mockActivities.find(a => a.id === id);
    if (!activity) throw new Error('Activity not found');
    return mockResponse(activity);
  },
  
  getActivitiesByUserId: async (userId: number) => {
    await delay();
    const activities = mockActivities.filter(a => a.userId === userId);
    return mockResponse(activities);
  },
  
  getActivitiesByType: async (activityType: string) => {
    await delay();
    const activities = mockActivities.filter(a => a.activityType === activityType);
    return mockResponse(activities);
  },
  
  getActivitiesByDevice: async (deviceType: string) => {
    await delay();
    const activities = mockActivities.filter(a => a.deviceType === deviceType);
    return mockResponse(activities);
  },
  
  getActivitiesByTimeRange: async (startTime: string, endTime: string) => {
    await delay();
    const activities = mockActivities.filter(a => {
      const activityTime = new Date(a.createdAt || '');
      return activityTime >= new Date(startTime) && activityTime <= new Date(endTime);
    });
    return mockResponse(activities);
  },
  
  searchActivitiesByLocation: async (keyword: string) => {
    await delay();
    const activities = mockActivities.filter(a => 
      a.location?.toLowerCase().includes(keyword.toLowerCase())
    );
    return mockResponse(activities);
  },
  
  getActivitiesBySession: async (sessionId: string) => {
    await delay();
    const activities = mockActivities.filter(a => a.sessionId === sessionId);
    return mockResponse(activities);
  },
  
  getActivitiesByPageUrl: async (pageUrl: string) => {
    await delay();
    const activities = mockActivities.filter(a => a.pageUrl === pageUrl);
    return mockResponse(activities);
  },
  
  getRecentActivitiesByUserId: async (userId: number, limit: number = 10) => {
    await delay();
    const activities = mockActivities
      .filter(a => a.userId === userId)
      .sort((a, b) => new Date(b.createdAt || '').getTime() - new Date(a.createdAt || '').getTime())
      .slice(0, limit);
    return mockResponse(activities);
  },
  
  createActivity: async (activity: ActivityTrack) => {
    await delay();
    const newActivity = { 
      ...activity, 
      id: Math.max(...mockActivities.map(a => a.id || 0)) + 1,
      createdAt: new Date().toISOString(),
      timestamp: new Date().toISOString()
    };
    mockActivities.push(newActivity);
    return mockResponse(newActivity);
  },
  
  updateActivity: async (id: number, activity: ActivityTrack) => {
    await delay();
    const index = mockActivities.findIndex(a => a.id === id);
    if (index === -1) throw new Error('Activity not found');
    mockActivities[index] = { ...activity, id };
    return mockResponse(mockActivities[index]);
  },
  
  deleteActivity: async (id: number) => {
    await delay();
    const index = mockActivities.findIndex(a => a.id === id);
    if (index === -1) throw new Error('Activity not found');
    mockActivities.splice(index, 1);
    return mockResponse({});
  }
};

// Mock购买历史API
export const mockPurchaseHistoryApi = {
  getAllPurchases: async () => {
    await delay();
    return mockResponse(mockPurchases);
  },
  
  getPurchaseById: async (id: number) => {
    await delay();
    const purchase = mockPurchases.find(p => p.id === id);
    if (!purchase) throw new Error('Purchase not found');
    return mockResponse(purchase);
  },
  
  getPurchasesByUserId: async (userId: number) => {
    await delay();
    const purchases = mockPurchases.filter(p => p.userId === userId);
    return mockResponse(purchases);
  },
  
  getPurchaseByOrderNumber: async (orderNumber: string) => {
    await delay();
    const purchase = mockPurchases.find(p => p.orderNumber === orderNumber);
    if (!purchase) throw new Error('Purchase not found');
    return mockResponse(purchase);
  },
  
  getPurchasesByCategory: async (category: string) => {
    await delay();
    const purchases = mockPurchases.filter(p => p.category === category);
    return mockResponse(purchases);
  },
  
  getPurchasesByBrand: async (brand: string) => {
    await delay();
    const purchases = mockPurchases.filter(p => p.brand === brand);
    return mockResponse(purchases);
  },
  
  getPurchasesByPaymentMethod: async (paymentMethod: string) => {
    await delay();
    const purchases = mockPurchases.filter(p => p.paymentMethod === paymentMethod);
    return mockResponse(purchases);
  },
  
  getPurchasesByOrderStatus: async (orderStatus: string) => {
    await delay();
    const purchases = mockPurchases.filter(p => p.orderStatus === orderStatus);
    return mockResponse(purchases);
  },
  
  getPurchasesByChannel: async (channel: string) => {
    await delay();
    const purchases = mockPurchases.filter(p => p.channel === channel);
    return mockResponse(purchases);
  },
  
  getPurchasesByTimeRange: async (startTime: string, endTime: string) => {
    await delay();
    const purchases = mockPurchases.filter(p => {
      const purchaseTime = new Date(p.purchaseTime || '');
      return purchaseTime >= new Date(startTime) && purchaseTime <= new Date(endTime);
    });
    return mockResponse(purchases);
  },
  
  getPurchasesByPriceRange: async (minPrice: number, maxPrice: number) => {
    await delay();
    const purchases = mockPurchases.filter(p => 
      p.totalPrice >= minPrice && p.totalPrice <= maxPrice
    );
    return mockResponse(purchases);
  },
  
  searchPurchases: async (keyword: string) => {
    await delay();
    const purchases = mockPurchases.filter(p => 
      p.productName.toLowerCase().includes(keyword.toLowerCase()) ||
      p.orderNumber.toLowerCase().includes(keyword.toLowerCase())
    );
    return mockResponse(purchases);
  },
  
  getPurchaseStats: async () => {
    await delay();
    const stats = {
      totalPurchases: mockPurchases.length,
      totalAmount: mockPurchases.reduce((sum, p) => sum + p.totalPrice, 0),
      averageOrderValue: mockPurchases.length > 0 
        ? mockPurchases.reduce((sum, p) => sum + p.totalPrice, 0) / mockPurchases.length 
        : 0
    };
    return mockResponse(stats);
  },
  
  createPurchase: async (purchase: PurchaseHistory) => {
    await delay();
    const newPurchase = { 
      ...purchase, 
      id: Math.max(...mockPurchases.map(p => p.id || 0)) + 1,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
    mockPurchases.push(newPurchase);
    return mockResponse(newPurchase);
  },
  
  createPurchases: async (purchases: PurchaseHistory[]) => {
    await delay();
    const newPurchases = purchases.map((purchase, index) => ({
      ...purchase,
      id: Math.max(...mockPurchases.map(p => p.id || 0)) + index + 1,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }));
    mockPurchases.push(...newPurchases);
    return mockResponse(newPurchases);
  },
  
  updatePurchase: async (id: number, purchase: PurchaseHistory) => {
    await delay();
    const index = mockPurchases.findIndex(p => p.id === id);
    if (index === -1) throw new Error('Purchase not found');
    mockPurchases[index] = { 
      ...purchase, 
      id, 
      updatedAt: new Date().toISOString() 
    };
    return mockResponse(mockPurchases[index]);
  },
  
  deletePurchase: async (id: number) => {
    await delay();
    const index = mockPurchases.findIndex(p => p.id === id);
    if (index === -1) throw new Error('Purchase not found');
    mockPurchases.splice(index, 1);
    return mockResponse({});
  }
};

// Mock广告活动API
export const mockCampaignApi = {
  getAllCampaigns: async (page: number = 0, size: number = 10) => {
    await delay();
    const startIndex = page * size;
    const endIndex = startIndex + size;
    const paginatedCampaigns = mockCampaigns.slice(startIndex, endIndex);
    return mockResponse({
      content: paginatedCampaigns,
      totalElements: mockCampaigns.length,
      totalPages: Math.ceil(mockCampaigns.length / size),
      size,
      number: page
    });
  },

  getCampaignById: async (id: string) => {
    await delay();
    const campaign = mockCampaigns.find(c => c.id === id);
    if (!campaign) throw new Error('Campaign not found');
    return mockResponse(campaign);
  },

  createCampaign: async (campaign: Campaign) => {
    await delay();
    const newCampaign = {
      ...campaign,
      id: String(Math.max(...mockCampaigns.map(c => parseInt(c.id || '0'))) + 1),
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
    mockCampaigns.push(newCampaign);
    return mockResponse(newCampaign);
  },

  updateCampaign: async (id: string, campaign: Campaign) => {
    await delay();
    const index = mockCampaigns.findIndex(c => c.id === id);
    if (index === -1) throw new Error('Campaign not found');
    mockCampaigns[index] = {
      ...campaign,
      id,
      updatedAt: new Date().toISOString()
    };
    return mockResponse(mockCampaigns[index]);
  },

  deleteCampaign: async (id: string) => {
    await delay();
    const index = mockCampaigns.findIndex(c => c.id === id);
    if (index === -1) throw new Error('Campaign not found');
    mockCampaigns.splice(index, 1);
    return mockResponse({});
  },

  updateCampaignStatus: async (id: string, status: string) => {
    await delay();
    const index = mockCampaigns.findIndex(c => c.id === id);
    if (index === -1) throw new Error('Campaign not found');
    mockCampaigns[index].status = status;
    mockCampaigns[index].updatedAt = new Date().toISOString();
    return mockResponse(mockCampaigns[index]);
  },

  getCampaignsByAdvertiser: async (advertiserId: string) => {
    await delay();
    const campaigns = mockCampaigns.filter(c => c.advertiserId === advertiserId);
    return mockResponse(campaigns);
  },

  getCampaignsByStatus: async (status: string) => {
    await delay();
    const campaigns = mockCampaigns.filter(c => c.status === status);
    return mockResponse(campaigns);
  },

  getCampaignStatistics: async () => {
    await delay();
    const stats = {
      total: mockCampaigns.length,
      active: mockCampaigns.filter(c => c.status === 'active').length,
      paused: mockCampaigns.filter(c => c.status === 'paused').length,
      completed: mockCampaigns.filter(c => c.status === 'completed').length
    };
    return mockResponse(stats);
  },

  searchCampaigns: async (query: string) => {
    await delay();
    const campaigns = mockCampaigns.filter(c => 
      c.name.toLowerCase().includes(query.toLowerCase()) ||
      c.campaignId.toLowerCase().includes(query.toLowerCase()) ||
      c.advertiserId.toLowerCase().includes(query.toLowerCase())
    );
    return mockResponse(campaigns);
  }
};

// 导出所有Mock API
export const mockApi = {
  userApi: mockUserApi,
  userProfileApi: mockUserProfileApi,
  activityTrackApi: mockActivityTrackApi,
  purchaseHistoryApi: mockPurchaseHistoryApi,
  campaignApi: mockCampaignApi
};

export default mockApi;