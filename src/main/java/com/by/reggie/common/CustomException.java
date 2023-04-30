package com.by.reggie.common;

/**
 * @author xiaobai
 * @create 2023-04-30 16:33
 */
/*自定义业务异常*/
public class CustomException extends RuntimeException {
    public CustomException(String message){
        super(message);
    }
}
