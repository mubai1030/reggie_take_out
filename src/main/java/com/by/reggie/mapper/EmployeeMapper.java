package com.by.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.by.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xiaobai
 * @create 2023-03-09 21:59
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
