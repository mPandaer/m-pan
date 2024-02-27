package com.pandaer.pan.server.modules.user.convertor;

import com.pandaer.pan.server.modules.user.context.UserLoginContext;
import com.pandaer.pan.server.modules.user.context.UserRegisterContext;
import com.pandaer.pan.server.modules.user.domain.MPanUser;
import com.pandaer.pan.server.modules.user.po.UserLoginPO;
import com.pandaer.pan.server.modules.user.po.UserRegisterPO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-02-27T12:31:10+0800",
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
}
