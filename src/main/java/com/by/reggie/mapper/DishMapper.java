package com.by.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.by.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xiaobai
 * @create 2023-04-30 16:09
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
