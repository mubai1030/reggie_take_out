package com.by.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.by.reggie.entity.ShoppingCart;
import com.by.reggie.mapper.shoppingCartMapper;
import com.by.reggie.service.shoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author xiaobai
 * @create 2023-05-11 23:47
 */
@Service
public class shoppingCartServiceImpl
        extends ServiceImpl<shoppingCartMapper, ShoppingCart>
        implements shoppingCartService {
}
