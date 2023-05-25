package com.by.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.by.reggie.common.BaseContext;
import com.by.reggie.common.R;
import com.by.reggie.dto.DishDto;
import com.by.reggie.entity.*;
import com.by.reggie.service.CategoryService;
import com.by.reggie.service.DishFlavorService;
import com.by.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping("/list")
    private R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList = null;

        //拼key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();//dish_1397844263642378242_1

        //先从Redis中获取缓存数据
        dishDtoList = (List<DishDto> )redisTemplate.opsForValue().get(key);

        if (dishDtoList != null){
            //如果存在，直接返回，无序查询数据库
            return R.success(dishDtoList);
        }

        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis中
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //只查找在售的
        queryWrapper.eq(Dish::getStatus,1).eq(Dish::getIsDeleted,0);
        //排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);
        List<Dish> list = dishService.list(queryWrapper);


        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            //item的普通属性，拷贝到dishDto
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//每个菜品所对应的分类ID
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null){
                //获取分类名称
                String categoryName = category.getName();
                //在dishDto中设置分类名称
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;

        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);


        return R.success(dishDtoList);
    }

/*    @GetMapping("/list")
    private R<List<Dish>> list(Long categoryId){
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.eq(categoryId != null,Dish::getCategoryId,categoryId);
        //只查找在售的
        queryWrapper.eq(Dish::getStatus,1).eq(Dish::getIsDeleted,0);
        //排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }*/

    /*(批量)删除*/
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

        //清理所有菜品的缓存数据
        /*Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);*/

         //清理某个分类下面的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("修改菜品成功");

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

            if (category != null){
                //获取分类名称
                String categoryName = category.getName();
                //在dishDto中设置分类名称
                dishDto.setCategoryName(categoryName);
            }
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

        //清理所有菜品的缓存数据
        /*Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);*/

        //清理某个分类下面的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("新增菜品成功");

    }



}
