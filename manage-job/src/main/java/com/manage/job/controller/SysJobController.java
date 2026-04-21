package com.manage.job.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.manage.common.entity.SysJob;
import com.manage.common.result.PageResult;
import com.manage.common.result.Result;
import com.manage.job.dto.SysJobDTO;
import com.manage.job.service.SysJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class SysJobController {

    private final SysJobService jobService;

    @GetMapping("/list")
    public Result<PageResult<SysJob>> getJobList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String job_name,
            @RequestParam(required = false) String job_group,
            @RequestParam(required = false) Integer status) {
        IPage<SysJob> pageData = jobService.getJobPage(page, pageSize, job_name, job_group, status);
        return Result.ok(PageResult.of(
                (int) pageData.getCurrent(),
                (int) pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        ));
    }

    @GetMapping("/{jobId}")
    public Result<SysJob> getJobDetail(@PathVariable String jobId) {
        return Result.ok(jobService.getJobById(jobId));
    }

    @PostMapping
    public Result<Map<String, String>> createJob(@RequestBody SysJobDTO dto) {
        String jobId = jobService.createJob(dto);
        Map<String, String> result = new HashMap<>();
        result.put("job_id", jobId);
        return Result.ok("创建成功！", result);
    }

    @PutMapping("/{jobId}")
    public Result<Integer> updateJob(@PathVariable String jobId, @RequestBody SysJobDTO dto) {
        dto.setJobId(jobId);
        jobService.updateJob(dto);
        return Result.ok("更新成功！", 1);
    }

    @DeleteMapping("/{jobId}")
    public Result<Integer> deleteJob(@PathVariable String jobId) {
        jobService.deleteJob(jobId);
        return Result.ok("删除成功！", 1);
    }

    @PutMapping("/pause/{jobId}")
    public Result<Integer> pauseJob(@PathVariable String jobId) {
        jobService.pauseJob(jobId);
        return Result.ok("暂停成功！", 1);
    }

    @PutMapping("/resume/{jobId}")
    public Result<Integer> resumeJob(@PathVariable String jobId) {
        jobService.resumeJob(jobId);
        return Result.ok("恢复成功！", 1);
    }

    @PostMapping("/exec/{jobId}")
    public Result<Map<String, Object>> execJob(@PathVariable String jobId) {
        return Result.ok("执行成功！", jobService.execJob(jobId));
    }
}
