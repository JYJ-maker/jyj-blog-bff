package com.nj.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Describe:
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/7/26 14:20
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class File {
    /**
     * 文件ID
     */
    private String fileId;
    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件地址
     */
    private String filePath;
    /**
     * 上传人ID
     */
    private String uploaderId;
    /**
     * 上传人姓名
     */
    private String uploaderName;
    /**
     * 上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

}
