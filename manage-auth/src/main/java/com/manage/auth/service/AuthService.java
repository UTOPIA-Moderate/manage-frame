package com.manage.auth.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manage.common.entity.SysMenu;
import com.manage.common.entity.SysRole;
import com.manage.common.entity.SysUser;
import com.manage.common.exception.BaseException;
import com.manage.common.mapper.SysMenuMapper;
import com.manage.common.mapper.SysRoleMapper;
import com.manage.common.mapper.SysUserMapper;
import com.manage.common.result.ResultCode;
import com.manage.common.util.PasswordEncoder;
import com.manage.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String TOKEN_PREFIX = "auth:token:";
    private static final long TOKEN_EXPIRE_HOURS = 12;

    @Transactional
    public Map<String, Object> login(String type, String userName, String password, String phone, String verifyCode) {
        if ("account".equals(type)) {
            if (StrUtil.isBlank(userName) || StrUtil.isBlank(password)) {
                throw new BaseException(ResultCode.BAD_REQUEST.getCode(), "用户名和密码不能为空");
            }
            SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, userName));
            if (user == null) {
                throw new BaseException(ResultCode.UNAUTHORIZED.getCode(), "用户不存在");
            }
            if (!PasswordEncoder.matches(password, user.getPassword())) {
                throw new BaseException(ResultCode.UNAUTHORIZED.getCode(), "密码错误");
            }
            if (user.getStatus() == null || user.getStatus() != 1) {
                throw new BaseException(ResultCode.FORBIDDEN.getCode(), "账号已被禁用");
            }

            // 生成 Token
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", user.getUsername());
            claims.put("cnName", user.getCnName());
            String token = SecurityUtils.createToken(user.getId(), user.getUsername(), claims);

            // 记录登录信息
            user.setLoginNum(user.getLoginNum() == null ? 1 : user.getLoginNum() + 1);
            user.setLoginLastTime(LocalDateTime.now());
            user.setToken(token);
            userMapper.updateById(user);

            // 缓存 Token
            redisTemplate.opsForValue().set(TOKEN_PREFIX + token, user.getId(), TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);

            Map<String, Object> result = new HashMap<>();
            result.put("access_token", token);
            result.put("login_last_time", user.getLoginLastTime());
            return result;
        } else if ("phone".equals(type)) {
            // 手机号登录暂不实现
            throw new BaseException(ResultCode.BAD_REQUEST.getCode(), "手机号登录暂未实现");
        } else {
            throw new BaseException(ResultCode.BAD_REQUEST.getCode(), "不支持的登录类型");
        }
    }

    public void logout(String token) {
        if (StrUtil.isNotBlank(token)) {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            redisTemplate.delete(TOKEN_PREFIX + token);
        }
    }

    public UserInfoResponse getUserInfo(String token) {
        String userId = getUserIdFromToken(token);
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BaseException(ResultCode.UNAUTHORIZED);
        }

        UserInfoResponse resp = new UserInfoResponse();
        resp.setUserId(user.getId());
        resp.setUserName(user.getUsername());
        resp.setCnName(user.getCnName());
        resp.setEnName(user.getEnName());
        resp.setAge(user.getAge());
        resp.setEmail(user.getEmail());
        resp.setPhone(user.getPhone());
        resp.setAvatarUrl(user.getAvatarUrl());
        resp.setSex(user.getSex());
        resp.setSort(user.getSort());
        resp.setStatus(user.getStatus());
        resp.setMotto(user.getMotto());
        if (StrUtil.isNotBlank(user.getTags())) {
            resp.setTags(JSONUtil.toList(user.getTags(), String.class));
        }
        if (StrUtil.isNotBlank(user.getCity())) {
            resp.setCity(JSONUtil.toList(user.getCity(), String.class));
        }
        resp.setAddress(user.getAddress());
        resp.setJobsId(user.getJobsId());
        resp.setOrgId(user.getOrgId());
        resp.setRoleId(user.getRoleId());
        resp.setFounder(user.getFounder());
        resp.setLoginNum(user.getLoginNum());
        resp.setLoginLastIp(user.getLoginLastIp());
        resp.setLoginLastTime(user.getLoginLastTime() != null ? user.getLoginLastTime().toString() : null);
        resp.setCreatedTime(user.getCreatedTime() != null ? user.getCreatedTime().toString() : null);
        resp.setUpdatedTime(user.getUpdatedTime() != null ? user.getUpdatedTime().toString() : null);

        // 角色名称
        if (StrUtil.isNotBlank(user.getRoleId())) {
            SysRole role = roleMapper.selectById(user.getRoleId());
            if (role != null) {
                resp.setRoleName(role.getRoleName());
            }
        }
        return resp;
    }

    public List<String> getPermissions(String token) {
        String userId = getUserIdFromToken(token);
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return Collections.emptyList();
        }

        List<String> permissions = new ArrayList<>();
        if (StrUtil.isNotBlank(user.getRoleId())) {
            // 查询角色关联的菜单权限
            List<SysMenu> menus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                    .inSql(SysMenu::getId,
                            "SELECT menu_id FROM sys_role_menu WHERE role_id = '" + user.getRoleId() + "'"));
            for (SysMenu menu : menus) {
                if (StrUtil.isNotBlank(menu.getPermission())) {
                    permissions.add(menu.getPermission());
                }
            }
        }
        return permissions;
    }

    public List<Map<String, Object>> getRoutesMenu(String token) {
        String userId = getUserIdFromToken(token);
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return Collections.emptyList();
        }

        List<SysMenu> allMenus = new ArrayList<>();
        if (StrUtil.isNotBlank(user.getRoleId())) {
            allMenus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getStatus, 1)
                    .inSql(SysMenu::getId,
                            "SELECT menu_id FROM sys_role_menu WHERE role_id = '" + user.getRoleId() + "'")
                    .orderByAsc(SysMenu::getSort));
        }

        // 构建树形结构
        return buildMenuTree(allMenus, null);
    }

    private List<Map<String, Object>> buildMenuTree(List<SysMenu> menus, String parentId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysMenu menu : menus) {
            if ((menu.getParentId() == null && parentId == null) ||
                    (menu.getParentId() != null && menu.getParentId().equals(parentId))) {
                Map<String, Object> node = new HashMap<>();
                node.put("menu_id", menu.getId());
                node.put("name", menu.getName());
                node.put("menu_type", menu.getMenuType());
                node.put("path", menu.getPath());
                node.put("icon", menu.getIcon());
                node.put("component", menu.getComponent());
                node.put("redirect", menu.getRedirect());
                node.put("parent_id", menu.getParentId());
                node.put("target", menu.getTarget());
                node.put("permission", menu.getPermission());
                node.put("layout", menu.getLayout());
                node.put("navTheme", menu.getNavTheme());
                node.put("headerTheme", menu.getHeaderTheme());
                node.put("hideChildrenInMenu", menu.getHideChildrenInMenu() != null && menu.getHideChildrenInMenu() == 1);
                node.put("hideInMenu", menu.getHideInMenu() != null && menu.getHideInMenu() == 1);
                node.put("hideInBreadcrumb", menu.getHideInBreadcrumb() != null && menu.getHideInBreadcrumb() == 1);
                node.put("headerRender", menu.getHeaderRender() != null && menu.getHeaderRender() == 1);
                node.put("flatMenu", menu.getFlatMenu() != null && menu.getFlatMenu() == 1);
                node.put("fixedHeader", menu.getFixedHeader() != null && menu.getFixedHeader() == 1);
                node.put("fixSiderbar", menu.getFixSiderbar() != null && menu.getFixSiderbar() == 1);
                node.put("sort", menu.getSort());
                node.put("status", menu.getStatus());
                node.put("created_time", menu.getCreatedTime() != null ? menu.getCreatedTime().toString() : null);
                node.put("updated_time", menu.getUpdatedTime() != null ? menu.getUpdatedTime().toString() : null);

                List<Map<String, Object>> children = buildMenuTree(menus, menu.getId());
                if (!children.isEmpty()) {
                    node.put("routes", children);
                }
                result.add(node);
            }
        }
        return result;
    }

    public String getVerifyCode() {
        // 简单生成4位验证码 SVG
        int code = (int) ((Math.random() * 9000) + 1000);
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"80\" height=\"32\"><text x=\"10\" y=\"24\" font-size=\"20\" fill=\"#333\">" + code + "</text></svg>";
    }

    private String getUserIdFromToken(String token) {
        if (token == null) {
            throw new BaseException(ResultCode.UNAUTHORIZED);
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String userId = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        if (userId == null) {
            // 尝试直接解析 JWT
            try {
                userId = SecurityUtils.getUserIdFromToken(token);
            } catch (Exception e) {
                throw new BaseException(ResultCode.UNAUTHORIZED);
            }
        }
        return userId;
    }
}
