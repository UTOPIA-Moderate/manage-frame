package com.manage.job.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manage.common.entity.SysJob;
import com.manage.common.entity.SysJobLog;
import com.manage.common.exception.BaseException;
import com.manage.common.result.ResultCode;
import com.manage.common.util.SecurityUtils;
import com.manage.job.dto.SysJobDTO;
import com.manage.common.mapper.SysJobLogMapper;
import com.manage.common.mapper.SysJobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysJobService extends ServiceImpl<SysJobMapper, SysJob> {

    private final SysJobLogMapper jobLogMapper;

    public IPage<SysJob> getJobPage(int page, int pageSize, String jobName, String jobGroup, Integer status) {
        LambdaQueryWrapper<SysJob> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(jobName)) wrapper.like(SysJob::getJobName, jobName);
        if (StrUtil.isNotBlank(jobGroup)) wrapper.eq(SysJob::getJobGroup, jobGroup);
        if (status != null) wrapper.eq(SysJob::getStatus, status);
        wrapper.orderByDesc(SysJob::getCreatedTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    public SysJob getJobById(String jobId) {
        SysJob job = getById(jobId);
        if (job == null) {
            throw new BaseException(ResultCode.NOT_FOUND.getCode(), "任务不存在");
        }
        return job;
    }

    @Transactional
    public String createJob(SysJobDTO dto) {
        SysJob job = new SysJob();
        job.setId(SecurityUtils.generateUuid());
        job.setJobName(dto.getJobName());
        job.setJobGroup(dto.getJobGroup() != null ? dto.getJobGroup() : "default");
        job.setJobHandler(dto.getJobHandler());
        job.setCronExpression(dto.getCronExpression());
        job.setMisfirePolicy(dto.getMisfirePolicy() != null ? dto.getMisfirePolicy() : 0);
        job.setStatus(dto.getStatus() != null ? dto.getStatus() : 0);
        job.setRemark(dto.getRemark());
        save(job);
        return job.getId();
    }

    @Transactional
    public void updateJob(SysJobDTO dto) {
        SysJob job = getById(dto.getJobId());
        if (job == null) {
            throw new BaseException(ResultCode.NOT_FOUND.getCode(), "任务不存在");
        }
        if (StrUtil.isNotBlank(dto.getJobName())) job.setJobName(dto.getJobName());
        if (StrUtil.isNotBlank(dto.getJobGroup())) job.setJobGroup(dto.getJobGroup());
        if (StrUtil.isNotBlank(dto.getJobHandler())) job.setJobHandler(dto.getJobHandler());
        if (StrUtil.isNotBlank(dto.getCronExpression())) job.setCronExpression(dto.getCronExpression());
        if (dto.getMisfirePolicy() != null) job.setMisfirePolicy(dto.getMisfirePolicy());
        if (dto.getStatus() != null) job.setStatus(dto.getStatus());
        if (dto.getRemark() != null) job.setRemark(dto.getRemark());
        updateById(job);
    }

    @Transactional
    public void deleteJob(String jobId) {
        removeById(jobId);
    }

    @Transactional
    public void pauseJob(String jobId) {
        SysJob job = getById(jobId);
        if (job == null) throw new BaseException(ResultCode.NOT_FOUND.getCode(), "任务不存在");
        job.setStatus(0);
        updateById(job);
    }

    @Transactional
    public void resumeJob(String jobId) {
        SysJob job = getById(jobId);
        if (job == null) throw new BaseException(ResultCode.NOT_FOUND.getCode(), "任务不存在");
        job.setStatus(1);
        updateById(job);
    }

    public Map<String, Object> execJob(String jobId) {
        SysJob job = getById(jobId);
        if (job == null) throw new BaseException(ResultCode.NOT_FOUND.getCode(), "任务不存在");

        // 记录执行日志
        long start = System.currentTimeMillis();
        SysJobLog jobLog = new SysJobLog();
        jobLog.setId(SecurityUtils.generateUuid());
        jobLog.setJobId(jobId);
        jobLog.setJobName(job.getJobName());
        jobLog.setJobGroup(job.getJobGroup());
        jobLog.setHandlerName(job.getJobHandler());
        jobLog.setStatus(0);
        jobLog.setExecuteTime(LocalDateTime.now());

        try {
            // 实际执行：这里可以反射调用 jobHandler 对应的 Spring Bean 方法
            executeJobHandler(job);
            jobLog.setExecutorTime((int) (System.currentTimeMillis() - start));
            jobLog.setStatus(0);
        } catch (Exception e) {
            jobLog.setExecutorTime((int) (System.currentTimeMillis() - start));
            jobLog.setStatus(1);
            jobLog.setErrorMsg(e.getMessage());
            log.error("Job execution failed: {}", job.getJobHandler(), e);
        }

        jobLogMapper.insert(jobLog);

        Map<String, Object> result = new HashMap<>();
        result.put("job_id", jobId);
        result.put("execute_time", jobLog.getExecuteTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return result;
    }

    private void executeJobHandler(SysJob job) {
        // 占位实现：实际项目中根据 jobHandler 名称查找并调用对应的 Spring Bean
        log.info("Executing job handler: {}", job.getJobHandler());
    }

    // 定时调度入口（基于数据库轮询的简单实现）
    // 生产环境建议对接 XXL-Job 执行器
}
