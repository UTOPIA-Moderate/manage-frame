package com.manage.system.dto;

import lombok.Data;

@Data
public class SysMenuDTO {

    private String menuId;
    private String name;
    private String menuType;
    private String path;
    private String icon;
    private String component;
    private String redirect;
    private String parentId;
    private String target;
    private String permission;
    private String layout;
    private String navTheme;
    private String headerTheme;
    private Boolean hideChildrenInMenu;
    private Boolean hideInMenu;
    private Boolean hideInBreadcrumb;
    private Boolean headerRender;
    private Boolean flatMenu;
    private Boolean fixedHeader;
    private Boolean fixSiderbar;
    private Boolean footerRender;
    private Boolean menuRender;
    private Integer sort;
    private Integer status;
}
