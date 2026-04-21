package com.manage.file.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manage.common.entity.SysFile;
import com.manage.common.exception.BaseException;
import com.manage.common.mapper.SysFileMapper;
import com.manage.common.result.ResultCode;
import com.manage.common.util.SecurityUtils;
import com.manage.file.dto.SysFileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileService extends ServiceImpl<SysFileMapper, SysFile> {

    private final StorageService storageService;

    public IPage<SysFile> getFilePage(int page, int pageSize, String fileName, String storageType) {
        LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(fileName)) wrapper.like(SysFile::getFileName, fileName);
        if (StrUtil.isNotBlank(storageType)) wrapper.eq(SysFile::getStorageType, storageType);
        wrapper.orderByDesc(SysFile::getCreatedTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    @Transactional
    public Map<String, Object> uploadFile(MultipartFile file, String operator) {
        if (file == null || file.isEmpty()) {
            throw new BaseException(ResultCode.BAD_REQUEST.getCode(), "文件不能为空");
        }

        String originalName = file.getOriginalFilename();
        String fileExt = "";
        if (originalName != null && originalName.contains(".")) {
            fileExt = originalName.substring(originalName.lastIndexOf("."));
        }
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileId = SecurityUtils.generateUuid();
        String fileName = datePath + "/" + fileId + fileExt;
        String contentType = file.getContentType();

        try {
            storageService.uploadFile(fileName, file.getInputStream(), file.getSize(),
                    contentType != null ? contentType : "application/octet-stream");
        } catch (IOException e) {
            throw new BaseException(ResultCode.INTERNAL_ERROR.getCode(), "文件上传失败");
        }

        // 保存记录
        SysFile record = new SysFile();
        record.setId(fileId);
        record.setFileName(fileName);
        record.setOriginalName(originalName);
        record.setFileSize(file.getSize());
        record.setFileType(contentType);
        record.setFilePath(fileName);
        record.setStorageType("minio");
        record.setBucketName("manage-frame");
        record.setFileUrl(storageService.getFileUrl(fileName));
        record.setCreateBy(operator);
        save(record);

        Map<String, Object> result = new HashMap<>();
        result.put("file_id", record.getId());
        result.put("file_name", record.getFileName());
        result.put("original_name", record.getOriginalName());
        result.put("file_size", record.getFileSize());
        result.put("file_type", record.getFileType());
        result.put("file_path", record.getFilePath());
        result.put("file_url", record.getFileUrl());
        return result;
    }

    @Transactional
    public void deleteFiles(List<String> fileIds) {
        for (String fileId : fileIds) {
            SysFile file = getById(fileId);
            if (file != null) {
                try {
                    storageService.deleteFile(file.getFilePath());
                } catch (Exception e) {
                    log.warn("Delete storage file failed: {}", file.getFilePath());
                }
                removeById(fileId);
            }
        }
    }

    public Map<String, Object> getDownloadUrl(String fileId) {
        SysFile file = getById(fileId);
        if (file == null) {
            throw new BaseException(ResultCode.NOT_FOUND.getCode(), "文件不存在");
        }
        String url = storageService.getPresignedDownloadUrl(file.getFilePath());
        Map<String, Object> result = new HashMap<>();
        result.put("download_url", url);
        result.put("expires_in", 3600);
        return result;
    }
}
