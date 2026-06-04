package com.nj.controller;

import com.luhuiguo.fastdfs.domain.StorePath;
import com.luhuiguo.fastdfs.service.FastFileStorageClient;
import com.nj.pojo.User;
import com.nj.pojo.res.Result;
import com.nj.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Describe: 文件操作
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/7/26 11:33
 **/
@RestController
@RequestMapping("file")
public class FileController {
    @Autowired
    public FileService fileService;

    @PostMapping("upload")
    public Result upload(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        try {
            return fileService.uploadFile(file,request);
        }catch (Exception e){
            e.printStackTrace();
            return Result.errorResult(600,"文件上传失败");
        }
    }

    @GetMapping("download")
    public void download(@RequestParam("fileId") String fileId, HttpServletResponse response){
        try {
             fileService.downloadFile(fileId,response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("delFile")
    public Result delFile(@RequestParam("fileId") String fileId){
        return fileService.delFile(fileId);
    }

    @PostMapping("getFileList")
    public Result getFileList(){
        return fileService.getFileList();
    }

}
