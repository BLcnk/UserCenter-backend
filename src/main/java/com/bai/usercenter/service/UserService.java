package com.bai.usercenter.service;

import com.bai.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.http.HttpRequest;

/**
* @author bai
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-08-17 17:53:08
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册（前端所有的校验都可以被绕开，所以后端都需要全部重新校验）
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassWord 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassWord);

    /**
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User doLogin(String userAccount, String userPassword, HttpServletRequest request);
}
