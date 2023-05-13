package com.by.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.by.reggie.entity.Orders;

/**
 * @author xiaobai
 * @create 2023-05-12 10:45
 */
public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}
