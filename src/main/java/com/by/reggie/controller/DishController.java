package com.by.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.by.reggie.common.R;
import com.by.reggie.dto.DishDto;
import com.by.reggie.entity.Category;
import com.by.reggie.entity.Dish;
import com.by.reggie.entity.DishFlavor;
import com.by.reggie.entity.Employee;
import com.by.reggie.service.CategoryService;
import com.by.reggie.service.DishFlavorService;
import com.by.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaobai
 * @create 2023-05-01 23:45
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /*(批量)起售/停售*/
    @DeleteMapping
    private R<String> delete(@RequestParam List<Long> ids){
        dishService.deleteByIds(ids);
        return R.success("删除成功！");
    }

    /*(批量)起售/停售*/
    @PostMapping("/status/{status}")
    private R<String> status(@PathVariable Integer status,
                             @RequestParam List<Long> ids){
        List<Dish> dishList = dishService.listByIds(ids);
        for (Dish dish : dishList) {
            dish.setStatus(status);
        }
        dishService.updateBatchById(dishList);
        return R.success("菜品状态已经更改成功！");
    }


    /*更新菜品*/
    @PutMapping
    private R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("新增菜品成功");

    }

    /*【页面回显数据】根据id查询菜品信息，和对象的口味信息*/
    @GetMapping("/{id}")
    public R<DishDto>  getById(@PathVariable Long id){
        DishDto dishDto  = dishService.getByIdAndFlavor(id);
        return R.success(dishDto);

    }

    /*分页查询*/
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name)
                .eq(Dish::getIsDeleted,0);

        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝,排除records
        //这里的records实际就是展示的这些信息，但是最终要用的是dishDtoPage里面的记录信息
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        /*自己手动处理records*/
        //获取pageInfo中的记录集合
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            //item的普通属性，拷贝到dishDto
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//每个菜品所对应的分类ID
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            //获取分类名称
            String categoryName = category.getName();
            //在dishDto中设置分类名称
            dishDto.setCategoryName(categoryName);
            return dishDto;

        }).collect(Collectors.toList());


        //上面的stream流，用for循环写法
        /*List<DishDto> list = new ArrayList<>();
        for (Record record : records) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record, dishDto);


            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();

            dishDto.setCategoryName(categoryName);
            list.add(dishDto);

        }*/

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /*新增菜品*/
    @PostMapping
    private R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");

    }



}
