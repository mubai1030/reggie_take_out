package com.by.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.by.reggie.entity.Employee;
import com.by.reggie.mapper.EmployeeMapper;
import com.by.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author xiaobai
 * @create 2023-03-09 22:05
 */
@Service
public class EmployeeServiceImpl
        extends ServiceImpl<EmployeeMapper, Employee>
        implements EmployeeService {

}
