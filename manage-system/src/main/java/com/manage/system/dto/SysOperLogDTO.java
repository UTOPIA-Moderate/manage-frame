package com.manage.system.dto;

import lombok.Data;

@Data
public class SysOperLogDTO {

    private String logId;
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
    private String operateTime;
    private String createdTime;
}
