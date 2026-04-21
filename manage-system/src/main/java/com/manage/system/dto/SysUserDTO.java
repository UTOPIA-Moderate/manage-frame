package com.manage.system.dto;

import lombok.Data;

import java.util.List;

@Data
public class SysUserDTO {

    private String userId;
    private String userName;
    private String workNo;
    private String password;
    private String cnName;
    private String enName;
    private Integer age;
    private String email;
    private String phone;
    private String avatarUrl;
    private String sex;
    private Integer sort;
    private String motto;
    private List<String> tags;
    private List<String> city;
    private String address;
    private String jobsId;
    private String orgId;
    private String roleId;
    private Integer status;
}
