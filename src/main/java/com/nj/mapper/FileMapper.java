package com.nj.mapper;

import com.nj.pojo.File;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文件数据访问层
 *
 * @author jiayj
 * @version 1.0
 * @date 2024/7/26
 */
@Mapper
public interface FileMapper {

    /**
     * 新增文件记录
     *
     * @param file 文件信息
     */
    void addFile(File file);

    /**
     * 根据文件ID查询文件信息
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    File getFile(String fileId);

    /**
     * 根据文件ID删除文件记录
     *
     * @param fileId 文件ID
     */
    void delFile(String fileId);

    /**
     * 获取所有文件列表
     *
     * @return 文件列表
     */
    List<File> getFileList();
}
