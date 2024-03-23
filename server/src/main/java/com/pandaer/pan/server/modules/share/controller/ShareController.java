package com.pandaer.pan.server.modules.share.controller;


import com.pandaer.pan.core.response.Resp;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.server.common.annotation.LoginIgnore;
import com.pandaer.pan.server.common.annotation.NeedShareCode;
import com.pandaer.pan.server.common.utils.ShareIdUtil;
import com.pandaer.pan.server.common.utils.UserIdUtil;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.server.modules.share.context.*;
import com.pandaer.pan.server.modules.share.converter.ShareConverter;
import com.pandaer.pan.server.modules.share.po.CancelSharesPO;
import com.pandaer.pan.server.modules.share.po.CheckShareCodePO;
import com.pandaer.pan.server.modules.share.po.CreateShareUrlPO;
import com.pandaer.pan.server.modules.share.po.SaveShareFilePO;
import com.pandaer.pan.server.modules.share.service.IShareService;
import com.pandaer.pan.server.modules.share.vo.MPanShareUrlListVO;
import com.pandaer.pan.server.modules.share.vo.MPanShareUrlVO;
import com.pandaer.pan.server.modules.share.vo.ShareDetailVO;
import com.pandaer.pan.server.modules.share.vo.ShareSimpleInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.List;


@RestController
@Api(tags = "文件分享模块")
@Log4j2
@Validated
public class ShareController {

    private final IShareService shareService;

    private final ShareConverter shareConverter;

    @Autowired
    public ShareController(IShareService shareService, ShareConverter shareConverter) {
        this.shareService = shareService;
        this.shareConverter = shareConverter;
    }



    @ApiOperation(value = "创建分享并生成分享链接",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("share")
    public Resp<MPanShareUrlVO> createShareUrl(@Validated @RequestBody CreateShareUrlPO createShareUrlPO) {
        CreateShareUrlContext context = shareConverter.PO2ContextInCreateShareUrl(createShareUrlPO);
        MPanShareUrlVO shareUrlVO = shareService.createShareUrl(context);
        return Resp.successAndData(shareUrlVO);
    }

    @ApiOperation(value = "获取已经分享的信息列表",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
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
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @DeleteMapping("shares")
    public Resp<Object> cancelShares(@Validated @RequestBody CancelSharesPO cancelSharesPO) {
        CancelSharesContext context = shareConverter.PO2ContextInCancelShares(cancelSharesPO);
        shareService.cancelShares(context);
        return Resp.success();
    }


    @ApiOperation(value = "分享码的校验",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("share/code/check")
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
            produces = MediaType.APPLICATION_JSON_VALUE
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


    @ApiOperation(value = "获取分享的简略信息",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("share/simple")
    @LoginIgnore
    public Resp<ShareSimpleInfoVO> simpleInfo(@RequestParam("shareId") @NotBlank(message = "分享id不能为空") String shareId) {
        ShareSimpleInfoContext context = new ShareSimpleInfoContext();
        context.setShareId(IdUtil.decrypt(shareId));
        ShareSimpleInfoVO vo = shareService.simpleInfo(context);
        return Resp.successAndData(vo);
    }

    @ApiOperation(value = "根据文件夹ID获取文件夹下的文件列表",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("share/file/list")
    @LoginIgnore
    @NeedShareCode
    public Resp<List<UserFileVO>> listFile(@RequestParam("parentId") @NotBlank(message = "文件的父文件夹ID") String parentId) {
        QueryChildFileListContext context = new QueryChildFileListContext();
        context.setParentId(IdUtil.decrypt(parentId));
        context.setShareId(ShareIdUtil.getShareId());
        List<UserFileVO> vo = shareService.listChildFile(context);
        return Resp.successAndData(vo);
    }

    @ApiOperation(value = "将分享文件保存到我的网盘",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("share/file/save")
    @NeedShareCode
    public Resp<Object> saveFileList(@Validated @RequestBody SaveShareFilePO saveShareFilePO) {
        SaveShareFileContext context = shareConverter.PO2ContextInSaveFileList(saveShareFilePO);
        shareService.saveFileList(context);
        return Resp.success();
    }

    @ApiOperation(value = "分享文件下载",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("share/file/save")
    @NeedShareCode
    public void shareDownload(@NotBlank(message = "分享的文件ID不能为空") @RequestParam("fileId") String fileId,
                                      HttpServletResponse response) {
        ShareDownloadContext context = new ShareDownloadContext();
        context.setUserId(UserIdUtil.getUserId());
        context.setFileId(IdUtil.decrypt(fileId));
        context.setShareId(ShareIdUtil.getShareId());
        context.setResponse(response);
        shareService.shareDownload(context);
    }






}
