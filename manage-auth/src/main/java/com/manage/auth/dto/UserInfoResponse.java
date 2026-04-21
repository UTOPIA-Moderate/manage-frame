package com.manage.auth.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserInfoResponse {

    private String userId;
    private String userName;
    private String workNo;
    private String cnName;
    private String enName;
    private Integer age;
    private String email;
    private String phone;
    private String avatarUrl;
    private String sex;
    private Integer sort;
    private Integer status;
    private String motto;
    private List<String> tags;
    private List<String> city;
    private String address;
    private String jobsId;
    private String orgId;
    private String roleId;
    private String founder;
    private Integer loginNum;
    private String loginLastIp;
    private String loginLastTime;
    private String createdTime;
    private String updatedTime;
    private String jobsName;
    private String orgName;
    private String roleName;
}
