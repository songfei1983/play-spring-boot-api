package fei.song.play_spring_boot_api.users.infrastructure;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import fei.song.play_spring_boot_api.users.domain.User;
import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class UserAccessLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(UserAccessLogAspect.class);

    @Around("execution(* fei.song.play_spring_boot_api.users.interfaces.UserController.*(..))")
    public Object logUserAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // 获取或生成 Request ID
        String requestId = getOrGenerateRequestId();

        // 确定操作类型
        String operationType = getOperationType(methodName);

        // 记录请求开始
        logger.info("[用户访问日志] RequestID: {}, 操作类型: {}, 开始处理", requestId, operationType);

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 记录成功日志
            logSuccess(requestId, methodName, args, result, duration);

            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 记录失败日志
            logger.error("[用户访问日志] RequestID: {}, 操作类型: {}, 耗时: {}ms, 状态: 失败, 错误: {}",
                    requestId, operationType, duration, e.getMessage());

            throw e;
        }
    }

    /**
     * 获取或生成 Request ID
     * 优先从 HTTP 请求头中获取 nginx 传递的 X-Request-ID
     * 如果没有则生成一个类似 nginx 格式的 Request ID
     */
    private String getOrGenerateRequestId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // 尝试从常见的请求头中获取 Request ID
                String requestId = request.getHeader("X-Request-ID");
                if (requestId == null || requestId.trim().isEmpty()) {
                    requestId = request.getHeader("X-Request-Id");
                }
                if (requestId == null || requestId.trim().isEmpty()) {
                    requestId = request.getHeader("Request-ID");
                }
                if (requestId == null || requestId.trim().isEmpty()) {
                    requestId = request.getHeader("Request-Id");
                }

                if (requestId != null && !requestId.trim().isEmpty()) {
                    return requestId.trim();
                }
            }
        } catch (Exception e) {
            // 如果获取请求上下文失败，继续生成新的 ID
        }

        // 生成类似 nginx 格式的 Request ID
        return generateNginxStyleRequestId();
    }

    /**
     * 生成类似 nginx 格式的 Request ID
     * nginx 通常使用 32 位十六进制字符串作为 Request ID
     */
    private String generateNginxStyleRequestId() {
        // 生成 32 位十六进制字符串，类似 nginx 的 $request_id
        StringBuilder sb = new StringBuilder(32);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < 32; i++) {
            sb.append(Integer.toHexString(random.nextInt(16)));
        }

        return sb.toString();
    }

    private String getOperationType(String methodName) {
        switch (methodName) {
            case "getAllUsers":
                return "查询所有用户";
            case "getUserById":
                return "查询单个用户";
            case "createUser":
                return "创建用户";
            case "updateUser":
                return "更新用户";
            case "deleteUser":
                return "删除用户";
            default:
                return "未知操作";
        }
    }

    private void logSuccess(String requestId, String methodName, Object[] args, Object result, long duration) {
        switch (methodName) {
            case "getAllUsers":
                if (result instanceof List) {
                    List<?> users = (List<?>) result;
                    logger.info("[用户访问日志] RequestID: {}, 操作类型: 查询所有用户, 返回用户数量: {}, 耗时: {}ms, 状态: 成功",
                            requestId, users.size(), duration);
                }
                break;
            case "getUserById":
                if (result instanceof User) {
                    User user = (User) result;
                    logger.info("[用户访问日志] RequestID: {}, 用户ID: {}, 用户名: {}, 操作类型: 查询单个用户, 耗时: {}ms, 状态: 成功",
                            requestId, user.getId(), user.getName(), duration);
                }
                break;
            case "createUser":
                if (result instanceof User) {
                    User user = (User) result;
                    logger.info("[用户访问日志] RequestID: {}, 用户ID: {}, 用户名: {}, 操作类型: 创建用户, 耗时: {}ms, 状态: 成功",
                            requestId, user.getId(), user.getName(), duration);
                }
                break;
            case "updateUser":
                if (result instanceof User) {
                    User user = (User) result;
                    logger.info("[用户访问日志] RequestID: {}, 用户ID: {}, 用户名: {}, 操作类型: 更新用户, 耗时: {}ms, 状态: 成功",
                            requestId, user.getId(), user.getName(), duration);
                }
                break;
            case "deleteUser":
                if (args.length > 0) {
                    logger.info("[用户访问日志] RequestID: {}, 用户ID: {}, 操作类型: 删除用户, 耗时: {}ms, 状态: 成功",
                            requestId, args[0], duration);
                }
                break;
            default:
                logger.info("[用户访问日志] RequestID: {}, 操作类型: 未知操作, 耗时: {}ms, 状态: 成功",
                        requestId, duration);
        }
    }
}