package com.bai.usercenter.common;

public enum ErrorCode {
    SUCCESS(0, "ok", ""),
    PARAMS_ERROR(4000, "请求参数错误", ""),
    NULL_ERROR(4001, "请求数据为空", ""),
    NOT_LOGIN(4100, "未登录", ""),
    NO_AUTH(4101, "无权限", ""),
    SYSTEM_ERROR(5000, "系统内部异常", "");

    /**
     * 状态码
     */
    private final int code;
    /**
     * 状态码信息
     */
    private final String message;
    /**
     * 状态码描述
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
