// Mock axios
jest.mock('axios', () => ({
  create: jest.fn(() => ({
    get: jest.fn().mockResolvedValue({ data: [] }),
    post: jest.fn().mockResolvedValue({ data: {} }),
    put: jest.fn().mockResolvedValue({ data: {} }),
    delete: jest.fn().mockResolvedValue({ data: {} }),
    interceptors: {
      request: {
        use: jest.fn(),
      },
      response: {
        use: jest.fn(),
      },
    },
  })),
  default: {
    create: jest.fn(() => ({
      get: jest.fn().mockResolvedValue({ data: [] }),
      post: jest.fn().mockResolvedValue({ data: {} }),
      put: jest.fn().mockResolvedValue({ data: {} }),
      delete: jest.fn().mockResolvedValue({ data: {} }),
      interceptors: {
        request: {
          use: jest.fn(),
        },
        response: {
          use: jest.fn(),
        },
      },
    })),
  },
}));

// Mock logger
jest.mock('../utils/logger', () => ({
  logger: {
    apiCall: jest.fn(),
    apiResponse: jest.fn(),
    apiError: jest.fn(),
    debug: jest.fn(),
    error: jest.fn(),
  },
}));

import axios from 'axios';
import { logger } from '../utils/logger';

const mockedAxios = axios as jest.Mocked<typeof axios>;
const mockLogger = logger as jest.Mocked<typeof logger>;

// Get the mock axios instance for testing
const mockAxiosInstance = mockedAxios.create() as any;

// Now import the api module after setting up mocks
import { userApi, userProfileApi, activityTrackApi, purchaseHistoryApi, initializeInterceptors } from './api';

describe('API Configuration', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should have axios instance configured', () => {
    expect(axios.create).toHaveBeenCalledWith({
      baseURL: 'http://localhost:8080',
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  });

  it('should initialize interceptors when not in test environment', () => {
    initializeInterceptors();
    
    expect(mockAxiosInstance.interceptors.request.use).toHaveBeenCalled();
    expect(mockAxiosInstance.interceptors.response.use).toHaveBeenCalled();
  });

  it('should handle request interceptor success', () => {
    const mockConfig = {
      method: 'GET',
      url: '/test',
      baseURL: 'http://localhost:8080',
      data: { test: 'data' },
      headers: { 'Content-Type': 'application/json' }
    };
    
    initializeInterceptors();
    
    // Get the success handler from the first call
    const successHandler = (mockAxiosInstance.interceptors.request.use as jest.Mock).mock.calls[0][0];
    const result = successHandler(mockConfig);
    
    expect(result).toBe(mockConfig);
    expect(mockLogger.apiCall).toHaveBeenCalledWith('GET', 'http://localhost:8080/test', { test: 'data' });
    expect(mockLogger.debug).toHaveBeenCalledWith('Request Headers:', mockConfig.headers);
  });

  it('should handle request interceptor error', async () => {
    const mockError = new Error('Request failed');
    
    initializeInterceptors();
    
    // Get the error handler from the first call
    const errorHandler = (mockAxiosInstance.interceptors.request.use as jest.Mock).mock.calls[0][1];
    
    await expect(errorHandler(mockError)).rejects.toBe(mockError);
    expect(mockLogger.error).toHaveBeenCalledWith('Request Error:', mockError);
  });

  it('should handle response interceptor success', () => {
    const mockResponse = {
      config: {
        method: 'GET',
        url: '/test',
        baseURL: 'http://localhost:8080'
      },
      status: 200,
      statusText: 'OK',
      data: { result: 'success' }
    };
    
    initializeInterceptors();
    
    // Get the success handler from the first call
    const successHandler = (mockAxiosInstance.interceptors.response.use as jest.Mock).mock.calls[0][0];
    const result = successHandler(mockResponse);
    
    expect(result).toBe(mockResponse);
    expect(mockLogger.apiResponse).toHaveBeenCalledWith('GET', 'http://localhost:8080/test', {
      status: 200,
      statusText: 'OK',
      data: { result: 'success' }
    });
  });

  it('should handle response interceptor error', async () => {
    const mockError = {
      message: 'API Error',
      config: {
        method: 'POST',
        url: '/test',
        baseURL: 'http://localhost:8080'
      },
      response: {
        status: 500,
        statusText: 'Internal Server Error',
        data: { error: 'Server error' }
      }
    };
    
    initializeInterceptors();
    
    // Get the error handler from the first call
    const errorHandler = (mockAxiosInstance.interceptors.response.use as jest.Mock).mock.calls[0][1];
    
    await expect(errorHandler(mockError)).rejects.toBe(mockError);
    expect(mockLogger.apiError).toHaveBeenCalledWith('POST', 'http://localhost:8080/test', {
      message: 'API Error',
      status: 500,
      statusText: 'Internal Server Error',
      data: { error: 'Server error' }
    });
  });

  it('should handle response interceptor error without config', async () => {
    const mockError = {
      message: 'Network Error'
    };
    
    initializeInterceptors();
    
    // Get the error handler from the first call
    const errorHandler = (mockAxiosInstance.interceptors.response.use as jest.Mock).mock.calls[0][1];
    
    await expect(errorHandler(mockError)).rejects.toBe(mockError);
    expect(mockLogger.apiError).toHaveBeenCalledWith('UNKNOWN', 'Unknown URL', {
      message: 'Network Error',
      status: undefined,
      statusText: undefined,
      data: undefined
    });
  });
});

