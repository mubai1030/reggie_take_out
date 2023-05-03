package com.by.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.by.reggie.entity.DishFlavor;
import com.by.reggie.mapper.DishFlavorMapper;
import com.by.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @author xiaobai
 * @create 2023-05-01 23:42
 */
@Service
public class DishFlavorServiceImpl
        extends ServiceImpl<DishFlavorMapper, DishFlavor>
        implements DishFlavorService {
}
