package com.by.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.by.reggie.entity.OrderDetail;
import com.by.reggie.mapper.OrderDetailMapper;
import com.by.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author xiaobai
 * @create 2023-05-12 10:57
 */
@Service
public class OrderDetailServiceImpl
        extends ServiceImpl<OrderDetailMapper, OrderDetail>
        implements OrderDetailService {
}
