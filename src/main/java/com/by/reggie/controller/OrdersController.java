package com.by.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.by.reggie.common.R;
import com.by.reggie.entity.Orders;
import com.by.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author xiaobai
 * @create 2023-05-12 11:01
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @PostMapping("/submit")
    public R<String> sunmit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> page(Integer page,Integer pageSize){
        return null;
    }

}
