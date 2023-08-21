package com.bai.usercenter.service.impl;
import java.util.Date;

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
    //  用户登录态键
    private static final String USER_LOGIN_STATE = "userLoginState";
    //  用户注册
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassWord) {
        /*
        1.校验
         */
        //数据是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassWord)) {
            // todo 修改为自定义异常
            return -1;
        }
        //数据长度是否符合要求
        if (userAccount.length() < 4) {
            return -1;
        }
        if (userPassword.length() < 8 || checkPassWord.length() < 8) {
            return -1;
        }
        //账户不能包含特殊字符
        String validPattern = "\\pP|\\pS|\\s+";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) { //经测试，find()方法表示如果找到字符，则返回true。 改之前为!matcher.find()，导致找到特殊字符后不执行if中的语句
            return -1;
        }
        //密码和校验密码相同
        if (!userPassword.equals(checkPassWord)) {
            return -1;
        }
        //账户不能重复(处理顺序优化，将此部分放在在最后，因为此处进行了数据库查询，若上边的校验不通过，则没必要进行数据库对比，故放在最后)
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        userQueryWrapper.eq("user_account", userAccount); //此处column是表中的映射，改前为userAccount导致SQL语法错误
        queryWrapper.lambda().eq(User::getUserAccount, userAccount); //为避免上述错误，写“user_account”这种硬代码，改为lambda()方法
//        long count = this.count(userQueryWrapper);
        long count = userMapper.selectCount(queryWrapper); //返回值为 查询到的符合要求的数目
        if (count > 0) {
            return -1;
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
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }

    //  用户登录
    @Override
    public User doLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1. 校验
        //数据是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        //数据长度是否符合要求
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        //账户不能包含特殊字符
        String validPattern = "\\pP|\\pS|\\s+";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) { //经测试，find()方法表示如果找到字符，则返回true。 改之前为!matcher.find()，导致找到特殊字符后不执行if中的语句
            return null;
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
            return null;
        }
        //3. 用户信息脱敏。
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUserName(user.getUserName());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        //4. 用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

}




