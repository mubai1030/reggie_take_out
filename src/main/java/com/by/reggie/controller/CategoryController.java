package com.by.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.by.reggie.common.R;
import com.by.reggie.entity.Category;
import com.by.reggie.entity.Employee;
import com.by.reggie.service.CategoryService;
import com.sun.media.jfxmedia.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xiaobai
 * @create 2023-04-30 15:01
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /*修改*/
    @PutMapping
    public R<String> update(HttpServletRequest request,
                            @RequestBody Category category){
        log.info("category{}" + category.toString());

        categoryService.updateById(category);

        return R.success("修改分类信息成功");
    }

    /*根据id删除分类*/
    @DeleteMapping
    public R<String> delete(Long id){
        log.info("id = " + id);
        /*service实现类写了remove方法*/
        categoryService.remove(id);
        return R.success("删除成功！");
    }

    /*分页*/
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){

        //构造分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();

        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort);

        //执行查询
        categoryService.page(pageInfo,queryWrapper);


        return R.success(pageInfo);
    }

    /*新增分类*/
    @PostMapping
    public R<String> save(HttpServletRequest request,
                            @RequestBody Category category){
        log.info("category：+{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

}
