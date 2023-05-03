package com.by.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.by.reggie.common.CustomException;
import com.by.reggie.dto.DishDto;
import com.by.reggie.entity.Dish;
import com.by.reggie.entity.DishFlavor;
import com.by.reggie.mapper.DishMapper;
import com.by.reggie.service.DishFlavorService;
import com.by.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaobai
 * @create 2023-04-30 16:10
 */
@Service
@Transactional
public class DishServiceImpl
        extends ServiceImpl<DishMapper, Dish>
        implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /*新增菜品，同时保存对应得到口味数据*/
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表
        this.save(dishDto);

        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());


/*        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> updatedFlavors = new ArrayList<>();

        for (DishFlavor item : flavors) {
            item.setDishId(dishId);
            updatedFlavors.add(item);
        }*/


        //保存菜品口味数据到菜品口味表dish_flavor
        //saveBatch集合，批量保存
        dishFlavorService.saveBatch(flavors);

    }

    //根据id查询菜品信息，和对应的口味信息
    @Override
    public DishDto getByIdAndFlavor(Long id) {
        //1、查询菜品基本信息，从dish表中查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);


        //2、查询当前菜品对应的口味信息，从dish_dlavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        //就是查dish和dishflavor然后组合成dishDto

        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //从数据库中删除当前菜品对应口味数据--dish_flavor表得到delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);


        /*添加当前提交过来的口味数据dish_flavor表得到insert操作*/
        //获取页面提交的菜品口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        //设置一个更新后的口味信息集合
        List<DishFlavor> updatedFlavors = new ArrayList<>();

        //遍历每一个口味信息
        for (DishFlavor item : flavors) {
            //给每一个口味信息设置对应的菜品id
            item.setDishId(dishDto.getId());
            //把每一个口味信息添加到更新后的口味信息集合中
            updatedFlavors.add(item);
        }

        dishFlavorService.saveBatch(updatedFlavors);


    }

    @Override
    public void deleteByIds(List<Long> ids) {
        //构造条件查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //先查询该菜品是否在售卖，如果是则抛出业务异常
        queryWrapper.in(ids!=null,Dish::getId,ids);
        List<Dish> list = this.list(queryWrapper);
        for (Dish dish : list) {
            Integer status = dish.getStatus();
            //如果不是在售卖,则可以删除
            if (status == 0){
                this.removeById(dish.getId());
            }else {
                //此时应该回滚,因为可能前面的删除了，但是后面的是正在售卖
                throw new CustomException("删除菜品中有正在售卖菜品,无法全部删除");
            }
        }
    }
}
