package com.bai.usercenter.service;
import java.util.Date;

import com.bai.usercenter.model.domain.User;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 用户服务测试
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testAddUser(){
        User user = new User();
        user.setUserName("bai3");
        user.setUserAccount("123");
        user.setAvatarUrl("https://pic2.zhimg.com/80/v2-cef1bd681556b3352f3b8bea15d4e0fd_1440w.webp");
        user.setUserPassword("xxx");
        user.setGender(0);
        user.setPhone("123");
        user.setEmail("456");
        user.setUserStatus(0);
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "yupi";
        String userPassword = "";
        String checkPassword = "123456";
        String allowCode = "123";
        //数据为空
        long result = userService.userRegister(userAccount, userPassword, checkPassword,allowCode);
        Assertions.assertEquals(-1,result);
        //账号长度小于4
        userAccount = "yu";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,allowCode);
        Assertions.assertEquals(-1,result);
        //密码长度小于8
        userAccount = "yupi";
        result = userService.userRegister(userAccount, userPassword, checkPassword,allowCode);
        Assertions.assertEquals(-1,result);
        //账号含特殊字符
        userAccount = "yu pi";
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword,allowCode);
        Assertions.assertEquals(-1,result);
        //密码与校验密码不同
        userAccount = "yupi";
        result = userService.userRegister(userAccount, userPassword, checkPassword,allowCode);
        Assertions.assertEquals(-1,result);
        //账号存在
        userAccount = "baijin";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,allowCode);
        Assertions.assertEquals(-1,result);
        //注册码为空
        allowCode = "";
        result = userService.userRegister(userAccount, userPassword, checkPassword, allowCode);
        Assertions.assertEquals(-1,result);
        //注册码存在
        allowCode = "123";
        result = userService.userRegister(userAccount, userPassword, checkPassword, allowCode);
        Assertions.assertEquals(-1,result);
        //成功
        userAccount = "yupi3";
        allowCode = "1234";
        result = userService.userRegister(userAccount, userPassword, checkPassword,allowCode);
        Assertions.assertTrue(result > 0);
    }
}