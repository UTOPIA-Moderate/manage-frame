package com.manage.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_job_log")
public class SysJobLog extends BaseEntity {

    private String jobId;
    private String jobName;
    private String jobGroup;
    private String handlerName;
    private String executorParams;
    private Integer executorTime;
    private Integer status;
    private String errorMsg;
    private LocalDateTime executeTime;
}
