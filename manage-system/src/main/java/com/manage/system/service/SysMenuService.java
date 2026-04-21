package com.manage.system.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manage.common.entity.SysMenu;
import com.manage.common.exception.BaseException;
import com.manage.common.result.ResultCode;
import com.manage.common.util.SecurityUtils;
import com.manage.system.dto.SysMenuDTO;
import com.manage.common.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysMenuService extends ServiceImpl<SysMenuMapper, SysMenu> {

    public IPage<SysMenu> getMenuPage(int page, int pageSize, String name, String menuType, Integer status) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(name)) wrapper.like(SysMenu::getName, name);
        if (StrUtil.isNotBlank(menuType)) wrapper.eq(SysMenu::getMenuType, menuType);
        if (status != null) wrapper.eq(SysMenu::getStatus, status);
        wrapper.orderByAsc(SysMenu::getSort).orderByDesc(SysMenu::getCreatedTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    public SysMenu getMenuById(String menuId) {
        SysMenu menu = getById(menuId);
        if (menu == null) {
            throw new BaseException(ResultCode.NOT_FOUND.getCode(), "菜单不存在");
        }
        return menu;
    }

    public List<SysMenu> getMenuTree() {
        List<SysMenu> all = list(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getStatus, 1)
                .orderByAsc(SysMenu::getSort));
        return buildTree(all, null);
    }

    private List<SysMenu> buildTree(List<SysMenu> all, String parentId) {
        List<SysMenu> children = new ArrayList<>();
        for (SysMenu menu : all) {
            boolean match = (parentId == null && menu.getParentId() == null)
                    || (parentId != null && parentId.equals(menu.getParentId()));
            if (match) {
                List<SysMenu> subChildren = buildTree(all, menu.getId());
                if (!subChildren.isEmpty()) {
                    // 树形结构通过子菜单列表表示
                }
                children.add(menu);
            }
        }
        return children;
    }

    public List<Map<String, Object>> getMenuTreeStructured() {
        List<SysMenu> all = list(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getStatus, 1)
                .orderByAsc(SysMenu::getSort));
        return convertToTree(all, null);
    }

    private List<Map<String, Object>> convertToTree(List<SysMenu> all, String parentId) {
        return all.stream()
                .filter(m -> (parentId == null && m.getParentId() == null) ||
                        (parentId != null && parentId.equals(m.getParentId())))
                .map(m -> {
                    Map<String, Object> node = com.manage.common.util.ConvertUtils.toMap(m);
                    List<Map<String, Object>> children = convertToTree(all, m.getId());
                    node.put("routes", children);
                    return node;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public String createMenu(SysMenuDTO dto, String operatorId) {
        SysMenu menu = new SysMenu();
        menu.setId(SecurityUtils.generateUuid());
        menu.setName(dto.getName());
        menu.setParentId(dto.getParentId());
        menu.setMenuType(dto.getMenuType() != null ? dto.getMenuType() : "menu");
        menu.setPath(dto.getPath());
        menu.setIcon(dto.getIcon());
        menu.setComponent(dto.getComponent());
        menu.setRedirect(dto.getRedirect());
        menu.setTarget(dto.getTarget());
        menu.setPermission(dto.getPermission());
        menu.setLayout(dto.getLayout());
        menu.setNavTheme(dto.getNavTheme());
        menu.setHeaderTheme(dto.getHeaderTheme());
        menu.setHideChildrenInMenu(dto.getHideChildrenInMenu() != null && dto.getHideChildrenInMenu() ? 1 : 0);
        menu.setHideInMenu(dto.getHideInMenu() != null && dto.getHideInMenu() ? 1 : 0);
        menu.setHideInBreadcrumb(dto.getHideInBreadcrumb() != null && dto.getHideInBreadcrumb() ? 1 : 0);
        menu.setHeaderRender(dto.getHeaderRender() != null && dto.getHeaderRender() ? 1 : 1);
        menu.setFlatMenu(dto.getFlatMenu() != null && dto.getFlatMenu() ? 1 : 0);
        menu.setFixedHeader(dto.getFixedHeader() != null && dto.getFixedHeader() ? 1 : 0);
        menu.setFixSiderbar(dto.getFixSiderbar() != null && dto.getFixSiderbar() ? 1 : 0);
        menu.setFooterRender(dto.getFooterRender() != null && dto.getFooterRender() ? 1 : 0);
        menu.setMenuRender(dto.getMenuRender() != null && dto.getMenuRender() ? 1 : 1);
        menu.setSort(dto.getSort() != null ? dto.getSort() : 0);
        menu.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        menu.setFounder(operatorId);
        save(menu);
        return menu.getId();
    }

    @Transactional
    public void updateMenu(SysMenuDTO dto) {
        SysMenu menu = getById(dto.getMenuId());
        if (menu == null) {
            throw new BaseException(ResultCode.NOT_FOUND.getCode(), "菜单不存在");
        }

        if (StrUtil.isNotBlank(dto.getName())) menu.setName(dto.getName());
        if (dto.getParentId() != null) menu.setParentId(dto.getParentId());
        if (StrUtil.isNotBlank(dto.getMenuType())) menu.setMenuType(dto.getMenuType());
        if (dto.getPath() != null) menu.setPath(dto.getPath());
        if (dto.getIcon() != null) menu.setIcon(dto.getIcon());
        if (dto.getComponent() != null) menu.setComponent(dto.getComponent());
        if (dto.getRedirect() != null) menu.setRedirect(dto.getRedirect());
        if (dto.getTarget() != null) menu.setTarget(dto.getTarget());
        if (dto.getPermission() != null) menu.setPermission(dto.getPermission());
        if (dto.getLayout() != null) menu.setLayout(dto.getLayout());
        if (dto.getNavTheme() != null) menu.setNavTheme(dto.getNavTheme());
        if (dto.getHeaderTheme() != null) menu.setHeaderTheme(dto.getHeaderTheme());
        if (dto.getHideChildrenInMenu() != null) menu.setHideChildrenInMenu(dto.getHideChildrenInMenu() ? 1 : 0);
        if (dto.getHideInMenu() != null) menu.setHideInMenu(dto.getHideInMenu() ? 1 : 0);
        if (dto.getHideInBreadcrumb() != null) menu.setHideInBreadcrumb(dto.getHideInBreadcrumb() ? 1 : 0);
        if (dto.getHeaderRender() != null) menu.setHeaderRender(dto.getHeaderRender() ? 1 : 1);
        if (dto.getFlatMenu() != null) menu.setFlatMenu(dto.getFlatMenu() ? 1 : 0);
        if (dto.getFixedHeader() != null) menu.setFixedHeader(dto.getFixedHeader() ? 1 : 0);
        if (dto.getFixSiderbar() != null) menu.setFixSiderbar(dto.getFixSiderbar() ? 1 : 0);
        if (dto.getFooterRender() != null) menu.setFooterRender(dto.getFooterRender() ? 1 : 0);
        if (dto.getMenuRender() != null) menu.setMenuRender(dto.getMenuRender() ? 1 : 1);
        if (dto.getSort() != null) menu.setSort(dto.getSort());
        if (dto.getStatus() != null) menu.setStatus(dto.getStatus());
        updateById(menu);
    }

    @Transactional
    public void deleteMenu(String menuId) {
        // 递归删除子菜单
        List<SysMenu> children = list(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, menuId));
        for (SysMenu child : children) {
            deleteMenu(child.getId());
        }
        removeById(menuId);
    }
}
