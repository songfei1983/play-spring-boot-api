import { logger } from './logger';

// Mock console methods
const mockConsole = {
  debug: jest.fn(),
  info: jest.fn(),
  warn: jest.fn(),
  error: jest.fn(),
};

// Store original console methods
const originalConsole = {
  debug: console.debug,
  info: console.info,
  warn: console.warn,
  error: console.error,
};

// Mock process.env
const originalEnv = process.env;

describe('Logger (utils)', () => {
  beforeEach(() => {
    // Replace console methods with mocks
    console.debug = mockConsole.debug;
    console.info = mockConsole.info;
    console.warn = mockConsole.warn;
    console.error = mockConsole.error;
    
    // Clear all mocks
    jest.clearAllMocks();
  });

  afterEach(() => {
    // Restore original console methods
    console.debug = originalConsole.debug;
    console.info = originalConsole.info;
    console.warn = originalConsole.warn;
    console.error = originalConsole.error;
    
    // Restore original environment
    process.env = originalEnv;
  });

  describe('Log Levels', () => {
    it('should use info as default log level', () => {
      // Test with default log level (info)
      logger.debug('debug message');
      logger.info('info message');
      logger.warn('warn message');
      logger.error('error message');

      expect(mockConsole.debug).not.toHaveBeenCalled();
      expect(mockConsole.info).toHaveBeenCalledWith('[INFO]', 'info message');
      expect(mockConsole.warn).toHaveBeenCalledWith('[WARN]', 'warn message');
      expect(mockConsole.error).toHaveBeenCalledWith('[ERROR]', 'error message');
    });
  });

  describe('Debug Logging', () => {
    it('should log debug messages when log level is debug', () => {
      // Mock environment variable for debug level
      process.env.REACT_APP_LOG_LEVEL = 'debug';
      
      // Create new logger instance to pick up env change
      const { logger: debugLogger } = require('./logger');
      
      debugLogger.debug('debug message', { data: 'test' });
      
      expect(mockConsole.debug).toHaveBeenCalledWith('[DEBUG]', 'debug message', { data: 'test' });
    });

    it('should not log debug messages when log level is info or higher', () => {
      logger.debug('debug message');
      
      expect(mockConsole.debug).not.toHaveBeenCalled();
    });
  });

  describe('Info Logging', () => {
    it('should log info messages', () => {
      logger.info('info message', { data: 'test' });
      
      expect(mockConsole.info).toHaveBeenCalledWith('[INFO]', 'info message', { data: 'test' });
    });

    it('should log info messages with multiple arguments', () => {
      logger.info('info', 'message', 123, { key: 'value' });
      
      expect(mockConsole.info).toHaveBeenCalledWith('[INFO]', 'info', 'message', 123, { key: 'value' });
    });
  });

  describe('Warn Logging', () => {
    it('should log warn messages', () => {
      logger.warn('warning message', { error: 'details' });
      
      expect(mockConsole.warn).toHaveBeenCalledWith('[WARN]', 'warning message', { error: 'details' });
    });
  });

  describe('Error Logging', () => {
    it('should log error messages', () => {
      const error = new Error('Test error');
      logger.error('error message', error);
      
      expect(mockConsole.error).toHaveBeenCalledWith('[ERROR]', 'error message', error);
    });

    it('should always log error messages regardless of log level', () => {
      // Even with a hypothetical higher log level, errors should still be logged
      logger.error('critical error');
      
      expect(mockConsole.error).toHaveBeenCalledWith('[ERROR]', 'critical error');
    });
  });

  describe('API Logging Methods', () => {
    it('should log API calls with data', () => {
      // Mock debug level to see API call logs
      process.env.REACT_APP_LOG_LEVEL = 'debug';
      const { logger: debugLogger } = require('./logger');
      
      const requestData = { userId: 1, name: 'test' };
      debugLogger.apiCall('post', '/api/users', requestData);
      
      expect(mockConsole.debug).toHaveBeenCalledWith(
        'API POST /api/users',
        { data: requestData }
      );
    });

    it('should log API calls without data', () => {
      process.env.REACT_APP_LOG_LEVEL = 'debug';
      const { logger: debugLogger } = require('./logger');
      
      debugLogger.apiCall('get', '/api/users');
      
      expect(mockConsole.debug).toHaveBeenCalledWith(
        'API GET /api/users',
        ''
      );
    });

    it('should log API responses', () => {
      process.env.REACT_APP_LOG_LEVEL = 'debug';
      const { logger: debugLogger } = require('./logger');
      
      const responseData = { id: 1, name: 'John' };
      debugLogger.apiResponse('get', '/api/users/1', responseData);
      
      expect(mockConsole.debug).toHaveBeenCalledWith(
        'API GET /api/users/1 Response:',
        responseData
      );
    });

    it('should log API errors', () => {
      const error = new Error('Network error');
      logger.apiError('post', '/api/users', error);
      
      expect(mockConsole.error).toHaveBeenCalledWith(
        'API POST /api/users Error:',
        error
      );
    });

    it('should handle method case conversion in API logs', () => {
      process.env.REACT_APP_LOG_LEVEL = 'debug';
      const { logger: debugLogger } = require('./logger');
      
      debugLogger.apiCall('patch', '/api/users/1');
      debugLogger.apiResponse('delete', '/api/users/1', {});
      debugLogger.apiError('put', '/api/users/1', new Error('test'));
      
      expect(mockConsole.debug).toHaveBeenCalledWith('API PATCH /api/users/1', '');
      expect(mockConsole.debug).toHaveBeenCalledWith('API DELETE /api/users/1 Response:', {});
      expect(mockConsole.error).toHaveBeenCalledWith('API PUT /api/users/1 Error:', expect.any(Error));
    });
  });

  describe('Log Level Hierarchy', () => {
    it('should respect log level hierarchy for warn level', () => {
      process.env.REACT_APP_LOG_LEVEL = 'warn';
      const { logger: warnLogger } = require('./logger');
      
      warnLogger.debug('debug message');
      warnLogger.info('info message');
      warnLogger.warn('warn message');
      warnLogger.error('error message');
      
      expect(mockConsole.debug).not.toHaveBeenCalled();
      expect(mockConsole.info).not.toHaveBeenCalled();
      expect(mockConsole.warn).toHaveBeenCalledWith('[WARN]', 'warn message');
      expect(mockConsole.error).toHaveBeenCalledWith('[ERROR]', 'error message');
    });

    it('should respect log level hierarchy for error level', () => {
      process.env.REACT_APP_LOG_LEVEL = 'error';
      const { logger: errorLogger } = require('./logger');
      
      errorLogger.debug('debug message');
      errorLogger.info('info message');
      errorLogger.warn('warn message');
      errorLogger.error('error message');
      
      expect(mockConsole.debug).not.toHaveBeenCalled();
      expect(mockConsole.info).not.toHaveBeenCalled();
      expect(mockConsole.warn).not.toHaveBeenCalled();
      expect(mockConsole.error).toHaveBeenCalledWith('[ERROR]', 'error message');
    });
  });

  describe('Complex Data Logging', () => {
    it('should handle complex objects in log messages', () => {
      const complexObject = {
        user: { id: 1, name: 'John' },
        metadata: { timestamp: Date.now(), version: '1.0' },
        array: [1, 2, 3],
      };
      
      logger.info('Complex data:', complexObject);
      
      expect(mockConsole.info).toHaveBeenCalledWith('[INFO]', 'Complex data:', complexObject);
    });

    it('should handle null and undefined values', () => {
      logger.info('Null value:', null);
      logger.warn('Undefined value:', undefined);
      
      expect(mockConsole.info).toHaveBeenCalledWith('[INFO]', 'Null value:', null);
      expect(mockConsole.warn).toHaveBeenCalledWith('[WARN]', 'Undefined value:', undefined);
    });
  });

  describe('Singleton Behavior', () => {
    it('should export the same logger instance', () => {
      const { logger: logger1 } = require('./logger');
      const { logger: logger2 } = require('./logger');
      
      expect(logger1).toBe(logger2);
    });

    it('should export logger as default export', () => {
      const defaultLogger = require('./logger').default;
      
      expect(defaultLogger).toBe(logger);
    });
  });

  describe('Environment Variable Handling', () => {
    it('should handle invalid log level gracefully', () => {
      process.env.REACT_APP_LOG_LEVEL = 'invalid' as any;
      
      // This should not throw an error and should fall back to default behavior
      expect(() => {
        const { logger: invalidLogger } = require('./logger');
        invalidLogger.info('test message');
      }).not.toThrow();
    });

    it('should handle missing environment variable', () => {
      delete process.env.REACT_APP_LOG_LEVEL;
      
      expect(() => {
        const { logger: noEnvLogger } = require('./logger');
        noEnvLogger.info('test message');
      }).not.toThrow();
    });
  });
});