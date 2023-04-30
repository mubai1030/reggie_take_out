package com.by.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.by.reggie.common.CustomException;
import com.by.reggie.entity.Category;
import com.by.reggie.entity.Dish;
import com.by.reggie.entity.Setmeal;
import com.by.reggie.mapper.CategoryMapper;
import com.by.reggie.service.CategoryService;
import com.by.reggie.service.DishService;
import com.by.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaobai
 * @create 2023-04-30 14:56
 */
@Service
public class CategoryServiceImpl
        extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {

        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        long dishCount = dishService.count(dishLambdaQueryWrapper);
        if (dishCount  > 0){
            //已经关联，抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常、
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        long setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if (setmealCount  > 0){
            //已经关联，抛出异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }


        //正常删除
        super.removeById(id);

    }
}
