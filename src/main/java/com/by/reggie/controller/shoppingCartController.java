package com.by.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.by.reggie.common.BaseContext;
import com.by.reggie.common.R;
import com.by.reggie.entity.ShoppingCart;
import com.by.reggie.service.shoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xiaobai
 * @create 2023-05-11 23:50
 */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class shoppingCartController {
    @Autowired
    private shoppingCartService  shoppingCartService;

    /*清空购物车*/
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("删除成功");
    }


    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车。。。");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        return R.success(shoppingCartList);
    }

    /*加入购物车*/
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //1、设置用户id，指定当前是哪个用户的购物车数据
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);

        if (dishId != null){
            //添加到购物车的是菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId).eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        }else {
            //添加到购物车的是套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //2、查询当前菜品或者套餐是否在购物车中
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(lambdaQueryWrapper);
        if (shoppingCartOne != null){
            //3、如果已经存在，原来数量基础上加一
            Integer number = shoppingCartOne.getNumber();
            shoppingCartOne.setNumber(number+1);
            shoppingCartService.updateById(shoppingCartOne);
        }else {
            //4、如果不存在，则添加到购物车，数量默认为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCartOne = shoppingCart;
        }

        return R.success(shoppingCartOne);

    }
}
