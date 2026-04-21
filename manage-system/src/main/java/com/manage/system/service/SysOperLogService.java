package com.manage.system.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manage.common.entity.SysOperLog;
import com.manage.system.mapper.SysOperLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysOperLogService extends ServiceImpl<SysOperLogMapper, SysOperLog> {

    public IPage<SysOperLog> getLogPage(int page, int pageSize, String userName, String module,
                                        String businessType, String operatorType, Integer status,
                                        String startTime, String endTime) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(userName)) wrapper.like(SysOperLog::getUserName, userName);
        if (StrUtil.isNotBlank(module)) wrapper.like(SysOperLog::getModule, module);
        if (StrUtil.isNotBlank(businessType)) wrapper.eq(SysOperLog::getBusinessType, businessType);
        if (StrUtil.isNotBlank(operatorType)) wrapper.eq(SysOperLog::getOperatorType, operatorType);
        if (status != null) wrapper.eq(SysOperLog::getStatus, status);
        if (StrUtil.isNotBlank(startTime)) wrapper.ge(SysOperLog::getOperateTime, LocalDateTime.parse(startTime));
        if (StrUtil.isNotBlank(endTime)) wrapper.le(SysOperLog::getOperateTime, LocalDateTime.parse(endTime));
        wrapper.orderByDesc(SysOperLog::getOperateTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    public SysOperLog getLogById(String logId) {
        return getById(logId);
    }

    public void deleteLogs(List<String> ids) {
        removeBatchByIds(ids);
    }
}
