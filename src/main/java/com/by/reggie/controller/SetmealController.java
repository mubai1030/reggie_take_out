package com.by.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.by.reggie.common.R;
import com.by.reggie.dto.SetmealDto;
import com.by.reggie.entity.Category;
import com.by.reggie.entity.Dish;
import com.by.reggie.entity.Setmeal;
import com.by.reggie.entity.SetmealDish;
import com.by.reggie.service.CategoryService;
import com.by.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaobai
 * @create 2023-05-03 22:03
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    /*(批量)删除*/
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info(ids.toString());
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    /*(批量)起售/停售*/
    @PostMapping("/status/{status}")
    private R<String> status(@PathVariable Integer status,
                             @RequestParam List<Long> ids){
        List<Setmeal> setmealList = setmealService.listByIds(ids);
        for (Setmeal setmeal : setmealList) {
            setmeal.setStatus(status);
        }
        setmealService.updateBatchById(setmealList);
        return R.success("套餐状态已经更改成功！");
    }

    /*新增保存*/
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /*分页查询*/
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPageInfo = new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加一个查询条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        //添加一个排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPageInfo,"records");

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPageInfo.setRecords(list);

        return R.success(dtoPageInfo);
    }

}
