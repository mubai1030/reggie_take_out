package com.by.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.by.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xiaobai
 * @create 2023-05-04 21:11
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
