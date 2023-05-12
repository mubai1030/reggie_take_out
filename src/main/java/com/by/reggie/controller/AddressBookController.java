package com.by.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.by.reggie.common.BaseContext;
import com.by.reggie.common.R;
import com.by.reggie.entity.AddressBook;
import com.by.reggie.service.AddressBookService;
import javafx.scene.chart.BubbleChart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.events.Event;

import java.util.List;

/**
 * @author xiaobai
 * @create 2023-05-11 10:48
 */
@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /*保存地址信息*/
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook :{}",addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /*获取默认地址*/
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if (null == addressBook){
            return R.error("没有找到该对象");
        }
        return R.success(addressBook);
    }

    /*设置默认地址*/
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> Wrapper = new LambdaUpdateWrapper<>();
        //1、获取当前登录用户的所有地址
        Wrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        //2、把所有的地址默认值都改为0
        Wrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(Wrapper);
        //3、单独改当前传递过来的地址为1
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }



    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        /*AddressBook addressBook = new AddressBook();
        log.info("addressBook:{}",addressBook);*/

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseContext.getCurrentId() != null,AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

}




















