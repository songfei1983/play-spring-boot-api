package fei.song.play_spring_boot_api.ads.infrastructure.util;

import com.mongodb.MongoException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.MongoWriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * MongoDB异常处理工具类
 * 提供统一的MongoDB异常处理和日志记录
 */
@Component
public class MongoExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(MongoExceptionHandler.class);
    
    /**
     * 处理MongoDB异常
     * @param operation 操作名称
     * @param exception 异常对象
     * @return 是否可以重试
     */
    public boolean handleException(String operation, Exception exception) {
        if (exception instanceof MongoTimeoutException) {
            logger.warn("MongoDB操作超时: {} - {}", operation, exception.getMessage());
            return true; // 超时异常可以重试
        } else if (exception instanceof MongoWriteException) {
            MongoWriteException writeException = (MongoWriteException) exception;
            logger.error("MongoDB写入异常: {} - 错误代码: {}, 错误信息: {}", 
                    operation, writeException.getCode(), writeException.getMessage());
            return false; // 写入异常通常不应重试
        } else if (exception instanceof MongoException) {
            logger.error("MongoDB操作异常: {} - {}", operation, exception.getMessage());
            return isRetryableException((MongoException) exception);
        } else {
            logger.error("未知异常: {} - {}", operation, exception.getMessage(), exception);
            return false;
        }
    }
    
    /**
     * 判断MongoDB异常是否可以重试
     * @param exception MongoDB异常
     * @return 是否可以重试
     */
    private boolean isRetryableException(MongoException exception) {
        int errorCode = exception.getCode();
        
        // 网络相关错误码，通常可以重试
        return errorCode == 11600 || // InterruptedAtShutdown
               errorCode == 11601 || // Interrupted
               errorCode == 89 ||    // NetworkTimeout
               errorCode == 7 ||     // HostUnreachable
               errorCode == 6;       // HostNotFound
    }
    
    /**
     * 执行带异常处理的MongoDB操作
     * @param operation 操作名称
     * @param mongoOperation MongoDB操作
     * @param <T> 返回类型
     * @return 操作结果
     * @throws RuntimeException 当操作失败且不可重试时抛出
     */
    public <T> T executeWithExceptionHandling(String operation, MongoOperation<T> mongoOperation) {
        try {
            return mongoOperation.execute();
        } catch (Exception e) {
            boolean canRetry = handleException(operation, e);
            if (canRetry) {
                logger.info("尝试重新执行MongoDB操作: {}", operation);
                try {
                    Thread.sleep(1000); // 简单的延迟重试
                    return mongoOperation.execute();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("操作被中断: " + operation, ie);
                } catch (Exception retryException) {
                    handleException(operation + "(重试)", retryException);
                    throw new RuntimeException("MongoDB操作失败: " + operation, retryException);
                }
            } else {
                throw new RuntimeException("MongoDB操作失败: " + operation, e);
            }
        }
    }
    
    /**
     * MongoDB操作函数式接口
     * @param <T> 返回类型
     */
    @FunctionalInterface
    public interface MongoOperation<T> {
        T execute() throws Exception;
    }
}