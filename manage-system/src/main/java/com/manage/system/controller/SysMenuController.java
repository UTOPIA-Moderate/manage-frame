package com.manage.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.manage.common.entity.SysMenu;
import com.manage.common.result.PageResult;
import com.manage.common.result.Result;
import com.manage.system.dto.SysMenuDTO;
import com.manage.system.service.SysMenuService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/menu-management")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService menuService;

    @GetMapping
    public Result<PageResult<SysMenu>> getMenuList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String menu_type,
            @RequestParam(required = false) Integer status) {
        IPage<SysMenu> pageData = menuService.getMenuPage(page, pageSize, name, menu_type, status);
        return Result.ok(PageResult.of(
                (int) pageData.getCurrent(),
                (int) pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        ));
    }

    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> getMenuTree() {
        return Result.ok(menuService.getMenuTreeStructured());
    }

    @GetMapping("/{menuId}")
    public Result<SysMenu> getMenuDetail(@PathVariable String menuId) {
        return Result.ok(menuService.getMenuById(menuId));
    }

    @PostMapping
    public Result<Map<String, String>> createMenu(@RequestBody SysMenuDTO dto, HttpServletRequest request) {
        String operatorId = getOperatorId(request);
        String menuId = menuService.createMenu(dto, operatorId);
        Map<String, String> result = new HashMap<>();
        result.put("menu_id", menuId);
        return Result.ok("创建成功！", result);
    }

    @PutMapping("/{menuId}")
    public Result<Integer> updateMenu(@PathVariable String menuId, @RequestBody SysMenuDTO dto) {
        dto.setMenuId(menuId);
        menuService.updateMenu(dto);
        return Result.ok("更新成功！", 1);
    }

    @DeleteMapping("/{menuId}")
    public Result<Integer> deleteMenu(@PathVariable String menuId) {
        menuService.deleteMenu(menuId);
        return Result.ok("删除成功！", 1);
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
