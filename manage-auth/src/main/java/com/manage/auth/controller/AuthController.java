package com.manage.auth.controller;

import com.manage.auth.dto.LoginRequest;
import com.manage.auth.dto.LoginResponse;
import com.manage.auth.dto.UserInfoResponse;
import com.manage.auth.service.AuthService;
import com.manage.common.result.Result;
import com.manage.common.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        Map<String, Object> data = authService.login(
                request.getType(),
                request.getUserName(),
                request.getPassword(),
                request.getPhone(),
                request.getVerifyCode()
        );
        LoginResponse resp = new LoginResponse();
        resp.setAccessToken((String) data.get("access_token"));
        resp.setLoginLastTime((String) data.get("login_last_time"));
        return Result.ok(resp);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("X-User-Token");
        if (token == null) {
            token = request.getHeader("Authorization");
        }
        authService.logout(token);
        return Result.ok("退出成功！");
    }

    @GetMapping("/user-info")
    public Result<UserInfoResponse> getUserInfo(HttpServletRequest request) {
        String token = getToken(request);
        UserInfoResponse userInfo = authService.getUserInfo(token);
        return Result.ok(userInfo);
    }

    @GetMapping("/permissions")
    public Result<List<String>> getPermissions(HttpServletRequest request) {
        String token = getToken(request);
        List<String> permissions = authService.getPermissions(token);
        return Result.ok(permissions);
    }

    @GetMapping("/routes-menu")
    public Result<List<Map<String, Object>>> getRoutesMenu(HttpServletRequest request) {
        String token = getToken(request);
        List<Map<String, Object>> routes = authService.getRoutesMenu(token);
        return Result.ok(routes);
    }

    @GetMapping("/verify-code")
    public Result<String> getVerifyCode() {
        return Result.ok(authService.getVerifyCode());
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("X-User-Token");
        if (token == null) {
            token = request.getHeader("Authorization");
        }
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token;
    }
}
