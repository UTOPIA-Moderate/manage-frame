package com.manage.system.dto;

import lombok.Data;

import java.util.List;

@Data
public class SysRoleDTO {

    private String roleId;
    private String roleName;
    private String roleCode;
    private String describe;
    private Integer sort;
    private Integer status;
    private List<MenuPermission> menuPermission;

    @Data
    public static class MenuPermission {
        private String menuId;
        private String permission;
        private String menuType;
    }
}
