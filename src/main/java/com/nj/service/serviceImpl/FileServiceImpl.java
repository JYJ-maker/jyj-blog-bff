package com.nj.service.serviceImpl;

import com.luhuiguo.fastdfs.domain.StorePath;
import com.luhuiguo.fastdfs.service.FastFileStorageClient;
import com.nj.mapper.FileMapper;
import com.nj.pojo.File;
import com.nj.pojo.res.Result;
import com.nj.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

/**
 * @Describe:
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/7/26 13:57
 **/
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private static final int ERROR_FILE_TYPE_NOT_SUPPORTED = 601;
    private static final int ERROR_FILE_DOWNLOAD_FAILED = 602;
    private static final int ERROR_FILE_NOT_FOUND = 604;
    private static final int ERROR_FILE_SIZE_EXCEEDED = 605;
    private static final String DEFAULT_GROUP_NAME = "default";
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private FileMapper fileMapper;

    @Value("${allow-file-type}")
    private String allowFileType;

    @Override
    public Result uploadFile(MultipartFile file, HttpServletRequest request) throws IOException {
        String fileName = getRequestParam(request, "file_name");
        if (!StringUtils.hasText(fileName)) {
            fileName = file.getOriginalFilename();
        }

        if (!isValidFileName(fileName)) {
            return Result.errorResult(ERROR_FILE_TYPE_NOT_SUPPORTED, "不支持该文件类型上传");
        }

        String groupName = getRequestParam(request, "group_name");
        if (!StringUtils.hasText(groupName)) {
            groupName = DEFAULT_GROUP_NAME;
        }

        String uploaderName = getRequestParam(request, "uploader_name");
        String uploaderId = getRequestParam(request, "uploader_id");
        String extName = extractFileExtension(fileName);

        try {
            StorePath storePath = fastFileStorageClient.uploadFile(
                    groupName, file.getInputStream(), file.getSize(), extName);

            File fileInfo = buildFileInfo(fileName, groupName, storePath.getPath(), uploaderId, uploaderName);
            fileMapper.addFile(fileInfo);

            return Result.successfulResult(fileInfo.getFileId());
        } catch (FileSizeLimitExceededException e) {
            log.warn("文件大小超出限制: {}", fileName);
            return Result.errorResult(ERROR_FILE_SIZE_EXCEEDED, "文件大小超出限制范围");
        }
    }

    @Override
    public Result downloadFile(String fileId, HttpServletResponse response) {
        File file = fileMapper.getFile(fileId);
        if (ObjectUtils.isEmpty(file)) {
            return Result.errorResult(ERROR_FILE_NOT_FOUND, "文件不存在");
        }

        try (OutputStream outputStream = new BufferedOutputStream(response.getOutputStream())) {
            byte[] bytes = fastFileStorageClient.downloadFile(file.getGroupName(), file.getFilePath());

            response.reset();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(file.getFileName(), "UTF-8"));
            outputStream.write(bytes);
            outputStream.flush();

            return Result.successfulResult();
        } catch (IOException e) {
            log.error("文件下载失败, fileId: {}", fileId, e);
            return Result.errorResult(ERROR_FILE_DOWNLOAD_FAILED, "文件下载失败");
        }
    }

    @Override
    public Result delFile(String fileId) {
        File file = fileMapper.getFile(fileId);
        if (ObjectUtils.isEmpty(file)) {
            return Result.errorResult(ERROR_FILE_NOT_FOUND, "文件不存在");
        }

        fastFileStorageClient.deleteFile(file.getGroupName(), file.getFilePath());
        fileMapper.delFile(fileId);
        log.info("文件删除成功, fileId: {}, fileName: {}", fileId, file.getFileName());

        return Result.successfulResult();
    }

    @Override
    public Result getFileList() {
        return Result.successfulResult(fileMapper.getFileList());
    }

    /**
     * 构建文件信息对象
     */
    private File buildFileInfo(String fileName, String groupName, String filePath,
                               String uploaderId, String uploaderName) {
        File fileInfo = new File();
        fileInfo.setFileId(generateFileId());
        fileInfo.setFileName(fileName);
        fileInfo.setGroupName(groupName);
        fileInfo.setFilePath(filePath);
        fileInfo.setUploaderId(uploaderId);
        fileInfo.setUploaderName(uploaderName);
        fileInfo.setUploadTime(new Date());
        return fileInfo;
    }

    /**
     * 生成文件ID
     */
    private String generateFileId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 验证文件名是否有效
     */
    private boolean isValidFileName(String fileName) {
        return StringUtils.hasText(fileName) && checkFileType(fileName);
    }

    /**
     * 提取文件扩展名
     */
    private String extractFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return lastDotIndex == -1 ? "" : fileName.substring(lastDotIndex + 1);
    }

    /**
     * 获取请求体中信息
     *
     * @param request   请求体
     * @param paramName 键名
     * @return 参数值
     */
    private String getRequestParam(HttpServletRequest request, String paramName) {
        String paramValue = request.getParameter(paramName);
        if (!StringUtils.hasText(paramValue)) {
            Object attribute = request.getAttribute(paramName);
            return attribute == null ? null : String.valueOf(attribute);
        }
        return paramValue;
    }

    /**
     * 验证文件类型是否合法
     *
     * @param fileName 文件名
     * @return 是否合法
     */
    private boolean checkFileType(String fileName) {
        String fileType = extractFileExtension(fileName);
        return StringUtils.hasText(fileType) && allowFileType.contains(fileType);
    }

}
