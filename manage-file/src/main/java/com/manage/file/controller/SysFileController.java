package com.manage.file.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.manage.common.entity.SysFile;
import com.manage.common.result.PageResult;
import com.manage.common.result.Result;
import com.manage.file.service.SysFileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class SysFileController {

    private final SysFileService fileService;

    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        String operator = getOperator(request);
        Map<String, Object> result = fileService.uploadFile(file, operator);
        return Result.ok("上传成功！", result);
    }

    @GetMapping("/list")
    public Result<PageResult<SysFile>> getFileList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String file_name,
            @RequestParam(required = false) String storage_type) {
        IPage<SysFile> pageData = fileService.getFilePage(page, pageSize, file_name, storage_type);
        return Result.ok(PageResult.of(
                (int) pageData.getCurrent(),
                (int) pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        ));
    }

    @DeleteMapping("/{ids}")
    public Result<Integer> deleteFiles(@PathVariable String ids) {
        // ids 是逗号分隔的字符串
        String[] idArr = ids.split(",");
        fileService.deleteFiles(Arrays.asList(idArr));
        return Result.ok("删除成功！", idArr.length);
    }

    @GetMapping("/download/{fileId}")
    public Result<Map<String, Object>> getDownloadUrl(@PathVariable String fileId) {
        return Result.ok(fileService.getDownloadUrl(fileId));
    }

    private String getOperator(HttpServletRequest request) {
        String token = request.getHeader("X-User-Token");
        if (token == null) token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        if (token != null) {
            try {
                return com.manage.common.util.SecurityUtils.getUsernameFromToken(token);
            } catch (Exception ignored) {}
        }
        return "anonymous";
    }
}
