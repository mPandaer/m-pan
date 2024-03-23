package com.pandaer.pan.server.modules.user.controller;

import com.pandaer.pan.core.response.Resp;
import com.pandaer.pan.server.common.utils.UserIdUtil;
import com.pandaer.pan.server.modules.user.context.UserHistoryContext;
import com.pandaer.pan.server.modules.user.service.IUserSearchHistoryService;
import com.pandaer.pan.server.modules.user.vo.UserHistoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "用户搜索历史模块")
@RestController
public class UserHistoryController {

    @Autowired
    private IUserSearchHistoryService userSearchHistoryService;

    @ApiOperation(value = "获取用户搜索历史记录",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/user/search/histories")
    public Resp<List<UserHistoryVO>> getUserHistory() {
        UserHistoryContext context = new UserHistoryContext();
        context.setUserId(UserIdUtil.getUserId());
        List<UserHistoryVO> list = userSearchHistoryService.queryUserHistory(context);
        return Resp.successAndData(list);
    }
}
