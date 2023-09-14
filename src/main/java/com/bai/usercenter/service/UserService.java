package com.bai.usercenter.service;

import com.bai.usercenter.contant.UserContant;
import com.bai.usercenter.model.domain.User;
import com.bai.usercenter.model.domain.request.UserRegisterRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author bai
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2023-08-17 17:53:08
 */
public interface UserService extends IService<User> {


    /**
     * 用户注册（前端所有的校验都可以被绕开，所以后端都需要全部重新校验）
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param allowCode 用户注册码--有这个码才允许注册
     * @return 新用户 id
     */
    long userRegister(String userAccount,String userPassword,String checkPassword,String allowCode);

    /**
     * 登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户对象
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户信息脱敏
     * @param originUser 需要脱敏的用户对象
     * @return 脱敏后的用户对象
     */
    User getSafetyUser(User originUser);

    /**
     * 根据用户姓名查询
     *
     * @param userName 用户姓名
     * @return 查询到的所有用户
     */
//    List<User> searchUsers(String userName, HttpServletRequest request);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

}
