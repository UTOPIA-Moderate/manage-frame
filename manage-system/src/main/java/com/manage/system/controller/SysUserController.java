package com.manage.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manage.common.entity.SysUser;
import com.manage.common.result.PageResult;
import com.manage.common.result.Result;
import com.manage.system.dto.SysUserDTO;
import com.manage.system.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/system/user-management")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    @GetMapping
    public Result<PageResult<SysUser>> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String user_name,
            @RequestParam(required = false) String cn_name,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String jobs_id,
            @RequestParam(required = false) String org_id) {
        IPage<SysUser> pageData = userService.getUserPage(page, pageSize, user_name, cn_name, status, jobs_id, org_id);
        return Result.ok(PageResult.of(
                (int) pageData.getCurrent(),
                (int) pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        ));
    }

    @GetMapping("/{userId}")
    public Result<SysUser> getUserDetail(@PathVariable String userId) {
        return Result.ok(userService.getUserById(userId));
    }

    @PostMapping
    public Result<Map<String, String>> createUser(@RequestBody SysUserDTO dto, HttpServletRequest request) {
        String operatorId = getOperatorId(request);
        String userId = userService.createUser(dto, operatorId);
        Map<String, String> result = new HashMap<>();
        result.put("user_id", userId);
        return Result.ok("创建成功！", result);
    }

    @PutMapping("/{userId}")
    public Result<Integer> updateUser(@PathVariable String userId, @RequestBody SysUserDTO dto) {
        dto.setUserId(userId);
        userService.updateUser(dto);
        return Result.ok("更新成功！", 1);
    }

    @DeleteMapping("/{userId}")
    public Result<Integer> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return Result.ok("删除成功！", 1);
    }

    @PatchMapping("/{userId}")
    public Result<Integer> updateUserStatus(@PathVariable String userId, @RequestBody Map<String, Integer> body) {
        userService.updateUserStatus(userId, body.get("status"));
        return Result.ok("更新成功！", 1);
    }

    @PutMapping("/password/reset")
    public Result<Integer> resetPassword(@RequestBody Map<String, String> body) {
        userService.resetPassword(body.get("user_id"), body.get("password"));
        return Result.ok("密码已重置！", 1);
    }

    private String getOperatorId(HttpServletRequest request) {
        String token = request.getHeader("X-User-Token");
        if (token == null) token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        if (token != null) {
            try {
                return com.manage.common.util.SecurityUtils.getUserIdFromToken(token);
            } catch (Exception ignored) {}
        }
        return "system";
    }
}
