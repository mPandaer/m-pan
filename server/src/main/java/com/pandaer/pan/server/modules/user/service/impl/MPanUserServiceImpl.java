package com.pandaer.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.pan.server.modules.user.domain.MPanUser;
import com.pandaer.pan.server.modules.user.service.MPanUserService;
import com.pandaer.pan.server.modules.user.mapper.MPanUserMapper;
import org.springframework.stereotype.Service;

/**
* @author pandaer
* @description 针对表【m_pan_user(用户信息表)】的数据库操作Service实现
* @createDate 2024-02-25 18:35:18
*/
@Service
public class MPanUserServiceImpl extends ServiceImpl<MPanUserMapper, MPanUser>
    implements MPanUserService{

}




