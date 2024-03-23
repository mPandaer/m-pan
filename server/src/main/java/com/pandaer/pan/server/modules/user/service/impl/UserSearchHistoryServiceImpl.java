package com.pandaer.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.pan.server.modules.user.context.UserHistoryContext;
import com.pandaer.pan.server.modules.user.domain.MPanUserSearchHistory;
import com.pandaer.pan.server.modules.user.service.IUserSearchHistoryService;
import com.pandaer.pan.server.modules.user.mapper.MPanUserSearchHistoryMapper;
import com.pandaer.pan.server.modules.user.vo.UserHistoryVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author pandaer
* @description 针对表【m_pan_user_search_history(用户搜索历史表)】的数据库操作Service实现
* @createDate 2024-02-25 18:35:18
*/
@Service("userSearchHistoryService")
public class UserSearchHistoryServiceImpl extends ServiceImpl<MPanUserSearchHistoryMapper, MPanUserSearchHistory>
    implements IUserSearchHistoryService {

    @Override
    public List<UserHistoryVO> queryUserHistory(UserHistoryContext context) {
        LambdaQueryWrapper<MPanUserSearchHistory> query = new LambdaQueryWrapper<>();
        query.eq(MPanUserSearchHistory::getUserId,context.getUserId())
                .orderByDesc(MPanUserSearchHistory::getCreateTime)
                .last("limit 10");
        List<MPanUserSearchHistory> list = list(query);
        List<UserHistoryVO> voList = list.stream().map(entity ->
                new UserHistoryVO(entity.getSearchContent())).collect(Collectors.toList());

        return voList;
    }
}




