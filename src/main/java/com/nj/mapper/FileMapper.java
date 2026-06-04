package com.nj.mapper;

import com.nj.pojo.File;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Describe:
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/7/26 16:30
 **/
@Mapper
public interface FileMapper {
    void addFile(File file);

    File getFile(String fileId);

    void delFile(String fileId);

    List<File> getFileList();
}
