package com.bai.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
//    @TableField("user_name")
    private String userName;

    /**
     * 用户账户
     */
//    @TableField(value = "user_account")
    private String userAccount;

    /**
     * 用户头像
     */
    @TableField(value = "avatarUrl")
    private String avatarUrl;

    /**
     * 用户密码
     */
//    @TableField(value = "user_password")
    private String userPassword;

    /**
     * 性别
     */
//    @TableField(value = "gender")
    private Integer gender;

    /**
     * 手机号
     */
//    @TableField(value = "phone")
    private String phone;

    /**
     * 邮箱
     */
//    @TableField(value = "email")
    private String email;


    /**
     * 用户状态(正常、封禁等，用0，1等数字表示)
     */
//    @TableField(value = "user_status")
    private Integer userStatus;

    /**
     * 创建时间
     */
//    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
//    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 是否删除(逻辑删除。为了保护数据，设置0，1表示该条数据是否删除)
     */
//    @TableField(value = "isDelete")
    @TableLogic //设置逻辑删除，
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}