package com.manage.job.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.manage.common.entity.SysJobLog;
import com.manage.common.result.PageResult;
import com.manage.common.result.Result;
import com.manage.job.service.SysJobLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/job/log")
@RequiredArgsConstructor
public class SysJobLogController {

    private final SysJobLogService jobLogService;

    @GetMapping("/list")
    public Result<PageResult<SysJobLog>> getLogList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String job_id,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String start_time,
            @RequestParam(required = false) String end_time) {
        IPage<SysJobLog> pageData = jobLogService.getLogPage(page, pageSize, job_id, status, start_time, end_time);
        return Result.ok(PageResult.of(
                (int) pageData.getCurrent(),
                (int) pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        ));
    }

    @DeleteMapping("/clean")
    public Result<Long> cleanLogs(@RequestBody(required = false) java.util.Map<String, String> body) {
        String jobId = body != null ? body.get("job_id") : null;
        long count = jobLogService.cleanLogs(jobId);
        return Result.ok("清空成功！", count);
    }
}
