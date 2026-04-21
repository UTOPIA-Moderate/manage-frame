package com.manage.job.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manage.common.entity.SysJobLog;
import com.manage.common.mapper.SysJobLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SysJobLogService extends ServiceImpl<SysJobLogMapper, SysJobLog> {

    public IPage<SysJobLog> getLogPage(int page, int pageSize, String jobId,
                                       Integer status, String startTime, String endTime) {
        LambdaQueryWrapper<SysJobLog> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(jobId)) wrapper.eq(SysJobLog::getJobId, jobId);
        if (status != null) wrapper.eq(SysJobLog::getStatus, status);
        if (StrUtil.isNotBlank(startTime)) wrapper.ge(SysJobLog::getExecuteTime, LocalDateTime.parse(startTime));
        if (StrUtil.isNotBlank(endTime)) wrapper.le(SysJobLog::getExecuteTime, LocalDateTime.parse(endTime));
        wrapper.orderByDesc(SysJobLog::getExecuteTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    public long cleanLogs(String jobId) {
        LambdaQueryWrapper<SysJobLog> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(jobId)) wrapper.eq(SysJobLog::getJobId, jobId);
        long count = count(wrapper);
        remove(wrapper);
        return count;
    }
}
