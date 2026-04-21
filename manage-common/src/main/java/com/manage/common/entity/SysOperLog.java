package com.manage.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oper_log")
public class SysOperLog extends BaseEntity {

    private String userId;
    private String userName;
    private String module;
    private String businessType;
    private String method;
    private String requestMethod;
    private String operatorType;
    private String requestUrl;
    private String requestParam;
    private String responseData;
    private Integer status;
    private String errorMsg;
    private String ip;
    private String location;
    private LocalDateTime operateTime;
}
