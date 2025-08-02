import axios from 'axios';
import { config, logger } from '../config/environment';
import { mockApi } from './mockApi';

const API_BASE_URL = 'http://localhost:8080';

const api = axios.create({
  baseURL: config.apiBaseUrl || API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 根据环境配置选择使用Mock API还是真实API
const shouldUseMockApi = config.useMockApi || false;

// 如果使用Mock API，则重写api实例的方法
if (shouldUseMockApi) {
  // 这里可以添加Mock API的逻辑
  logger.info('Using Mock API mode');
} else {
  logger.info('Using Real API mode');
}

// 请求拦截器 - 记录API调用
api.interceptors.request.use(
  (config) => {
    const method = config.method?.toUpperCase() || 'UNKNOWN';
    const url = config.url || '';
    const fullUrl = `${config.baseURL}${url}`;
    
    logger.apiCall(method, fullUrl, config.data);
    logger.debug(`Request Headers:`, config.headers);
    
    return config;
  },
  (error) => {
    logger.error('Request Error:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器 - 记录API响应和错误
api.interceptors.response.use(
  (response) => {
    const method = response.config.method?.toUpperCase() || 'UNKNOWN';
    const url = response.config.url || '';
    const fullUrl = `${response.config.baseURL}${url}`;
    
    logger.apiResponse(method, fullUrl, {
      status: response.status,
      statusText: response.statusText,
      data: response.data
    });
    
    return response;
  },
  (error) => {
    const method = error.config?.method?.toUpperCase() || 'UNKNOWN';
    const url = error.config?.url || '';
    const fullUrl = error.config ? `${error.config.baseURL}${url}` : 'Unknown URL';
    
    logger.apiError(method, fullUrl, {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data
    });
    
    return Promise.reject(error);
  }
);

// 用户接口
export interface User {
  id?: number;
  name: string;
  email: string;
}

// 用户档案接口
export interface UserProfile {
  id?: number;
  userId: number;
  age?: number;
  gender?: string;
  birthday?: string;
  phoneNumber?: string;
  address?: string;
  occupation?: string;
  bio?: string;
  avatarUrl?: string;
  createdAt?: string;
  updatedAt?: string;
}

// 活动轨迹接口
export interface ActivityTrack {
  id?: number;
  userId: number;
  activityType: string;
  description: string;
  longitude?: number;
  latitude?: number;
  location?: string;
  ipAddress?: string;
  deviceType?: string;
  operatingSystem?: string;
  browser?: string;
  userAgent?: string;
  sessionId?: string;
  pageUrl?: string;
  referrer?: string;
  duration?: number;
  extraData?: string;
  createdAt?: string;
  timestamp?: string; // 兼容字段
}

// 购买历史接口
export interface PurchaseHistory {
  id?: number;
  userId: number;
  orderNumber: string;
  productId?: number;
  productName: string;
  category?: string;
  brand?: string;
  sku?: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  discountAmount?: number;
  actualPrice?: number;
  paymentMethod?: string;
  paymentStatus?: string;
  orderStatus?: string;
  deliveryAddress?: string;
  deliveryMethod?: string;
  courierCompany?: string;
  trackingNumber?: string;
  couponId?: string;
  couponName?: string;
  rating?: number;
  review?: string;
  channel?: string;
  salesPersonId?: number;
  remarks?: string;
  purchaseTime?: string;
  paymentTime?: string;
  shipmentTime?: string;
  completionTime?: string;
  createdAt?: string;
  updatedAt?: string;
}

// 广告活动接口
export interface Campaign {
  id?: string;
  campaignId: string;
  advertiserId: string;
  name: string;
  status: string; // active, paused, completed
  budget?: {
    totalBudget?: number;
    dailyBudget?: number;
    spentTotal?: number;
    spentToday?: number;
    currency?: string;
  };
  targeting?: {
    geo?: {
      includedCountries?: string[];
      excludedCountries?: string[];
      includedRegions?: string[];
      includedCities?: string[];
    };
    device?: {
      deviceTypes?: number[];
      operatingSystems?: string[];
      browsers?: string[];
    };
    audience?: {
      ageRange?: {
        min?: number;
        max?: number;
      };
      genders?: string[];
      interests?: string[];
    };
    time?: {
      daysOfWeek?: number[];
      hoursOfDay?: number[];
    };
  };
  bidding?: {
    bidStrategy?: string; // cpm, cpc, cpa
    maxBid?: number;
    baseBid?: number;
    bidAdjustments?: { [key: string]: number };
  };
  creatives?: {
    creativeId?: string;
    format?: string;
    width?: number;
    height?: number;
    html?: string;
    clickUrl?: string;
    impressionTrackers?: string[];
    clickTrackers?: string[];
  }[];
  frequencyCap?: {
    impressionsPerUserPerDay?: number;
    impressionsPerUserPerHour?: number;
  };
  schedule?: {
    startDate?: string;
    endDate?: string;
    timezone?: string;
  };
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
}

// 用户管理 API
export const userApi = {
  // 获取所有用户
  getAllUsers: () => api.get<User[]>('/users'),
  
  // 根据ID获取用户
  getUserById: (id: number) => api.get<User>(`/users/${id}`),
  
  // 创建新用户
  createUser: (user: User) => api.post<User>('/users', user),
  
  // 更新用户
  updateUser: (id: number, user: User) => api.put<User>(`/users/${id}`, user),
  
  // 删除用户
  deleteUser: (id: number) => api.delete(`/users/${id}`),
};

// 用户档案管理 API
export const userProfileApi = {
  // 获取所有用户档案
  getAllProfiles: () => api.get<UserProfile[]>('/api/users/profiles'),
  
  // 根据档案ID获取用户档案
  getProfileById: (id: number) => api.get<UserProfile>(`/api/users/profiles/${id}`),
  
  // 根据用户ID获取用户档案
  getProfileByUserId: (userId: number) => api.get<UserProfile>(`/api/users/profiles/user/${userId}`),
  
  // 根据性别获取用户档案
  getProfilesByGender: (gender: string) => api.get<UserProfile[]>(`/api/users/profiles/gender/${gender}`),
  
  // 根据年龄范围获取用户档案
  getProfilesByAge: (minAge: number, maxAge: number) => 
    api.get<UserProfile[]>(`/api/users/profiles/age?minAge=${minAge}&maxAge=${maxAge}`),
  
  // 根据职业获取用户档案
  getProfilesByOccupation: (occupation: string) => 
    api.get<UserProfile[]>(`/api/users/profiles/occupation/${occupation}`),
  
  // 根据地址关键词搜索用户档案
  searchProfilesByAddress: (keyword: string) => 
    api.get<UserProfile[]>(`/api/users/profiles/search?keyword=${keyword}`),
  
  // 创建用户档案
  createProfile: (profile: UserProfile) => api.post<UserProfile>('/api/users/profiles', profile),
  
  // 更新用户档案
  updateProfile: (id: number, profile: UserProfile) => 
    api.put<UserProfile>(`/api/users/profiles/${id}`, profile),
  
  // 根据用户ID更新用户档案
  updateProfileByUserId: (userId: number, profile: UserProfile) => 
    api.put<UserProfile>(`/api/users/profiles/user/${userId}`, profile),
  
  // 删除用户档案
  deleteProfile: (id: number) => api.delete(`/api/users/profiles/${id}`),
  
  // 根据用户ID删除用户档案
  deleteProfileByUserId: (userId: number) => api.delete(`/api/users/profiles/user/${userId}`),
};

// 活动跟踪 API
export const activityTrackApi = {
  // 获取所有活动轨迹
  getAllActivities: () => api.get<ActivityTrack[]>('/api/users/activities'),
  
  // 根据ID获取活动轨迹
  getActivityById: (id: number) => api.get<ActivityTrack>(`/api/users/activities/${id}`),
  
  // 根据用户ID获取活动轨迹
  getActivitiesByUserId: (userId: number) => 
    api.get<ActivityTrack[]>(`/api/users/activities/user/${userId}`),
  
  // 根据活动类型获取轨迹
  getActivitiesByType: (activityType: string) => 
    api.get<ActivityTrack[]>(`/api/users/activities/type/${activityType}`),
  
  // 根据设备类型获取轨迹
  getActivitiesByDevice: (deviceType: string) => 
    api.get<ActivityTrack[]>(`/api/users/activities/device/${deviceType}`),
  
  // 根据时间范围获取轨迹
  getActivitiesByTimeRange: (startTime: string, endTime: string) => 
    api.get<ActivityTrack[]>(`/api/users/activities/time-range?startTime=${startTime}&endTime=${endTime}`),
  
  // 根据位置关键词搜索轨迹
  searchActivitiesByLocation: (keyword: string) => 
    api.get<ActivityTrack[]>(`/api/users/activities/search?keyword=${keyword}`),
  
  // 根据会话ID获取轨迹
  getActivitiesBySession: (sessionId: string) => 
    api.get<ActivityTrack[]>(`/api/users/activities/session/${sessionId}`),
  
  // 根据页面URL获取轨迹
  getActivitiesByPageUrl: (pageUrl: string) => 
    api.get<ActivityTrack[]>(`/api/users/activities/page?pageUrl=${encodeURIComponent(pageUrl)}`),
  
  // 获取用户最近的活动轨迹
  getRecentActivitiesByUserId: (userId: number, limit: number = 10) => 
    api.get<ActivityTrack[]>(`/api/users/activities/user/${userId}/recent?limit=${limit}`),
  
  // 创建活动轨迹
  createActivity: (activity: ActivityTrack) => api.post<ActivityTrack>('/api/users/activities', activity),
  
  // 更新活动轨迹
  updateActivity: (id: number, activity: ActivityTrack) => 
    api.put<ActivityTrack>(`/api/users/activities/${id}`, activity),
  
  // 删除活动轨迹
  deleteActivity: (id: number) => api.delete(`/api/users/activities/${id}`),
};

// 购买历史管理 API
export const purchaseHistoryApi = {
  // 获取所有购买记录
  getAllPurchases: () => api.get<PurchaseHistory[]>('/api/users/purchases'),
  
  // 根据ID获取购买记录
  getPurchaseById: (id: number) => api.get<PurchaseHistory>(`/api/users/purchases/${id}`),
  
  // 根据用户ID获取购买记录
  getPurchasesByUserId: (userId: number) => 
    api.get<PurchaseHistory[]>(`/api/users/purchases/user/${userId}`),
  
  // 根据订单号获取购买记录
  getPurchaseByOrderNumber: (orderNumber: string) => 
    api.get<PurchaseHistory>(`/api/users/purchases/order/${orderNumber}`),
  
  // 根据商品分类获取购买记录
  getPurchasesByCategory: (category: string) => 
    api.get<PurchaseHistory[]>(`/api/users/purchases/category/${category}`),
  
  // 根据品牌获取购买记录
  getPurchasesByBrand: (brand: string) => 
    api.get<PurchaseHistory[]>(`/api/users/purchases/brand/${brand}`),
  
  // 根据支付方式获取购买记录
  getPurchasesByPaymentMethod: (paymentMethod: string) => 
    api.get<PurchaseHistory[]>(`/api/users/purchases/payment/${paymentMethod}`),
  
  // 根据订单状态获取购买记录
  getPurchasesByOrderStatus: (orderStatus: string) => 
    api.get<PurchaseHistory[]>(`/api/users/purchases/status/${orderStatus}`),
  
  // 根据渠道获取购买记录
  getPurchasesByChannel: (channel: string) => 
    api.get<PurchaseHistory[]>(`/api/users/purchases/channel/${channel}`),
  
  // 根据时间范围获取购买记录
  getPurchasesByTimeRange: (startTime: string, endTime: string) => 
    api.get<PurchaseHistory[]>(`/api/users/purchases/time-range?startTime=${startTime}&endTime=${endTime}`),
  
  // 根据价格范围获取购买记录
  getPurchasesByPriceRange: (minPrice: number, maxPrice: number) => 
    api.get<PurchaseHistory[]>(`/api/users/purchases/price-range?minPrice=${minPrice}&maxPrice=${maxPrice}`),
  
  // 搜索购买记录
  searchPurchases: (keyword: string) => 
    api.get<PurchaseHistory[]>(`/api/users/purchases/search?keyword=${keyword}`),
  
  // 获取购买统计信息
  getPurchaseStats: () => 
    api.get<any>('/api/users/purchases/stats'),
  
  // 创建购买记录
  createPurchase: (purchase: PurchaseHistory) => 
    api.post<PurchaseHistory>('/api/users/purchases', purchase),
  
  // 批量创建购买记录
  createPurchases: (purchases: PurchaseHistory[]) => 
    api.post<PurchaseHistory[]>('/api/users/purchases/batch', purchases),
  
  // 更新购买记录
  updatePurchase: (id: number, purchase: PurchaseHistory) => 
    api.put<PurchaseHistory>(`/api/users/purchases/${id}`, purchase),
  
  // 删除购买记录
  deletePurchase: (id: number) => api.delete(`/api/users/purchases/${id}`),
};

// 广告活动管理 API
export const campaignApi = {
  // 获取所有广告活动（分页）
  getAllCampaigns: (page: number = 0, size: number = 10) => 
    api.get<{content: Campaign[], totalElements: number, totalPages: number}>(`/api/campaigns?page=${page}&size=${size}`),
  
  // 根据ID获取广告活动
  getCampaignById: (id: string) => api.get<Campaign>(`/api/campaigns/${id}`),
  
  // 创建新的广告活动
  createCampaign: (campaign: Campaign) => api.post<Campaign>('/api/campaigns', campaign),
  
  // 更新广告活动
  updateCampaign: (id: string, campaign: Campaign) => 
    api.put<Campaign>(`/api/campaigns/${id}`, campaign),
  
  // 删除广告活动
  deleteCampaign: (id: string) => api.delete(`/api/campaigns/${id}`),
  
  // 更新广告活动状态
  updateCampaignStatus: (id: string, status: string) => 
    api.patch<Campaign>(`/api/campaigns/${id}/status`, { status }),
  
  // 根据广告主ID获取广告活动
  getCampaignsByAdvertiser: (advertiserId: string) => 
    api.get<Campaign[]>(`/api/campaigns/advertiser/${advertiserId}`),
  
  // 根据状态获取广告活动
  getCampaignsByStatus: (status: string) => 
    api.get<Campaign[]>(`/api/campaigns/status/${status}`),
  
  // 获取广告活动统计信息
  getCampaignStatistics: () => 
    api.get<{total: number, active: number, paused: number, completed: number}>('/api/campaigns/statistics'),
  
  // 搜索广告活动
  searchCampaigns: (query: string) => 
    api.get<Campaign[]>(`/api/campaigns/search?query=${encodeURIComponent(query)}`),
};

// 根据环境配置选择API实例
const getApiInstance = () => {
  if (shouldUseMockApi) {
    logger.info('API calls will use Mock data');
    return {
      userApi: mockApi.userApi,
      userProfileApi: mockApi.userProfileApi,
      activityTrackApi: mockApi.activityTrackApi,
      purchaseHistoryApi: mockApi.purchaseHistoryApi,
      campaignApi: mockApi.campaignApi
    };
  } else {
    logger.info('API calls will use Real backend');
    return {
      userApi,
      userProfileApi,
      activityTrackApi,
      purchaseHistoryApi,
      campaignApi
    };
  }
};

// 导出选择的API实例
const apiInstance = getApiInstance();

export const {
  userApi: selectedUserApi,
  userProfileApi: selectedUserProfileApi,
  activityTrackApi: selectedActivityTrackApi,
  purchaseHistoryApi: selectedPurchaseHistoryApi,
  campaignApi: selectedCampaignApi
} = apiInstance;

// 为了向后兼容，也导出原始的API（已在上面单独导出，这里不需要重复）

export default api;