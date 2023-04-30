package com.by.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author xiaobai
 * @create 2023-04-30 11:14
 */
/*自定义元数据对象处理器*/
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    /*插入操作自动填充*/
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充【insert】");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        //通过BaseContext获取当前登录用户的id【过滤器中设置完，传过来的】
        Long empId = BaseContext.getCurrentId();
        metaObject.setValue("createUser", empId);
        metaObject.setValue("updateUser", empId);
    }


    /*更新操作自动填充*/
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充【update】");
        log.info(metaObject.toString());

        /*long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);*/

        metaObject.setValue("updateTime", LocalDateTime.now());

        //通过BaseContext获取当前登录用户的id【过滤器中设置完，传过来的】
        Long empId = BaseContext.getCurrentId();
        metaObject.setValue("updateUser", empId);
    }
}
