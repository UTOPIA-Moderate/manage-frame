package com.manage.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
public class SysFile extends BaseEntity {

    private String fileName;
    private String originalName;
    private Long fileSize;
    private String fileType;
    private String filePath;
    private String storageType;
    private String bucketName;
    private String fileUrl;
    private String createBy;
}