describe('userApi', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call getAllUsers', async () => {
    const mockUsers = [{ id: 1, name: 'John', email: 'john@example.com' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockUsers });

    const result = await userApi.getAllUsers();

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/users');
    expect(result.data).toEqual(mockUsers);
  });

  it('should call getUserById', async () => {
    const mockUser = { id: 1, name: 'John', email: 'john@example.com' };
    mockAxiosInstance.get.mockResolvedValue({ data: mockUser });

    const result = await userApi.getUserById(1);

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/users/1');
    expect(result.data).toEqual(mockUser);
  });

  it('should call createUser', async () => {
    const newUser = { name: 'John', email: 'john@example.com' };
    const createdUser = { id: 1, ...newUser };
    mockAxiosInstance.post.mockResolvedValue({ data: createdUser });

    const result = await userApi.createUser(newUser);

    expect(mockAxiosInstance.post).toHaveBeenCalledWith('/users', newUser);
    expect(result.data).toEqual(createdUser);
  });

  it('should call updateUser', async () => {
    const updatedUser = { id: 1, name: 'John Updated', email: 'john.updated@example.com' };
    mockAxiosInstance.put.mockResolvedValue({ data: updatedUser });

    const result = await userApi.updateUser(1, updatedUser);

    expect(mockAxiosInstance.put).toHaveBeenCalledWith('/users/1', updatedUser);
    expect(result.data).toEqual(updatedUser);
  });

  it('should call deleteUser', async () => {
    mockAxiosInstance.delete.mockResolvedValue({ data: {} });

    await userApi.deleteUser(1);

    expect(mockAxiosInstance.delete).toHaveBeenCalledWith('/users/1');
  });
});

