package com.by.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.by.reggie.entity.Setmeal;
import com.by.reggie.mapper.SetmealMapper;
import com.by.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

/**
 * @author xiaobai
 * @create 2023-04-30 16:13
 */
@Service
public class SetmealServiceImpl
        extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {
}
