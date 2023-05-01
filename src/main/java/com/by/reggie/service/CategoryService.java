package com.by.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.by.reggie.entity.Category;


/**
 * @author xiaobai
 * @create 2023-04-30 14:56
 */
public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
