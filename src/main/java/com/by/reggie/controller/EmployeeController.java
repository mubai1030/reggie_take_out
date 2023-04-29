package com.by.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.by.reggie.common.R;
import com.by.reggie.entity.Employee;
import com.by.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
