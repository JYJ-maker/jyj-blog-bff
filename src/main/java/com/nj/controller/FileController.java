package com.nj.controller;

import com.nj.pojo.res.Result;
import com.nj.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件管理控制器
 * <p>
 * 提供文件上传、下载、删除及列表查询接口。
 * 底层通过FastDFS实现分布式文件存储。
 * </p>
 *
 * @author jiayj
 * @version 1.0
 * @date 2024/7/26
 */
@Slf4j
@RestController
@RequestMapping("file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 上传文件
     *
     * @param file    上传的文件
     * @param request HTTP请求对象，包含额外参数（file_name, group_name等）
     * @return 上传结果，成功时返回文件ID
     */
    @PostMapping("upload")
    public Result upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            return fileService.uploadFile(file, request);
        } catch (Exception e) {
            log.error("文件上传失败: ", e);
            return Result.errorResult(600, "文件上传失败");
        }
    }

    /**
     * 下载文件
     *
     * @param fileId   文件ID
     * @param response HTTP响应对象，用于写入文件流
     */
    @GetMapping("download")
    public void download(@RequestParam("fileId") String fileId, HttpServletResponse response) {
        try {
            fileService.downloadFile(fileId, response);
        } catch (Exception e) {
            log.error("文件下载失败, fileId: {}", fileId, e);
        }
    }

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 删除结果
     */
    @GetMapping("delFile")
    public Result delFile(@RequestParam("fileId") String fileId) {
        return fileService.delFile(fileId);
    }

    /**
     * 获取文件列表
     *
     * @return 文件列表
     */
    @PostMapping("getFileList")
    public Result getFileList() {
        return fileService.getFileList();
    }
}
