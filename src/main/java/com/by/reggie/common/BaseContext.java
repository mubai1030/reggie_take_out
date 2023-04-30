package com.by.reggie.common;

/**
 * @author xiaobai
 * @create 2023-04-30 13:51
 */
/*基于ThreadLocal封装工具类，用于保存和获取当前登录用户id*/
    //作用范围是一个线程
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /*设置值*/
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /*获取值*/
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
