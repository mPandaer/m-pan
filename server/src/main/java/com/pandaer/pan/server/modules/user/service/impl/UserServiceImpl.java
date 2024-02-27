package com.pandaer.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.response.ResponseCode;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.core.utils.PasswordUtil;
import com.pandaer.pan.server.modules.file.constants.FileConstants;
import com.pandaer.pan.server.modules.file.context.CreateFolderContext;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.user.context.UserRegisterContext;
import com.pandaer.pan.server.modules.user.convertor.UserConverter;
import com.pandaer.pan.server.modules.user.domain.MPanUser;
import com.pandaer.pan.server.modules.user.service.IUserService;
import com.pandaer.pan.server.modules.user.mapper.MPanUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

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




