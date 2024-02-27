package com.pandaer.pan.server.modules.user.convertor;

import com.pandaer.pan.server.modules.user.context.UserRegisterContext;
import com.pandaer.pan.server.modules.user.domain.MPanUser;
import com.pandaer.pan.server.modules.user.po.UserRegisterPO;
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
}
