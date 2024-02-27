package com.pandaer.pan.server.modules.user.convertor;

import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.user.context.*;
import com.pandaer.pan.server.modules.user.domain.MPanUser;
import com.pandaer.pan.server.modules.user.po.*;
import com.pandaer.pan.server.modules.user.vo.CurrentUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用户模块 各层实体之间的转换器
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    UserRegisterContext PO2ContextInRegister(UserRegisterPO po);

    @Mapping(target = "password",ignore = true)
    MPanUser context2EntityInRegister(UserRegisterContext context);

    UserLoginContext PO2ContextInLogin(UserLoginPO userLoginPO);

    CheckUsernameContext PO2ContextInCheckUsername(CheckUsernamePO checkUsernamePO);

    CheckAnswerContext PO2ContextInCheckAnswer(CheckAnswerPO checkAnswerPO);

    ResetPasswordContext PO2ContextInResetPassword(ResetPasswordPO resetPasswordPO);

    ChangePasswordContext PO2ContextInChangePassword(ChangePasswordPO changePasswordPO);


    @Mapping(source = "userEntity.username",target = "username")
    @Mapping(source = "userFileEntity.fileId",target = "rootFileId")
    @Mapping(source = "userFileEntity.filename",target = "rootFileName")
    CurrentUserVO entity2VOInCurrentUser(MPanUser userEntity, MPanUserFile userFileEntity);
}
