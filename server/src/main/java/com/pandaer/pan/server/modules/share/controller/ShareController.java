package com.pandaer.pan.server.modules.share.controller;


import com.pandaer.pan.core.response.Resp;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.common.annotation.LoginIgnore;
import com.pandaer.pan.server.common.annotation.NeedShareCode;
import com.pandaer.pan.server.common.utils.ShareIdUtil;
import com.pandaer.pan.server.common.utils.UserIdUtil;
import com.pandaer.pan.server.modules.share.context.*;
import com.pandaer.pan.server.modules.share.converter.ShareConverter;
import com.pandaer.pan.server.modules.share.po.CancelSharesPO;
import com.pandaer.pan.server.modules.share.po.CheckShareCodePO;
import com.pandaer.pan.server.modules.share.po.CreateShareUrlPO;
import com.pandaer.pan.server.modules.share.service.IShareService;
import com.pandaer.pan.server.modules.share.vo.MPanShareUrlListVO;
import com.pandaer.pan.server.modules.share.vo.MPanShareUrlVO;
import com.pandaer.pan.server.modules.share.vo.ShareDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@Api("分享模块")
public class ShareController {

    @Autowired
    private IShareService shareService;

    @Autowired
    private ShareConverter shareConverter;

    @ApiOperation(value = "创建分享链接",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("share")
    public Resp<MPanShareUrlVO> createShareUrl(@Validated @RequestBody CreateShareUrlPO createShareUrlPO) {
        CreateShareUrlContext context = shareConverter.PO2ContextInCreateShareUrl(createShareUrlPO);
        MPanShareUrlVO shareUrlVO = shareService.createShareUrl(context);
        return Resp.successAndData(shareUrlVO);
    }

    @ApiOperation(value = "获取已经分享的信息列表",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("shares")
    public Resp<List<MPanShareUrlListVO>> listShares() {
        ListShareContext context = new ListShareContext();
        context.setUserId(UserIdUtil.getUserId());
        List<MPanShareUrlListVO> vo = shareService.listShare(context);
        return Resp.successAndData(vo);
    }

    @ApiOperation(value = "批量取消分享",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @DeleteMapping("shares")
    public Resp<Object> cancelShares(@Validated @RequestBody CancelSharesPO cancelSharesPO) {
        CancelSharesContext context = shareConverter.PO2ContextInCancelShares(cancelSharesPO);
        shareService.cancelShares(context);
        return Resp.success();
    }


    @ApiOperation(value = "分享码的校验",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @DeleteMapping("share/code/check")
    @LoginIgnore
    public Resp<String> checkShareCode(@Validated @RequestBody CheckShareCodePO checkShareCodePO) {
        CheckShareCodeContext context = new CheckShareCodeContext();
        context.setShareId(IdUtil.decrypt(checkShareCodePO.getShareId()));
        context.setShareCode(checkShareCodePO.getShareCode());
        String token = shareService.checkShareCode(context);
        return Resp.successAndData(token);
    }

    @ApiOperation(value = "获取分享的详情信息",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("share")
    @NeedShareCode
    @LoginIgnore
    public Resp<ShareDetailVO> detail() {
        ShareDetailContext context = new ShareDetailContext();
        context.setShareId(ShareIdUtil.getShareId());
        ShareDetailVO vo = shareService.detail(context);
        return Resp.successAndData(vo);
    }
}
