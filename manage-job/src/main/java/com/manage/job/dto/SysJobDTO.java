package com.manage.job.dto;

import lombok.Data;

@Data
public class SysJobDTO {

    private String jobId;
    private String jobName;
    private String jobGroup;
    private String jobHandler;
    private String cronExpression;
    private Integer misfirePolicy;
    private Integer status;
    private String remark;
}
