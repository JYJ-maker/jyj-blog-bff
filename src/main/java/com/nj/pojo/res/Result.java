package com.nj.pojo.res;

import com.nj.pojo.Enums.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Describe: 响应信息
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2023/12/30 16:11
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    /**
     * 响应编码
     */
    private Integer code = StatusCode.SUCCESS_STATUS.getCode();
    /**
     * 响应结果
     */
    private Boolean successful = true;
    /**
     * 响应错误提示
     */
    private String msg = StatusCode.SUCCESS_STATUS.getDesc();
    /**
     * 响应数据
     */
    private Object data;

    /**
     * 响应成功方法 -- 无参
     * @return
     */
    public static Result successfulResult(){
        return new Result(200,true,"成功",null);
    }

    /**
     * 响应成功方法 -- 有参
     * @param data
     * @return
     */
    public static Result successfulResult(Object data){
        Result result = new Result();
        result.setData(data);
        return result;
    }

    /**
     * 错误响应
     * @param msg
     * @return
     */
    public static Result errorResult(Integer code,String msg){
        return new Result(code, false,msg, null);
    }

    /**
     * 参数错误
     * @return
     */
    public static Result paramsErrorResult(){
        return new Result(StatusCode.NO_MAIN_PARAMS.getCode(), false,StatusCode.NO_MAIN_PARAMS.getDesc(),null);
    }

}