describe('userProfileApi', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call getAllProfiles', async () => {
    const mockProfiles = [{ id: 1, userId: 1, age: 25 }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockProfiles });

    const result = await userProfileApi.getAllProfiles();

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/profiles');
    expect(result.data).toEqual(mockProfiles);
  });

  it('should call getProfileById', async () => {
    const mockProfile = { id: 1, userId: 1, age: 25 };
    mockAxiosInstance.get.mockResolvedValue({ data: mockProfile });

    const result = await userProfileApi.getProfileById(1);

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/profiles/1');
    expect(result.data).toEqual(mockProfile);
  });

  it('should call getProfileByUserId', async () => {
    const mockProfile = { id: 1, userId: 1, age: 25 };
    mockAxiosInstance.get.mockResolvedValue({ data: mockProfile });

    const result = await userProfileApi.getProfileByUserId(1);

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/profiles/user/1');
    expect(result.data).toEqual(mockProfile);
  });

  it('should call getProfilesByGender', async () => {
    const mockProfiles = [{ id: 1, userId: 1, gender: 'male' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockProfiles });

    const result = await userProfileApi.getProfilesByGender('male');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/profiles/gender/male');
    expect(result.data).toEqual(mockProfiles);
  });

  it('should call getProfilesByAge', async () => {
    const mockProfiles = [{ id: 1, userId: 1, age: 25 }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockProfiles });

    const result = await userProfileApi.getProfilesByAge(20, 30);

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/profiles/age?minAge=20&maxAge=30');
    expect(result.data).toEqual(mockProfiles);
  });

  it('should call getProfilesByOccupation', async () => {
    const mockProfiles = [{ id: 1, userId: 1, occupation: 'Engineer' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockProfiles });

    const result = await userProfileApi.getProfilesByOccupation('Engineer');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/profiles/occupation/Engineer');
    expect(result.data).toEqual(mockProfiles);
  });

  it('should call searchProfilesByAddress', async () => {
    const mockProfiles = [{ id: 1, userId: 1, address: 'New York' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockProfiles });

    const result = await userProfileApi.searchProfilesByAddress('New York');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/profiles/search?keyword=New York');
    expect(result.data).toEqual(mockProfiles);
  });

  it('should call createProfile', async () => {
    const newProfile = { userId: 1, age: 25 };
    const createdProfile = { id: 1, ...newProfile };
    mockAxiosInstance.post.mockResolvedValue({ data: createdProfile });

    const result = await userProfileApi.createProfile(newProfile);

    expect(mockAxiosInstance.post).toHaveBeenCalledWith('/api/users/profiles', newProfile);
    expect(result.data).toEqual(createdProfile);
  });

  it('should call updateProfile', async () => {
    const updatedProfile = { id: 1, userId: 1, age: 26 };
    mockAxiosInstance.put.mockResolvedValue({ data: updatedProfile });

    const result = await userProfileApi.updateProfile(1, updatedProfile);

    expect(mockAxiosInstance.put).toHaveBeenCalledWith('/api/users/profiles/1', updatedProfile);
    expect(result.data).toEqual(updatedProfile);
  });

  it('should call updateProfileByUserId', async () => {
    const updatedProfile = { id: 1, userId: 1, age: 26 };
    mockAxiosInstance.put.mockResolvedValue({ data: updatedProfile });

    const result = await userProfileApi.updateProfileByUserId(1, updatedProfile);

    expect(mockAxiosInstance.put).toHaveBeenCalledWith('/api/users/profiles/user/1', updatedProfile);
    expect(result.data).toEqual(updatedProfile);
  });

  it('should call deleteProfile', async () => {
    mockAxiosInstance.delete.mockResolvedValue({ data: {} });

    await userProfileApi.deleteProfile(1);

    expect(mockAxiosInstance.delete).toHaveBeenCalledWith('/api/users/profiles/1');
  });

  it('should call deleteProfileByUserId', async () => {
    mockAxiosInstance.delete.mockResolvedValue({ data: {} });

    await userProfileApi.deleteProfileByUserId(1);

    expect(mockAxiosInstance.delete).toHaveBeenCalledWith('/api/users/profiles/user/1');
  });
});

describe('activityTrackApi', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call getAllActivities', async () => {
    const mockActivities = [{ id: 1, userId: 1, activityType: 'login' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockActivities });

    const result = await activityTrackApi.getAllActivities();

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/activity-tracks');
    expect(result.data).toEqual(mockActivities);
  });

  it('should call getActivityById', async () => {
    const mockActivity = { id: 1, userId: 1, activityType: 'login' };
    mockAxiosInstance.get.mockResolvedValue({ data: mockActivity });

    const result = await activityTrackApi.getActivityById(1);

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/activity-tracks/1');
    expect(result.data).toEqual(mockActivity);
  });

  it('should call getActivitiesByUserId', async () => {
    const mockActivities = [{ id: 1, userId: 1, activityType: 'login' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockActivities });

    const result = await activityTrackApi.getActivitiesByUserId(1);

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/activity-tracks/user/1');
    expect(result.data).toEqual(mockActivities);
  });

  it('should call getActivitiesByType', async () => {
    const mockActivities = [{ id: 1, userId: 1, activityType: 'login' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockActivities });

    const result = await activityTrackApi.getActivitiesByType('login');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/activity-tracks/type/login');
    expect(result.data).toEqual(mockActivities);
  });

  it('should call getActivitiesByDevice', async () => {
    const mockActivities = [{ id: 1, userId: 1, deviceType: 'mobile' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockActivities });

    const result = await activityTrackApi.getActivitiesByDevice('mobile');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/activity-tracks/device/mobile');
    expect(result.data).toEqual(mockActivities);
  });

  it('should call getActivitiesByTimeRange', async () => {
    const mockActivities = [{ id: 1, userId: 1, activityType: 'login' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockActivities });

    const result = await activityTrackApi.getActivitiesByTimeRange('2023-01-01', '2023-01-31');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/activity-tracks/time-range?startTime=2023-01-01&endTime=2023-01-31');
    expect(result.data).toEqual(mockActivities);
  });

  it('should call searchActivitiesByLocation', async () => {
    const mockActivities = [{ id: 1, userId: 1, location: 'New York' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockActivities });

    const result = await activityTrackApi.searchActivitiesByLocation('New York');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/activity-tracks/location/search?keyword=New York');
    expect(result.data).toEqual(mockActivities);
  });

  it('should call getActivitiesBySession', async () => {
    const mockActivities = [{ id: 1, userId: 1, sessionId: 'session123' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockActivities });

    const result = await activityTrackApi.getActivitiesBySession('session123');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/activity-tracks/session/session123');
    expect(result.data).toEqual(mockActivities);
  });

  it('should call getRecentActivitiesByUserId', async () => {
    const mockActivities = [{ id: 1, userId: 1, activityType: 'login' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockActivities });

    const result = await activityTrackApi.getRecentActivitiesByUserId(1, 10);

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/activity-tracks/user/1/recent?limit=10');
    expect(result.data).toEqual(mockActivities);
  });

  it('should call createActivity', async () => {
    const newActivity = { userId: 1, activityType: 'login', description: 'User logged in' };
    const createdActivity = { id: 1, ...newActivity };
    mockAxiosInstance.post.mockResolvedValue({ data: createdActivity });

    const result = await activityTrackApi.createActivity(newActivity);

    expect(mockAxiosInstance.post).toHaveBeenCalledWith('/api/activity-tracks', newActivity);
    expect(result.data).toEqual(createdActivity);
  });

  it('should call updateActivity', async () => {
    const updatedActivity = { id: 1, userId: 1, activityType: 'logout', description: 'User logged out' };
    mockAxiosInstance.put.mockResolvedValue({ data: updatedActivity });

    const result = await activityTrackApi.updateActivity(1, updatedActivity);

    expect(mockAxiosInstance.put).toHaveBeenCalledWith('/api/activity-tracks/1', updatedActivity);
    expect(result.data).toEqual(updatedActivity);
  });

  it('should call deleteActivity', async () => {
    mockAxiosInstance.delete.mockResolvedValue({ data: {} });

    await activityTrackApi.deleteActivity(1);

    expect(mockAxiosInstance.delete).toHaveBeenCalledWith('/api/activity-tracks/1');
  });
});

