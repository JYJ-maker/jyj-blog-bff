package com.nj.service;

import com.nj.pojo.res.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Describe:
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/7/26 13:57
 **/
public interface FileService {

    Result uploadFile(MultipartFile file, HttpServletRequest request) throws IOException;

    Result downloadFile(String fileId, HttpServletResponse response) throws IOException;

    Result delFile(String fileId);

    Result getFileList();

}
