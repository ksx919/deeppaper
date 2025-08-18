package com.fanxin.common;


import com.fanxin.exception.ErrorCode;
import lombok.Data;

/**
 * 通用返回结果类
 * @param <T> 数据类型
 */
@Data
public class CommonResult<T> {
    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 返回消息
     */
    private String message;
    
    /**
     * 返回数据
     */
    private T data;
    
    /**
     * 成功返回结果
     * @param data 返回的数据
     * @param <T> 数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.setCode(GlobalErrorCodeConstants.SUCCESS.getCode());
        result.setMessage(GlobalErrorCodeConstants.SUCCESS.getMsg());
        result.setData(data);
        return result;
    }
    
    /**
     * 成功返回结果
     * @param <T> 数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> success() {
        return success(null);
    }
    
    /**
     * 失败返回结果
     * @param errorCode 错误码
     * @param <T> 数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> error(ErrorCode errorCode) {
        CommonResult<T> result = new CommonResult<>();
        result.setCode(errorCode.getCode());
        result.setMessage(errorCode.getMsg());
        return result;
    }
    
    /**
     * 失败返回结果
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> error(Integer code, String message) {
        CommonResult<T> result = new CommonResult<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 失败返回结果
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> error(String message) {
        return error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(), message);
    }
    
    /**
     * 参数验证失败返回结果
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), message);
    }
    
    /**
     * 未授权返回结果
     * @param <T> 数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> unauthorized() {
        return error(GlobalErrorCodeConstants.UNAUTHORIZED);
    }
    
    /**
     * 未找到返回结果
     * @param <T> 数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> notFound() {
        return error(GlobalErrorCodeConstants.NOT_FOUND);
    }
    
    /**
     * 禁止访问返回结果
     * @param <T> 数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> forbidden() {
        return error(GlobalErrorCodeConstants.FORBIDDEN);
    }
} 