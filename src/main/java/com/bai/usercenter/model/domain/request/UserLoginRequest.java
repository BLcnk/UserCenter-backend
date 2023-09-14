package com.bai.usercenter.model.domain.request;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author bai
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -6039239202230789444L;

    private String userAccount;
    private String userPassword;

}
