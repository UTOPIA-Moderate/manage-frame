package com.manage.job.dto;

import lombok.Data;

@Data
public class SysJobLogDTO {

    private String logId;
    private String jobId;
    private String jobName;
    private String jobGroup;
    private String handlerName;
    private String executorParams;
    private Integer executorTime;
    private Integer status;
    private String errorMsg;
    private String executeTime;
}
