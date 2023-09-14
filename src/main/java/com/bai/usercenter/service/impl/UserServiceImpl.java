package com.bai.usercenter.service.impl;
import com.bai.usercenter.common.ErrorCode;
import com.bai.usercenter.common.ResultUtils;
import com.bai.usercenter.exception.BusinessException;
import com.bai.usercenter.model.domain.request.UserRegisterRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bai.usercenter.model.domain.User;
import com.bai.usercenter.service.UserService;
import com.bai.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bai.usercenter.contant.UserContant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 * @author bai
 *
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    UserMapper userMapper;
    //  盐
    private static final String SALT = "bai27"; //加盐，在密码字符串前加入一段字符串，增加密码加密的复杂度
    //  用户注册
    @Override
    public long userRegister(String userAccount,String userPassword,String checkPassword,String allowCode) {
        /*
        1.校验
         */
        //数据是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,allowCode)) {
            // todo 修改为自定义异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //数据长度是否符合要求
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码或校验密码过短");
        }
        //注册码
        if (allowCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册码过长");
        }

        //账户不能包含特殊字符
        String validPattern = "\\pP|\\pS|\\s+";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) { //经测试，find()方法表示如果找到字符，则返回true。 改之前为!matcher.find()，导致找到特殊字符后不执行if中的语句
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "存在特殊字符");
        }
        //密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验密码不同");
        }
        //判断注册用户是否有注册码
        if (allowCode.length() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册码为空");
        }
        //注册码是否重复
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getAllowCode, allowCode);
        long count = userMapper.selectCount(lambdaQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册码重复");
        }

        //账户不能重复(处理顺序优化，将此部分放在在最后，因为此处进行了数据库查询，若上边的校验不通过，则没必要进行数据库对比，故放在最后)
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        userQueryWrapper.eq("user_account", userAccount); //此处column是表中的映射，改前为userAccount导致SQL语法错误
        queryWrapper.lambda().eq(User::getUserAccount, userAccount); //为避免上述错误，写“user_account”这种硬代码，改为lambda()方法
//        long count = this.count(userQueryWrapper);
        count = userMapper.selectCount(queryWrapper); //返回值为 查询到的符合要求的数目
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户重复");
        }
        /*
        2.密码加密
         */
//        final String SALT = "bai27"; //加盐，在密码字符串前加入一段字符串，增加密码加密的复杂度
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        /*
        3.插入数据
         */
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setAllowCode(allowCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "插入数据失败");
        }
        return user.getId();
    }

    //  用户登录
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1. 校验
        //数据是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号密码数据异常");
        }
        //数据长度是否符合要求
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        //账户不能包含特殊字符
        String validPattern = "\\pP|\\pS|\\s+";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) { //经测试，find()方法表示如果找到字符，则返回true。 改之前为!matcher.find()，导致找到特殊字符后不执行if中的语句
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号含有特殊字符");
        }
        //2. 密码加密
//        final String SALT = "bai27";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在  **若用户状态为逻辑删除(isDelete属性为删除状态)，是否能查询到;mybatis-plus可以进行配置，只查询没被删除的值
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        queryWrapper.eq(User::getUserPassword, encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        //3. 用户信息脱敏。
        User safetyUser = getSafetyUser(user);
        //4. 用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originUser 需要脱敏的user对象
     * @return 脱敏后的对象
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUserName(originUser.getUserName());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setAllowCode(originUser.getAllowCode());
        return safetyUser;
    }

    /**
     * 用户注销 移除用户的登录态 返回值暂时写作1 没啥用
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /*
    // 鱼皮偷懒直接在controller里写了逻辑，下边是自己写的，但是为了保持项目一直，先按照视频的代码写
    @Override
    public List<User> searchUsers(String userName,HttpServletRequest request) {
        //鉴权 仅管理员可以查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null || user.getRole() != ADMIN_ROLE) {
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userName)) {
            queryWrapper.like("user_name", userName);
        }
        return this.list(queryWrapper);
    }
     */
}




