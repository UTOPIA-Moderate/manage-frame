package com.manage.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.manage.common.entity.SysOperLog;
import com.manage.common.result.PageResult;
import com.manage.common.result.Result;
import com.manage.system.service.SysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/operation-log")
@RequiredArgsConstructor
public class SysOperLogController {

    private final SysOperLogService operLogService;

    @GetMapping
    public Result<PageResult<SysOperLog>> getLogList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String user_name,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String business_type,
            @RequestParam(required = false) String operator_type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String start_time,
            @RequestParam(required = false) String end_time) {
        IPage<SysOperLog> pageData = operLogService.getLogPage(page, pageSize, user_name,
                module, business_type, operator_type, status, start_time, end_time);
        return Result.ok(PageResult.of(
                (int) pageData.getCurrent(),
                (int) pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        ));
    }

    @GetMapping("/{logId}")
    public Result<SysOperLog> getLogDetail(@PathVariable String logId) {
        return Result.ok(operLogService.getLogById(logId));
    }

    @DeleteMapping
    public Result<Integer> deleteLogs(@RequestBody Map<String, List<String>> body) {
        List<String> ids = body.get("ids");
        if (ids != null && !ids.isEmpty()) {
            operLogService.deleteLogs(ids);
        }
        return Result.ok("删除成功！", ids != null ? ids.size() : 0);
    }
}
