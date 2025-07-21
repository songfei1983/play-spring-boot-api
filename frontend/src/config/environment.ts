// 环境配置
export const config = {
  // 是否使用Mock API (开发环境默认使用Mock，生产环境使用真实API)
  useMockApi: process.env.NODE_ENV === 'development' || process.env.REACT_APP_USE_MOCK === 'true',
  
  // API基础URL
  apiBaseUrl: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080',
  
  // 其他配置
  enableLogging: process.env.NODE_ENV === 'development',
  
  // Mock API配置
  mockApiDelay: 500, // Mock API响应延迟(毫秒)
};

// 日志工具
export const logger = {
  info: (message: string, ...args: any[]) => {
    if (config.enableLogging) {
      console.log(`[INFO] ${message}`, ...args);
    }
  },
  
  warn: (message: string, ...args: any[]) => {
    if (config.enableLogging) {
      console.warn(`[WARN] ${message}`, ...args);
    }
  },
  
  error: (message: string, ...args: any[]) => {
    if (config.enableLogging) {
      console.error(`[ERROR] ${message}`, ...args);
    }
  },
  
  apiResponse: (method: string, url: string, data: any) => {
    if (config.enableLogging) {
      console.log(`[API Response] ${method.toUpperCase()} ${url}`, data);
    }
  },
  
  apiError: (method: string, url: string, error: any) => {
    if (config.enableLogging) {
      console.error(`[API Error] ${method.toUpperCase()} ${url}`, error);
    }
  },
  
  apiCall: (method: string, url: string, data: any) => {
    if (config.enableLogging) {
      console.log(`[API Call] ${method.toUpperCase()} ${url}`, data);
    }
  },
  
  debug: (message: string, ...args: any[]) => {
    if (config.enableLogging) {
      console.debug(`[DEBUG] ${message}`, ...args);
    }
  }
};

export default config;