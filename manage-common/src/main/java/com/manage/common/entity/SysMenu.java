package com.manage.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    private String name;
    private String parentId;
    private String menuType;
    private String path;
    private String icon;
    private String component;
    private String redirect;
    private String target;
    private String permission;
    private String layout;
    private String navTheme;
    private String headerTheme;
    private Integer hideChildrenInMenu;
    private Integer hideInMenu;
    private Integer hideInBreadcrumb;
    private Integer headerRender;
    private Integer footerRender;
    private Integer menuRender;
    private Integer flatMenu;
    private Integer fixedHeader;
    private Integer fixSiderbar;
    private String founder;
    private Integer sort;
    private Integer status;
}
