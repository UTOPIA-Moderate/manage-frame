package com.manage.system.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manage.common.entity.SysRole;
import com.manage.common.entity.SysUser;
import com.manage.common.entity.SysUserRole;
import com.manage.common.exception.BaseException;
import com.manage.common.mapper.SysRoleMapper;
import com.manage.common.mapper.SysUserRoleMapper;
import com.manage.common.result.ResultCode;
import com.manage.common.util.PasswordEncoder;
import com.manage.common.util.SecurityUtils;
import com.manage.system.dto.SysUserDTO;
import com.manage.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;

    public IPage<SysUser> getUserPage(int page, int pageSize, String userName, String cnName,
                                      Integer status, String jobsId, String orgId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(userName)) wrapper.like(SysUser::getUsername, userName);
        if (StrUtil.isNotBlank(cnName)) wrapper.like(SysUser::getCnName, cnName);
        if (status != null) wrapper.eq(SysUser::getStatus, status);
        if (StrUtil.isNotBlank(jobsId)) wrapper.eq(SysUser::getJobsId, jobsId);
        if (StrUtil.isNotBlank(orgId)) wrapper.eq(SysUser::getOrgId, orgId);
        wrapper.orderByDesc(SysUser::getSort).orderByDesc(SysUser::getCreatedTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    public SysUser getUserById(String userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BaseException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        return user;
    }

    @Transactional
    public String createUser(SysUserDTO dto, String operatorId) {
        // 检查用户名唯一
        SysUser exist = getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUserName()));
        if (exist != null) {
            throw new BaseException(ResultCode.BAD_REQUEST.getCode(), "用户名已存在");
        }

        SysUser user = new SysUser();
        user.setId(SecurityUtils.generateUuid());
        user.setUsername(dto.getUserName());
        user.setPassword(PasswordEncoder.encode(dto.getPassword() != null ? dto.getPassword() : "123456"));
        user.setCnName(dto.getCnName());
        user.setEnName(dto.getEnName());
        user.setAge(dto.getAge() != null ? dto.getAge() : 18);
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setSex(dto.getSex() != null ? dto.getSex() : "1");
        user.setSort(dto.getSort() != null ? dto.getSort() : 99);
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        user.setMotto(dto.getMotto());
        user.setTags(dto.getTags() != null ? JSONUtil.toJsonStr(dto.getTags()) : null);
        user.setCity(dto.getCity() != null ? JSONUtil.toJsonStr(dto.getCity()) : null);
        user.setAddress(dto.getAddress());
        user.setJobsId(dto.getJobsId());
        user.setOrgId(dto.getOrgId());
        user.setRoleId(dto.getRoleId());
        user.setFounder(operatorId);
        user.setLoginNum(0);
        user.setStatus(1);

        save(user);

        // 维护用户角色关联
        if (StrUtil.isNotBlank(dto.getRoleId())) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(user.getId());
            ur.setRoleId(dto.getRoleId());
            userRoleMapper.insert(ur);
        }

        return user.getId();
    }

    @Transactional
    public void updateUser(SysUserDTO dto) {
        SysUser user = getById(dto.getUserId());
        if (user == null) {
            throw new BaseException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }

        if (StrUtil.isNotBlank(dto.getUserName())) user.setUsername(dto.getUserName());
        if (StrUtil.isNotBlank(dto.getCnName())) user.setCnName(dto.getCnName());
        if (StrUtil.isNotBlank(dto.getEnName())) user.setEnName(dto.getEnName());
        if (dto.getAge() != null) user.setAge(dto.getAge());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getAvatarUrl() != null) user.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getSex() != null) user.setSex(dto.getSex());
        if (dto.getSort() != null) user.setSort(dto.getSort());
        if (dto.getMotto() != null) user.setMotto(dto.getMotto());
        if (dto.getTags() != null) user.setTags(JSONUtil.toJsonStr(dto.getTags()));
        if (dto.getCity() != null) user.setCity(JSONUtil.toJsonStr(dto.getCity()));
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getJobsId() != null) user.setJobsId(dto.getJobsId());
        if (dto.getOrgId() != null) user.setOrgId(dto.getOrgId());
        if (dto.getRoleId() != null) {
            user.setRoleId(dto.getRoleId());
            // 更新角色关联
            userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()));
            SysUserRole ur = new SysUserRole();
            ur.setUserId(user.getId());
            ur.setRoleId(dto.getRoleId());
            userRoleMapper.insert(ur);
        }
        updateById(user);
    }

    @Transactional
    public void deleteUser(String userId) {
        removeById(userId);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
    }

    @Transactional
    public void updateUserStatus(String userId, Integer status) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BaseException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        user.setStatus(status);
        updateById(user);
    }

    @Transactional
    public void resetPassword(String userId, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BaseException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        user.setPassword(PasswordEncoder.encode(newPassword != null ? newPassword : "123456"));
        updateById(user);
    }
}
