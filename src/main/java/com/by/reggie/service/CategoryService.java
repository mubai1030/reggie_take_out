package com.by.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.by.reggie.entity.Category;

import javax.jnlp.BasicService;

/**
 * @author xiaobai
 * @create 2023-04-30 14:56
 */
public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
