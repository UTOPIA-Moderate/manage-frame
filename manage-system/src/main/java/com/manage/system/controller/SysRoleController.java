package com.manage.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.manage.common.entity.SysRole;
import com.manage.common.result.PageResult;
import com.manage.common.result.Result;
import com.manage.system.dto.SysRoleDTO;
import com.manage.system.service.SysRoleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/role-management")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService roleService;

    @GetMapping
    public Result<PageResult<SysRole>> getRoleList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String role_name,
            @RequestParam(required = false) Integer status) {
        IPage<SysRole> pageData = roleService.getRolePage(page, pageSize, role_name, status);
        return Result.ok(PageResult.of(
                (int) pageData.getCurrent(),
                (int) pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        ));
    }

    @GetMapping("/{roleId}")
    public Result<SysRole> getRoleDetail(@PathVariable String roleId) {
        return Result.ok(roleService.getRoleById(roleId));
    }

    @GetMapping("/all")
    public Result<List<SysRole>> getAllRoles() {
        return Result.ok(roleService.getAllRoles());
    }

    @PostMapping
    public Result<Map<String, String>> createRole(@RequestBody SysRoleDTO dto, HttpServletRequest request) {
        String operatorId = getOperatorId(request);
        String roleId = roleService.createRole(dto, operatorId);
        Map<String, String> result = new HashMap<>();
        result.put("role_id", roleId);
        return Result.ok("创建成功！", result);
    }

    @PutMapping("/{roleId}")
    public Result<Integer> updateRole(@PathVariable String roleId, @RequestBody SysRoleDTO dto) {
        dto.setRoleId(roleId);
        roleService.updateRole(dto);
        return Result.ok("更新成功！", 1);
    }

    @DeleteMapping("/{roleId}")
    public Result<Integer> deleteRole(@PathVariable String roleId) {
        roleService.deleteRole(roleId);
        return Result.ok("删除成功！", 1);
    }

    @PatchMapping("/{roleId}")
    public Result<Integer> updateRoleStatus(@PathVariable String roleId, @RequestBody Map<String, Integer> body) {
        roleService.updateRoleStatus(roleId, body.get("status"));
        return Result.ok("更新成功！", 1);
    }

    @PutMapping("/menu")
    public Result<Integer> assignMenu(@RequestBody Map<String, Object> body) {
        String roleId = (String) body.get("role_id");
        List<Map<String, String>> menuPermissions = (List<Map<String, String>>) body.get("menu_permission");
        List<SysRoleDTO.MenuPermission> permissions = menuPermissions.stream()
                .map(mp -> {
                    SysRoleDTO.MenuPermission p = new SysRoleDTO.MenuPermission();
                    p.setMenuId(mp.get("menu_id"));
                    p.setPermission(mp.get("permission"));
                    p.setMenuType(mp.get("menu_type"));
                    return p;
                }).toList();
        roleService.assignMenu(roleId, permissions);
        return Result.ok("分配成功！", 1);
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
