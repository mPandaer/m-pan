package com.pandaer.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.response.ResponseCode;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.core.utils.JwtUtil;
import com.pandaer.pan.core.utils.PasswordUtil;
import com.pandaer.pan.server.common.cache.AnnotationCacheService;
import com.pandaer.pan.server.modules.file.constants.FileConstants;
import com.pandaer.pan.server.modules.file.context.CreateFolderContext;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.user.constants.UserConstants;
import com.pandaer.pan.server.modules.user.context.*;
import com.pandaer.pan.server.modules.user.convertor.UserConverter;
import com.pandaer.pan.server.modules.user.domain.MPanUser;
import com.pandaer.pan.server.modules.user.service.IUserService;
import com.pandaer.pan.server.modules.user.mapper.MPanUserMapper;
import com.pandaer.pan.server.modules.user.service.cache.UserCacheService;
import com.pandaer.pan.server.modules.user.vo.CurrentUserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
* @author pandaer
* @description 针对表【m_pan_user(用户信息表)】的数据库操作Service实现
* @createDate 2024-02-25 18:35:18
*/
@Service("userService")
public class UserServiceImpl extends ServiceImpl<MPanUserMapper, MPanUser>
    implements IUserService {

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private IUserFileService userFileService;

    @Autowired
    private Cache panCache;

    @Autowired
    @Qualifier(value = "userCacheService")
    private AnnotationCacheService<MPanUser> userCacheService;

    public UserServiceImpl() {
        super();
    }

    @Override
    public boolean removeById(Serializable id) {
        return userCacheService.removeById(id);
    }

    @Override
    public boolean updateById(MPanUser entity) {
        return userCacheService.updateById(entity.getUserId(),entity);
    }

    @Override
    public MPanUser getById(Serializable id) {
        return userCacheService.getById(id);
    }

    /**
     * 用户注册具体实现
     * 1.创建用户信息
     * 2.创建用户根目录信息
     * @param context
     * @return
     */
    @Override
    public Long register(UserRegisterContext context) {
        assembleEntityInContext(context);
        doRegisterWithContext(context);
        createUserRootFolderWithContext(context);//利用上下文对象创建用户根目录信息
        return context.getEntity().getUserId();
    }


    /**
     * 用户登录业务实现
     * 1.根据用户名查询用户实体，判断密码是否一致
     * 2.登陆成功后，生成有时效性的AccessToken
     * 3.将AccessToken放入到缓存中 实现单机登录
     * @param context
     * @return
     */
    @Override
    public String login(UserLoginContext context) {
        validLoginInfo(context);
        genAndSaveAccessToken(context);
        return context.getAccessToken();
    }


    //坚持是否已经登录
    private boolean checkLogined(UserLoginContext context) {
        String token = panCache.get(UserConstants.CACHE_LOGIN_USER_ID_PREFIX +
                context.getEntity().getUserId(), String.class);
        return token != null;
    }

    // 清除登录信息凭证
    @Override
    public void exit(Long userId) {
        panCache.evict(UserConstants.CACHE_LOGIN_USER_ID_PREFIX + userId);
    }

    @Override
    public String checkUsername(CheckUsernameContext context) {
        MPanUser userEntity = getUserByName(context.getUsername());
        if (userEntity == null) {
            throw new MPanBusinessException("用户不存在");
        }
        String question = userEntity.getQuestion();
        if (StringUtils.isBlank(question)) {
            throw new MPanBusinessException("没有设置密保问题");
        }
        return question;
    }

    @Override
    public String checkAnswer(CheckAnswerContext context) {
        MPanUser userEntity = getUserByName(context.getUsername());
        if (userEntity == null) {
            throw new MPanBusinessException("用户不存在");
        }
        if (!Objects.equals(context.getAnswer(),userEntity.getAnswer())) {
            throw new MPanBusinessException("密保答案不正确");
        }
        return JwtUtil.generateToken(userEntity.getUsername(), UserConstants.FORGET_USER_ID_KEY
                , userEntity.getUserId(), UserConstants.FIVE_MIN_TIME_LONG);
    }


    /**
     * 校验token是否合法
     * 解析token并修改信息
     * @param context
     */
    @Override
    public void resetPassword(ResetPasswordContext context) {
        checkAndParseToken(context);
        updatePasswordInReset(context);
    }

    /**
     * 校验旧密码
     * 更新密码
     * 退出登录
     * @param context
     */
    @Override
    public void changePassword(ChangePasswordContext context) {
        checkOldPassword(context);
        updatePasswordInChange(context);
        exit(context.getUserId());
    }

    /**
     * 获取当前用户的基本信息
     * 获取对应的根目录基本信息
     * 封装成VO对象返回
     * @param userId
     * @return
     */
    @Override
    public CurrentUserVO getCurrentUser(Long userId) {
        MPanUser userEntity = getById(userId);
        if (Objects.isNull(userEntity)) {
            throw new MPanBusinessException("当前用户不存在");
        }
        MPanUserFile rootFileEntity = getRootFile(userId);
        if (Objects.isNull(rootFileEntity)) {
            throw new MPanBusinessException("当前用户的根目录不存在");
        }
        return userConverter.entity2VOInCurrentUser(userEntity,rootFileEntity);

    }

    private MPanUserFile getRootFile(Long userId) {
        return userFileService.getRootUserFileByUserId(userId);
    }

    private void updatePasswordInChange(ChangePasswordContext context) {
        MPanUser entity = context.getEntity();
        String newPassword = PasswordUtil.encryptPassword(entity.getSalt(),context.getNewPassword());
        entity.setPassword(newPassword);
        if (!updateById(entity)) {
            throw new MPanBusinessException("更新密码失败");
        }
    }

    private void checkOldPassword(ChangePasswordContext context) {
        Long userId = context.getUserId();
        MPanUser userEntity = getById(userId);
        if (userEntity == null) {
            throw new MPanBusinessException("用户未登录");
        }
        String cryptoPassword = PasswordUtil.encryptPassword(userEntity.getSalt(),context.getOldPassword());
        if (!Objects.equals(userEntity.getPassword(),cryptoPassword)) {
            throw new MPanBusinessException("旧密码不正确");
        }
        context.setEntity(userEntity);
    }

    private void updatePasswordInReset(ResetPasswordContext context) {
        Long userId = context.getUserId();
        MPanUser userEntity = getById(userId);
        if (!Objects.equals(userEntity.getUsername(),context.getUsername())) {
            throw new MPanBusinessException("重置密码用户名不正确");
        }

        String newPassword = context.getPassword();
        userEntity.setPassword(PasswordUtil.encryptPassword(userEntity.getSalt(),newPassword));
        if (!updateById(userEntity)) {
            throw new MPanBusinessException("重置密码失败");
        }
    }

    private void checkAndParseToken(ResetPasswordContext context) {
        String requestToken = context.getToken();
        Long userId = (Long) JwtUtil.analyzeToken(requestToken, UserConstants.FORGET_USER_ID_KEY);
        if (userId == null) {
            throw new MPanBusinessException("Token不合法");
        }
        context.setUserId(userId);
    }

    private void genAndSaveAccessToken(UserLoginContext context) {
        String accessToken = JwtUtil.generateToken(context.getUsername(), UserConstants.LOGIN_USER_ID_KEY,
                context.getEntity().getUserId(), UserConstants.ONE_DAY_TIME_LONG);
        panCache.put(UserConstants.CACHE_LOGIN_USER_ID_PREFIX + context.getEntity().getUserId(),accessToken);
        context.setAccessToken(accessToken);
    }

    private void validLoginInfo(UserLoginContext context) {
        String username = context.getUsername();
        String password = context.getPassword();
        MPanUser userEntity = getUserByName(username);
        if (userEntity == null)
            throw new MPanBusinessException("用户不存在");
        String cryptPassword = PasswordUtil.encryptPassword(userEntity.getSalt(),password);
        if (!StringUtils.equals(cryptPassword,userEntity.getPassword()))
            throw new MPanBusinessException("密码不正确");
        context.setEntity(userEntity);
    }

    private MPanUser getUserByName(String username) {
        LambdaQueryWrapper<MPanUser> query = new LambdaQueryWrapper<>();
        query.eq(MPanUser::getUsername,username);
        return getOne(query);
    }

    private void createUserRootFolderWithContext(UserRegisterContext context) {
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(FileConstants.ROOT_FOLDER_PARENT_ID);
        createFolderContext.setFolderName(FileConstants.ROOT_FOLDER_NAME_CN);
        createFolderContext.setUserId(context.getEntity().getUserId());
        userFileService.creatFolder(createFolderContext);
    }

    //利用上下文对象创建用户信息
    private void doRegisterWithContext(UserRegisterContext context) {
        MPanUser entity = context.getEntity();
        if (Objects.isNull(entity)) {
            throw new MPanBusinessException(ResponseCode.ERROR);
        }
        try {
            if (!save(entity)) {
                throw new MPanBusinessException("用户注册失败");
            }
        } catch (DuplicateKeyException e) {
            throw new MPanBusinessException("用户已经存在");
        }
    }

    //组装用户实体到上下文中
    private void assembleEntityInContext(UserRegisterContext context) {
        MPanUser entity = userConverter.context2EntityInRegister(context);
        entity.setUserId(IdUtil.get());
        String salt = PasswordUtil.getSalt();
        String encryptPassword = PasswordUtil.encryptPassword(salt,context.getPassword());
        entity.setPassword(encryptPassword);
        entity.setSalt(salt);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        context.setEntity(entity); //放入上下文对象中
    }
}




