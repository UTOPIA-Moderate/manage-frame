package com.manage.system.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manage.common.entity.SysRole;
import com.manage.common.entity.SysRoleMenu;
import com.manage.common.exception.BaseException;
import com.manage.common.mapper.SysRoleMenuMapper;
import com.manage.common.result.ResultCode;
import com.manage.common.util.SecurityUtils;
import com.manage.system.dto.SysRoleDTO;
import com.manage.system.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleService extends ServiceImpl<SysRoleMapper, SysRole> {

    private final SysRoleMenuMapper roleMenuMapper;

    public IPage<SysRole> getRolePage(int page, int pageSize, String roleName, Integer status) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(roleName)) wrapper.like(SysRole::getRoleName, roleName);
        if (status != null) wrapper.eq(SysRole::getStatus, status);
        wrapper.orderByDesc(SysRole::getSort).orderByDesc(SysRole::getCreatedTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    public SysRole getRoleById(String roleId) {
        SysRole role = getById(roleId);
        if (role == null) {
            throw new BaseException(ResultCode.NOT_FOUND.getCode(), "角色不存在");
        }
        return role;
    }

    public List<SysRole> getAllRoles() {
        return list(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, 1)
                .orderByAsc(SysRole::getSort));
    }

    @Transactional
    public String createRole(SysRoleDTO dto, String operatorId) {
        SysRole exist = getOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, dto.getRoleCode()));
        if (exist != null) {
            throw new BaseException(ResultCode.BAD_REQUEST.getCode(), "角色编码已存在");
        }

        SysRole role = new SysRole();
        role.setId(SecurityUtils.generateUuid());
        role.setRoleName(dto.getRoleName());
        role.setRoleCode(dto.getRoleCode());
        role.setDescribe(dto.getDescribe());
        role.setSort(dto.getSort() != null ? dto.getSort() : 0);
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        role.setFounder(operatorId);
        save(role);

        // 保存菜单权限
        if (dto.getMenuPermission() != null && !dto.getMenuPermission().isEmpty()) {
            for (SysRoleDTO.MenuPermission mp : dto.getMenuPermission()) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(role.getId());
                rm.setMenuId(mp.getMenuId());
                rm.setPermission(mp.getPermission());
                roleMenuMapper.insert(rm);
            }
        }

        return role.getId();
    }

    @Transactional
    public void updateRole(SysRoleDTO dto) {
        SysRole role = getById(dto.getRoleId());
        if (role == null) {
            throw new BaseException(ResultCode.NOT_FOUND.getCode(), "角色不存在");
        }

        if (StrUtil.isNotBlank(dto.getRoleName())) role.setRoleName(dto.getRoleName());
        if (StrUtil.isNotBlank(dto.getRoleCode())) role.setRoleCode(dto.getRoleCode());
        if (dto.getDescribe() != null) role.setDescribe(dto.getDescribe());
        if (dto.getSort() != null) role.setSort(dto.getSort());
        if (dto.getStatus() != null) role.setStatus(dto.getStatus());
        updateById(role);

        // 更新菜单权限
        if (dto.getMenuPermission() != null) {
            roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, dto.getRoleId()));
            for (SysRoleDTO.MenuPermission mp : dto.getMenuPermission()) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(role.getId());
                rm.setMenuId(mp.getMenuId());
                rm.setPermission(mp.getPermission());
                roleMenuMapper.insert(rm);
            }
        }
    }

    @Transactional
    public void deleteRole(String roleId) {
        removeById(roleId);
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
    }

    @Transactional
    public void updateRoleStatus(String roleId, Integer status) {
        SysRole role = getById(roleId);
        if (role == null) {
            throw new BaseException(ResultCode.NOT_FOUND.getCode(), "角色不存在");
        }
        role.setStatus(status);
        updateById(role);
    }

    @Transactional
    public void assignMenu(String roleId, List<SysRoleDTO.MenuPermission> menuPermissions) {
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (menuPermissions != null) {
            for (SysRoleDTO.MenuPermission mp : menuPermissions) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(mp.getMenuId());
                rm.setPermission(mp.getPermission());
                roleMenuMapper.insert(rm);
            }
        }
    }
}
