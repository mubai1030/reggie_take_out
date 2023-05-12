package com.by.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.by.reggie.entity.AddressBook;
import com.by.reggie.mapper.AddressBookMapper;
import com.by.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author xiaobai
 * @create 2023-05-11 10:46
 */
@Service
public class AddressBookServiceImpl
        extends ServiceImpl<AddressBookMapper, AddressBook>
        implements AddressBookService {
}
