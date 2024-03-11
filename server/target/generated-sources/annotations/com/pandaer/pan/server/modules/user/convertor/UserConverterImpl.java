package com.pandaer.pan.server.modules.user.convertor;

import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.user.context.ChangePasswordContext;
import com.pandaer.pan.server.modules.user.context.CheckAnswerContext;
import com.pandaer.pan.server.modules.user.context.CheckUsernameContext;
import com.pandaer.pan.server.modules.user.context.ResetPasswordContext;
import com.pandaer.pan.server.modules.user.context.UserLoginContext;
import com.pandaer.pan.server.modules.user.context.UserRegisterContext;
import com.pandaer.pan.server.modules.user.domain.MPanUser;
import com.pandaer.pan.server.modules.user.po.ChangePasswordPO;
import com.pandaer.pan.server.modules.user.po.CheckAnswerPO;
import com.pandaer.pan.server.modules.user.po.CheckUsernamePO;
import com.pandaer.pan.server.modules.user.po.ResetPasswordPO;
import com.pandaer.pan.server.modules.user.po.UserLoginPO;
import com.pandaer.pan.server.modules.user.po.UserRegisterPO;
import com.pandaer.pan.server.modules.user.vo.CurrentUserVO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-03-11T18:22:40+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 1.8.0_402 (Oracle Corporation)"
)
@Component
public class UserConverterImpl implements UserConverter {

    @Override
    public UserRegisterContext PO2ContextInRegister(UserRegisterPO po) {
        if ( po == null ) {
            return null;
        }

        UserRegisterContext userRegisterContext = new UserRegisterContext();

        userRegisterContext.setUsername( po.getUsername() );
        userRegisterContext.setPassword( po.getPassword() );
        userRegisterContext.setQuestion( po.getQuestion() );
        userRegisterContext.setAnswer( po.getAnswer() );

        return userRegisterContext;
    }

    @Override
    public MPanUser context2EntityInRegister(UserRegisterContext context) {
        if ( context == null ) {
            return null;
        }

        MPanUser mPanUser = new MPanUser();

        mPanUser.setUsername( context.getUsername() );
        mPanUser.setQuestion( context.getQuestion() );
        mPanUser.setAnswer( context.getAnswer() );

        return mPanUser;
    }

    @Override
    public UserLoginContext PO2ContextInLogin(UserLoginPO userLoginPO) {
        if ( userLoginPO == null ) {
            return null;
        }

        UserLoginContext userLoginContext = new UserLoginContext();

        userLoginContext.setUsername( userLoginPO.getUsername() );
        userLoginContext.setPassword( userLoginPO.getPassword() );

        return userLoginContext;
    }

    @Override
    public CheckUsernameContext PO2ContextInCheckUsername(CheckUsernamePO checkUsernamePO) {
        if ( checkUsernamePO == null ) {
            return null;
        }

        CheckUsernameContext checkUsernameContext = new CheckUsernameContext();

        checkUsernameContext.setUsername( checkUsernamePO.getUsername() );

        return checkUsernameContext;
    }

    @Override
    public CheckAnswerContext PO2ContextInCheckAnswer(CheckAnswerPO checkAnswerPO) {
        if ( checkAnswerPO == null ) {
            return null;
        }

        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();

        checkAnswerContext.setUsername( checkAnswerPO.getUsername() );
        checkAnswerContext.setAnswer( checkAnswerPO.getAnswer() );

        return checkAnswerContext;
    }

    @Override
    public ResetPasswordContext PO2ContextInResetPassword(ResetPasswordPO resetPasswordPO) {
        if ( resetPasswordPO == null ) {
            return null;
        }

        ResetPasswordContext resetPasswordContext = new ResetPasswordContext();

        resetPasswordContext.setPassword( resetPasswordPO.getPassword() );
        resetPasswordContext.setUsername( resetPasswordPO.getUsername() );
        resetPasswordContext.setToken( resetPasswordPO.getToken() );

        return resetPasswordContext;
    }

    @Override
    public ChangePasswordContext PO2ContextInChangePassword(ChangePasswordPO changePasswordPO) {
        if ( changePasswordPO == null ) {
            return null;
        }

        ChangePasswordContext changePasswordContext = new ChangePasswordContext();

        changePasswordContext.setOldPassword( changePasswordPO.getOldPassword() );
        changePasswordContext.setNewPassword( changePasswordPO.getNewPassword() );

        return changePasswordContext;
    }

    @Override
    public CurrentUserVO entity2VOInCurrentUser(MPanUser userEntity, MPanUserFile userFileEntity) {
        if ( userEntity == null && userFileEntity == null ) {
            return null;
        }

        CurrentUserVO currentUserVO = new CurrentUserVO();

        if ( userEntity != null ) {
            currentUserVO.setUsername( userEntity.getUsername() );
        }
        if ( userFileEntity != null ) {
            currentUserVO.setRootFileId( userFileEntity.getFileId() );
            currentUserVO.setRootFileName( userFileEntity.getFilename() );
        }

        return currentUserVO;
    }
}
