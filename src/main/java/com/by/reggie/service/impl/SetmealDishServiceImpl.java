package com.by.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.by.reggie.entity.SetmealDish;
import com.by.reggie.mapper.SetmealDishMapper;
import com.by.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author xiaobai
 * @create 2023-05-03 22:36
 */
@Service
@Slf4j
public class SetmealDishServiceImpl
        extends ServiceImpl<SetmealDishMapper,SetmealDish>
        implements SetmealDishService {
}
