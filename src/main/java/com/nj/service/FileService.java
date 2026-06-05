package com.nj.service;

import com.nj.pojo.res.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件服务接口
 *
 * @author jiayj
 * @version 1.0
 * @date 2024/7/26
 */
public interface FileService {

    /**
     * 上传文件到FastDFS
     *
     * @param file    待上传的文件
     * @param request HTTP请求对象，包含文件名、分组名等附加参数
     * @return 上传结果，成功时返回文件ID
     * @throws IOException 文件读取异常
     */
    Result uploadFile(MultipartFile file, HttpServletRequest request) throws IOException;

    /**
     * 从FastDFS下载文件
     *
     * @param fileId   文件ID
     * @param response HTTP响应对象，用于输出文件流
     * @return 下载结果
     * @throws IOException 文件写入异常
     */
    Result downloadFile(String fileId, HttpServletResponse response) throws IOException;

    /**
     * 删除文件（同时删除FastDFS和数据库记录）
     *
     * @param fileId 文件ID
     * @return 删除结果
     */
    Result delFile(String fileId);

    /**
     * 获取文件列表
     *
     * @return 文件列表
     */
    Result getFileList();
}
