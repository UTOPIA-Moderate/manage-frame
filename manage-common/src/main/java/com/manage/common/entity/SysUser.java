package com.manage.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private String username;
    private String password;
    private String nickname;
    private String cnName;
    private String enName;
    private Integer age;
    private String email;
    private String phone;
    private String avatarUrl;
    private String sex;
    private Integer sort;
    private Integer status;
    private String token;
    private String motto;
    private String tags;
    private String city;
    private String address;
    private String jobsId;
    private String orgId;
    private String roleId;
    private String founder;
    private Integer loginNum;
    private String loginLastIp;
    private LocalDateTime loginLastTime;
}
