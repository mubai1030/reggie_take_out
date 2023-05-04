package com.by.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.by.reggie.entity.User;
import com.by.reggie.mapper.UserMapper;
import com.by.reggie.service.UserServie;
import org.springframework.stereotype.Service;

/**
 * @author xiaobai
 * @create 2023-05-04 21:13
 */
@Service
public class UserServiceImpl
        extends ServiceImpl<UserMapper, User>
        implements UserServie {
}
