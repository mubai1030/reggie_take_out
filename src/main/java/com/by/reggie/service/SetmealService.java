package com.by.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.by.reggie.dto.SetmealDto;
import com.by.reggie.entity.Setmeal;

import java.util.List;

/**
 * @author xiaobai
 * @create 2023-04-30 16:13
 */
public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    /*删除套餐，同时需要删除套餐和菜品的关联数据*/
    void removeWithDish(List<Long> ids);
}
