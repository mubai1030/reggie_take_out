package com.by.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.by.reggie.dto.DishDto;
import com.by.reggie.entity.Dish;

import java.util.List;

/**
 * @author xiaobai
 * @create 2023-04-30 16:09
 */
public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表，dish、dish_flavor
    void saveWithFlavor(DishDto dishDto);

    //据id查询菜品信息，和对应的口味信息
    DishDto getByIdAndFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);

    void deleteByIds(List<Long> ids);
}
