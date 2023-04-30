package com.by.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.by.reggie.common.R;
import com.by.reggie.entity.Employee;
import com.by.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.naming.Name;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author xiaobai
 * @create 2023-03-09 22:07
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }

    /*启用/禁用*/
    @PutMapping
    public R<String> update(HttpServletRequest request,
                            @RequestBody Employee employee){
        log.info("employee{}" + employee.toString());

        //公共字段统一处理
        /*
        //获取设置更新的管理员Id[这里的id会丢失精度，引入JacksonObjectMapper]
        Long setUpdateUserId= (Long)request.getSession().getAttribute("employee");
        log.info("empId = " + setUpdateUserId);

        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(setUpdateUserId);*/
        employeeService.updateById(employee);

        return R.success("更新成功");
    }

    /*分页*/
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /*新增员工*/
    @PostMapping
    public R<String> save(HttpServletRequest request,
                          @RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());
        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);

        //公共字段，这里注释了，统一在MetaObjectHandler处理
       /* employee.setCreateTime(LocalDateTime.now());//创建时间
        employee.setUpdateTime(LocalDateTime.now());//更新时间

        //获取当前登录用户的id
        Long  empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);//创建人
        employee.setUpdateUser(empId);//更新人*/

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /*登录方法*/
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,
                             @RequestBody Employee employee){
        /**
         * 处理逻辑如下：
         * ①. 将页面提交的密码password进行md5加密处理, 得到加密后的字符串
         * ②. 根据页面提交的用户名username查询数据库中员工数据信息
         * ③. 如果没有查询到, 则返回登录失败结果
         * ④. 密码比对，如果不一致, 则返回登录失败结果
         * ⑤. 查看员工状态，如果为已禁用状态，则返回员工已禁用结果
         * ⑥. 登录成功，将员工id存入Session, 并返回登录成功结果*/

//        ①. 将页面提交的密码password进行md5加密处理, 得到加密后的字符串
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        ②. 根据页面提交的用户名username查询数据库中员工数据信息
        //包装一个查询对象
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //等值查询
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
//        ③. 如果没有查询到, 则返回登录失败结果
        if (emp == null){
            return R.error("登录失败");
        }
//        ④. 密码比对，如果不一致, 则返回登录失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }
//        ⑤. 查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0){
            return R.error("账号异常");
        }
//        ⑥. 登录成功，将员工id存入Session, 并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /*退出方法*/
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
//        清理Session中保存的当前登录员工的id【前端用的本地存储，退出前清理一下】
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
}
