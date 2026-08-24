package com.course.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.course.common.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简易 Token 鉴权（教学演示）。
 * 生产环境请改为 JWT + Redis，并配合 HTTPS、刷新令牌与权限模型。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /** Token -> 管理员 ID */
    public static final Map<String, Long> TOKEN_STORE = new ConcurrentHashMap<>();
    /** Token -> 过期时间戳 */
    public static final Map<String, Long> TOKEN_EXPIRE = new ConcurrentHashMap<>();
    /** 默认 12 小时 */
    public static final long TOKEN_TTL_MS = 12L * 60 * 60 * 1000;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        cleanupExpired();
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.isEmpty() || !TOKEN_STORE.containsKey(token)) {
            writeUnauthorized(response);
            return false;
        }
        Long expireAt = TOKEN_EXPIRE.get(token);
        if (expireAt != null && expireAt < System.currentTimeMillis()) {
            TOKEN_STORE.remove(token);
            TOKEN_EXPIRE.remove(token);
            writeUnauthorized(response);
            return false;
        }
        return true;
    }

    private void cleanupExpired() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> it = TOKEN_EXPIRE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> e = it.next();
            if (e.getValue() < now) {
                it.remove();
                TOKEN_STORE.remove(e.getKey());
            }
        }
    }

    private void writeUnauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(401, "未登录或登录已过期")));
    }
}
