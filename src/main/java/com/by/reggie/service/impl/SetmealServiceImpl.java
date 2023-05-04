package com.by.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.by.reggie.common.CustomException;
import com.by.reggie.dto.SetmealDto;
import com.by.reggie.entity.Setmeal;
import com.by.reggie.entity.SetmealDish;
import com.by.reggie.mapper.SetmealMapper;
import com.by.reggie.service.SetmealDishService;
import com.by.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaobai
 * @create 2023-04-30 16:13
 */
@Service
public class SetmealServiceImpl
        extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /*新增套餐，同时需要保存套餐和菜品的关联关系*/
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);
        //从setmealDishes中获取所有的SetmealDishe
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //遍历每一个SetmealDishe，给他们加上setmealId
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联关系，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);

    }

    /*删除套餐，同时需要删除套餐和菜品的关联数据*/
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐的状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        long count = this.count(queryWrapper);
        if (count > 0){
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //如果可以删除，先删除setmeal_dish中的数据
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);

        //删除setmeal中的数据
        this.removeByIds(ids);

    }
}
