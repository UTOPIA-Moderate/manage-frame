package com.manage.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_job")
public class SysJob extends BaseEntity {

    private String jobName;
    private String jobGroup;
    private String jobHandler;
    private String cronExpression;
    private Integer misfirePolicy;
    private Integer status;
    private String remark;
}
