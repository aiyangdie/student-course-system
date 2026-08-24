package com.course.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.course.common.Result;
import com.course.entity.SysAdmin;
import com.course.interceptor.AuthInterceptor;
import com.course.mapper.SysAdminMapper;
import lombok.Data;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SysAdminMapper sysAdminMapper;

    public AuthController(SysAdminMapper sysAdminMapper) {
        this.sysAdminMapper = sysAdminMapper;
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginRequest req) {
        if (!StringUtils.hasText(req.getUsername()) || !StringUtils.hasText(req.getPassword())) {
            return Result.fail("用户名和密码不能为空");
        }
        SysAdmin admin = sysAdminMapper.selectOne(new LambdaQueryWrapper<SysAdmin>()
                .eq(SysAdmin::getUsername, req.getUsername().trim()));
        if (admin == null || !matchPassword(req.getPassword(), admin.getPassword())) {
            return Result.fail("用户名或密码错误");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        AuthInterceptor.TOKEN_STORE.put(token, admin.getId());
        AuthInterceptor.TOKEN_EXPIRE.put(token, System.currentTimeMillis() + AuthInterceptor.TOKEN_TTL_MS);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", admin.getUsername());
        data.put("realName", admin.getRealName());
        return Result.ok(data);
    }

    @GetMapping("/info")
    public Result<?> info(@RequestHeader(value = "Authorization", required = false) String auth) {
        String token = extractToken(auth);
        Long adminId = AuthInterceptor.TOKEN_STORE.get(token);
        if (adminId == null) {
            return Result.fail(401, "未登录");
        }
        SysAdmin admin = sysAdminMapper.selectById(adminId);
        if (admin == null) {
            return Result.fail(401, "未登录");
        }
        admin.setPassword(null);
        return Result.ok(admin);
    }

    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader(value = "Authorization", required = false) String auth) {
        String token = extractToken(auth);
        if (token != null) {
            AuthInterceptor.TOKEN_STORE.remove(token);
            AuthInterceptor.TOKEN_EXPIRE.remove(token);
        }
        return Result.ok();
    }

    private boolean matchPassword(String raw, String stored) {
        if (!StringUtils.hasText(stored)) {
            return false;
        }
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            try {
                return BCrypt.checkpw(raw, stored);
            } catch (Exception e) {
                return false;
            }
        }
        // 兼容极旧明文数据；开源默认脚本已改为 BCrypt
        return stored.equals(raw);
    }

    private String extractToken(String auth) {
        if (auth == null) return null;
        if (auth.startsWith("Bearer ")) return auth.substring(7);
        return auth;
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