describe('purchaseHistoryApi', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call getAllPurchases', async () => {
    const mockPurchases = [{ id: 1, userId: 1, orderNumber: 'ORD001' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockPurchases });

    const result = await purchaseHistoryApi.getAllPurchases();

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/purchases');
    expect(result.data).toEqual(mockPurchases);
  });

  it('should call getPurchaseById', async () => {
    const mockPurchase = { id: 1, userId: 1, orderNumber: 'ORD001' };
    mockAxiosInstance.get.mockResolvedValue({ data: mockPurchase });

    const result = await purchaseHistoryApi.getPurchaseById(1);

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/purchases/1');
    expect(result.data).toEqual(mockPurchase);
  });

  it('should call getPurchasesByUserId', async () => {
    const mockPurchases = [{ id: 1, userId: 1, orderNumber: 'ORD001' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockPurchases });

    const result = await purchaseHistoryApi.getPurchasesByUserId(1);

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/purchases/user/1');
    expect(result.data).toEqual(mockPurchases);
  });

  it('should call getPurchaseByOrderNumber', async () => {
    const mockPurchase = { id: 1, userId: 1, orderNumber: 'ORD001' };
    mockAxiosInstance.get.mockResolvedValue({ data: mockPurchase });

    const result = await purchaseHistoryApi.getPurchaseByOrderNumber('ORD001');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/purchases/order/ORD001');
    expect(result.data).toEqual(mockPurchase);
  });

  it('should call getPurchasesByCategory', async () => {
    const mockPurchases = [{ id: 1, userId: 1, category: 'Electronics' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockPurchases });

    const result = await purchaseHistoryApi.getPurchasesByCategory('Electronics');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/purchases/category/Electronics');
    expect(result.data).toEqual(mockPurchases);
  });

  it('should call getPurchasesByBrand', async () => {
    const mockPurchases = [{ id: 1, userId: 1, brand: 'Apple' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockPurchases });

    const result = await purchaseHistoryApi.getPurchasesByBrand('Apple');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/purchases/brand/Apple');
    expect(result.data).toEqual(mockPurchases);
  });

  it('should call getPurchasesByPaymentStatus', async () => {
    const mockPurchases = [{ id: 1, userId: 1, paymentStatus: 'PAID' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockPurchases });

    const result = await purchaseHistoryApi.getPurchasesByPaymentStatus('PAID');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/purchases/payment-status/PAID');
    expect(result.data).toEqual(mockPurchases);
  });

  it('should call getPurchasesByOrderStatus', async () => {
    const mockPurchases = [{ id: 1, userId: 1, orderStatus: 'Completed' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockPurchases });

    const result = await purchaseHistoryApi.getPurchasesByOrderStatus('Completed');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/purchases/order-status/Completed');
    expect(result.data).toEqual(mockPurchases);
  });

  it('should call getPurchasesByTimeRange', async () => {
    const mockPurchases = [{ id: 1, userId: 1, orderNumber: 'ORD001' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockPurchases });

    const result = await purchaseHistoryApi.getPurchasesByTimeRange('2023-01-01', '2023-01-31');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/purchases/time-range?startTime=2023-01-01&endTime=2023-01-31');
    expect(result.data).toEqual(mockPurchases);
  });

  it('should call getPurchasesByUserIdAndTimeRange', async () => {
    const mockPurchases = [{ id: 1, userId: 1, orderNumber: 'ORD001' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockPurchases });

    const result = await purchaseHistoryApi.getPurchasesByUserIdAndTimeRange(1, '2023-01-01', '2023-01-31');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/purchases/user/1/time-range?startTime=2023-01-01&endTime=2023-01-31');
    expect(result.data).toEqual(mockPurchases);
  });

  it('should call searchPurchases', async () => {
    const mockPurchases = [{ id: 1, userId: 1, productName: 'iPhone' }];
    mockAxiosInstance.get.mockResolvedValue({ data: mockPurchases });

    const result = await purchaseHistoryApi.searchPurchases('iPhone');

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/purchases/search?keyword=iPhone');
    expect(result.data).toEqual(mockPurchases);
  });

  it('should call getPurchaseCount', async () => {
    const mockCount = { count: 10 };
    mockAxiosInstance.get.mockResolvedValue({ data: mockCount });

    const result = await purchaseHistoryApi.getPurchaseCount();

    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/users/purchases/count');
    expect(result.data).toEqual(mockCount);
  });

  it('should call createPurchase', async () => {
    const newPurchase = { userId: 1, orderNumber: 'ORD001', productName: 'iPhone', quantity: 1, unitPrice: 999, totalPrice: 999 };
    const createdPurchase = { id: 1, ...newPurchase };
    mockAxiosInstance.post.mockResolvedValue({ data: createdPurchase });

    const result = await purchaseHistoryApi.createPurchase(newPurchase);

    expect(mockAxiosInstance.post).toHaveBeenCalledWith('/api/users/purchases', newPurchase);
    expect(result.data).toEqual(createdPurchase);
  });

  it('should call createPurchases (batch)', async () => {
    const newPurchases = [{ userId: 1, orderNumber: 'ORD001', productName: 'iPhone', quantity: 1, unitPrice: 999, totalPrice: 999 }];
    const createdPurchases = [{ id: 1, ...newPurchases[0] }];
    mockAxiosInstance.post.mockResolvedValue({ data: createdPurchases });

    const result = await purchaseHistoryApi.createPurchases(newPurchases);

    expect(mockAxiosInstance.post).toHaveBeenCalledWith('/api/users/purchases/batch', newPurchases);
    expect(result.data).toEqual(createdPurchases);
  });

  it('should call updatePurchase', async () => {
    const updatedPurchase = { id: 1, userId: 1, orderNumber: 'ORD001', productName: 'iPhone 15', quantity: 1, unitPrice: 1099, totalPrice: 1099 };
    mockAxiosInstance.put.mockResolvedValue({ data: updatedPurchase });

    const result = await purchaseHistoryApi.updatePurchase(1, updatedPurchase);

    expect(mockAxiosInstance.put).toHaveBeenCalledWith('/api/users/purchases/1', updatedPurchase);
    expect(result.data).toEqual(updatedPurchase);
  });

  it('should call deletePurchase', async () => {
    mockAxiosInstance.delete.mockResolvedValue({ data: {} });

    await purchaseHistoryApi.deletePurchase(1);

    expect(mockAxiosInstance.delete).toHaveBeenCalledWith('/api/users/purchases/1');
  });
});

describe('Error Handling', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should handle network errors', async () => {
    const networkError = new Error('Network Error');
    mockAxiosInstance.get.mockRejectedValue(networkError);

    await expect(userApi.getAllUsers()).rejects.toThrow('Network Error');
  });

  it('should handle HTTP errors', async () => {
    const httpError = {
      response: {
        status: 404,
        data: { message: 'User not found' }
      }
    };
    mockAxiosInstance.get.mockRejectedValue(httpError);

    await expect(userApi.getUserById(999)).rejects.toEqual(httpError);
  });
});