package com.bai.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author bai
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 5501047346182754183L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String allowCode;

}
