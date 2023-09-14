package com.bai.usercenter.controller;

import com.bai.usercenter.common.BaseResponse;
import com.bai.usercenter.common.ErrorCode;
import com.bai.usercenter.common.ResultUtils;
import com.bai.usercenter.exception.BusinessException;
import com.bai.usercenter.model.domain.User;
import com.bai.usercenter.model.domain.request.UserLoginRequest;
import com.bai.usercenter.model.domain.request.UserRegisterRequest;
import com.bai.usercenter.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.bai.usercenter.contant.UserContant.ADMIN_ROLE;
import static com.bai.usercenter.contant.UserContant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author bai
 */
@RestController //适用于编写restful风格的api，返回值默认为json类型
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
//            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String allowCode = userRegisterRequest.getAllowCode();
//        UserRegisterRequest user = new UserRegisterRequest();
//        user.setUserAccount(userRegisterRequest.getUserAccount());
//        user.setUserPassword(userRegisterRequest.getUserPassword());
//        user.setCheckPassword(userRegisterRequest.getCheckPassword());
//        user.setAllowCode(userRegisterRequest.getAllowCode());
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,allowCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册账号、密码、注册码有空值");
        }
        long userId = userService.userRegister(userAccount, userPassword, checkPassword, allowCode);
        return ResultUtils.success(userId);
    }

    @RequestMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号密码存在空值");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        int logout = userService.userLogout(request);
        return ResultUtils.success(logout);
    }

    /**
     *  用户登录之后，再次打开时，不用重新登录
     * @param request 用户缓存
     * @return 脱敏后的用户信息
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentuser = (User) userObj;
        if (currentuser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentuser.getId();
        //TODO 校验用户是否合法 用户是否封号等等
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers( String userName, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(userName)) {
            queryWrapper.like(User::getUserName, userName);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "没有权限");
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id数据异常");
        }
        //这里直接使用userService接口中定义的removeById方法，没有写删除逻辑
        boolean remove = userService.removeById(id);
        return ResultUtils.success(remove);
    }

    private boolean isAdmin(HttpServletRequest request){
        //鉴权 仅管理员可以查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
