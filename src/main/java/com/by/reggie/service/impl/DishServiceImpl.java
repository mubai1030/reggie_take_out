package com.by.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.by.reggie.entity.Dish;
import com.by.reggie.mapper.DishMapper;
import com.by.reggie.service.DishService;
import org.springframework.stereotype.Service;

/**
 * @author xiaobai
 * @create 2023-04-30 16:10
 */
@Service
public class DishServiceImpl
        extends ServiceImpl<DishMapper, Dish>
        implements DishService {
}
