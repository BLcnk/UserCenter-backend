package com.bai.usercenter.contant;

/**
 * 用户常量类
 *
 * @author bai
 */
public interface UserContant {
    /**
     * 用户登录态键 原本放在实现类中
     */
    String USER_LOGIN_STATE = "userLoginState";
    /**
     * 角色权限 0-普通用户
     */
    int DEFAULT_ROLE = 0;
    /**
     * 角色权限 1-管理员
     */
    int ADMIN_ROLE = 1;
}
