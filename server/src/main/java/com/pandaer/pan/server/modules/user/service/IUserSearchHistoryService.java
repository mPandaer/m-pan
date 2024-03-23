package com.pandaer.pan.server.modules.user.service;

import com.pandaer.pan.server.modules.user.context.UserHistoryContext;
import com.pandaer.pan.server.modules.user.domain.MPanUserSearchHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pandaer.pan.server.modules.user.vo.UserHistoryVO;

import java.util.List;

/**
* @author pandaer
* @description 针对表【m_pan_user_search_history(用户搜索历史表)】的数据库操作Service
* @createDate 2024-02-25 18:35:18
*/
public interface IUserSearchHistoryService extends IService<MPanUserSearchHistory> {

    List<UserHistoryVO> queryUserHistory(UserHistoryContext context);
}
