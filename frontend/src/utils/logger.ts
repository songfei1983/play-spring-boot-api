// 日志工具类
type LogLevel = 'debug' | 'info' | 'warn' | 'error';

class Logger {
  private logLevel: LogLevel;

  constructor() {
    // 从环境变量获取日志级别，默认为info
    this.logLevel = (process.env.REACT_APP_LOG_LEVEL as LogLevel) || 'info';
  }

  private shouldLog(level: LogLevel): boolean {
    const levels: LogLevel[] = ['debug', 'info', 'warn', 'error'];
    const currentLevelIndex = levels.indexOf(this.logLevel);
    const targetLevelIndex = levels.indexOf(level);
    return targetLevelIndex >= currentLevelIndex;
  }

  debug(...args: any[]): void {
    if (this.shouldLog('debug')) {
      console.debug('[DEBUG]', ...args);
    }
  }

  info(...args: any[]): void {
    if (this.shouldLog('info')) {
      console.info('[INFO]', ...args);
    }
  }

  warn(...args: any[]): void {
    if (this.shouldLog('warn')) {
      console.warn('[WARN]', ...args);
    }
  }

  error(...args: any[]): void {
    if (this.shouldLog('error')) {
      console.error('[ERROR]', ...args);
    }
  }

  // 用于API调用的详细日志
  apiCall(method: string, url: string, data?: any): void {
    this.debug(`API ${method.toUpperCase()} ${url}`, data ? { data } : '');
  }

  // 用于API响应的详细日志
  apiResponse(method: string, url: string, response: any): void {
    this.debug(`API ${method.toUpperCase()} ${url} Response:`, response);
  }

  // 用于API错误的详细日志
  apiError(method: string, url: string, error: any): void {
    this.error(`API ${method.toUpperCase()} ${url} Error:`, error);
  }
}

// 导出单例实例
export const logger = new Logger();
export default logger;