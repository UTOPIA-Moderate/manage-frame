package com.manage.file.dto;

import lombok.Data;

@Data
public class SysFileDTO {

    private String fileId;
    private String fileName;
    private String originalName;
    private Long fileSize;
    private String fileType;
    private String filePath;
    private String storageType;
    private String bucketName;
    private String fileUrl;
    private String createBy;
    private String createdTime;
}
