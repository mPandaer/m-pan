package com.pandaer.pan.server.modules.share;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.utils.JwtUtil;
import com.pandaer.pan.server.modules.file.context.CreateFolderContext;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.server.modules.share.constants.ShareConstants;
import com.pandaer.pan.server.modules.share.context.*;
import com.pandaer.pan.server.modules.share.enums.ShareDayTypeEnum;
import com.pandaer.pan.server.modules.share.enums.ShareTypeEnum;
import com.pandaer.pan.server.modules.share.service.IShareService;
import com.pandaer.pan.server.modules.share.vo.MPanShareUrlListVO;
import com.pandaer.pan.server.modules.share.vo.MPanShareUrlVO;
import com.pandaer.pan.server.modules.share.vo.ShareDetailVO;
import com.pandaer.pan.server.modules.share.vo.ShareSimpleInfoVO;
import com.pandaer.pan.server.modules.user.context.UserRegisterContext;
import com.pandaer.pan.server.modules.user.service.IUserService;
import com.pandaer.pan.server.modules.user.vo.CurrentUserVO;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ShareTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserFileService userFileService;

    @Autowired
    private IShareService shareService;


    /**
     * 创建分享链接 成功
     */
    @Test
    public void testCreateShareUrlSuccess() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        //分享文件夹
        CreateShareUrlContext context = new CreateShareUrlContext();
        context.setShareName("测试分享");
        context.setUserId(userId);
        context.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        context.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        context.setShareFileIdList(Lists.newArrayList(fileId));
        MPanShareUrlVO shareUrl = shareService.createShareUrl(context);
        Assert.assertTrue(shareUrl != null && shareUrl.getShareId() != null && shareUrl.getShareId() > 0);
    }

    /**
     * 创建分享链接 失败 文件不存在
     */
    @Test(expected = MPanBusinessException.class)
    public void testCreateShareUrlFailFileNotExist() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        //分享文件夹
        CreateShareUrlContext context = new CreateShareUrlContext();
        context.setShareName("测试分享");
        context.setUserId(userId);
        context.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        context.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        context.setShareFileIdList(Lists.newArrayList(fileId + 1));
        MPanShareUrlVO shareUrl = shareService.createShareUrl(context);
        Assert.assertTrue(shareUrl != null && shareUrl.getShareId() != null && shareUrl.getShareId() > 0);
    }


    /**
     * 创建分享链接 失败 用户没有权限
     */
    @Test(expected = MPanBusinessException.class)
    public void testCreateShareUrlFailUserNotPermission() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        //分享文件夹
        CreateShareUrlContext context = new CreateShareUrlContext();
        context.setShareName("测试分享");
        context.setUserId(userId + 1);
        context.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        context.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        context.setShareFileIdList(Lists.newArrayList(fileId));
        MPanShareUrlVO shareUrl = shareService.createShareUrl(context);
        Assert.assertTrue(shareUrl != null && shareUrl.getShareId() != null && shareUrl.getShareId() > 0);
    }


    /**
     * 获取分享链接列表 成功
     */

    @Test
    public void testListSharesSuccess() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        //分享文件夹
        CreateShareUrlContext context = new CreateShareUrlContext();
        context.setShareName("测试分享");
        context.setUserId(userId);
        context.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        context.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        context.setShareFileIdList(Lists.newArrayList(fileId));
        MPanShareUrlVO shareUrl = shareService.createShareUrl(context);
        Assert.assertTrue(shareUrl != null && shareUrl.getShareId() != null && shareUrl.getShareId() > 0);

        //获取分享链接列表
        ListShareContext listShareContext = new ListShareContext();
        listShareContext.setUserId(userId);
        List<MPanShareUrlListVO> shareList = shareService.listShare(listShareContext);
        Assert.assertTrue(shareList != null && shareList.size() == 1);
    }


    /**
     * 取消分享 成功
     */

    @Test
    public void testCancelSharesSuccess() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        //分享文件夹
        CreateShareUrlContext context = new CreateShareUrlContext();
        context.setShareName("测试分享");
        context.setUserId(userId);
        context.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        context.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        context.setShareFileIdList(Lists.newArrayList(fileId));
        MPanShareUrlVO shareUrl = shareService.createShareUrl(context);
        Assert.assertTrue(shareUrl != null && shareUrl.getShareId() != null && shareUrl.getShareId() > 0);

        //获取分享链接列表
        ListShareContext listShareContext = new ListShareContext();
        listShareContext.setUserId(userId);
        List<MPanShareUrlListVO> shareList = shareService.listShare(listShareContext);
        Assert.assertTrue(shareList != null && shareList.size() == 1);

        //取消分享
        CancelSharesContext cancelSharesContext = new CancelSharesContext();
        cancelSharesContext.setUserId(userId);
        cancelSharesContext.setShareIdList(Lists.newArrayList(shareList.get(0).getShareId()));
        shareService.cancelShares(cancelSharesContext);

        //获取分享链接列表
        List<MPanShareUrlListVO> shareList2 = shareService.listShare(listShareContext);
        Assert.assertTrue(shareList2 != null && shareList2.isEmpty());
    }

    /**
     * 校验分享码 成功
     */

    @Test
    public void testCheckShareCodeSuccess() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        //分享文件夹
        CreateShareUrlContext context = new CreateShareUrlContext();
        context.setShareName("测试分享");
        context.setUserId(userId);
        context.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        context.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        context.setShareFileIdList(Lists.newArrayList(fileId));
        MPanShareUrlVO shareUrl = shareService.createShareUrl(context);
        Assert.assertTrue(shareUrl != null && shareUrl.getShareId() != null && shareUrl.getShareId() > 0);

        //校验分享码
        String shareCode = shareUrl.getShareCode();
        Long shareId = shareUrl.getShareId();
        CheckShareCodeContext checkShareCodeContext = new CheckShareCodeContext();
        checkShareCodeContext.setShareId(shareId);
        checkShareCodeContext.setShareCode(shareCode);
        String token = shareService.checkShareCode(checkShareCodeContext);
        Assert.assertTrue(token != null && !token.isEmpty());
    }

    /**
     * 校验分享码 失败 分享码错误
     */

    @Test(expected = MPanBusinessException.class)
    public void testCheckShareCodeFailCodeError() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        //分享文件夹
        CreateShareUrlContext context = new CreateShareUrlContext();
        context.setShareName("测试分享");
        context.setUserId(userId);
        context.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        context.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        context.setShareFileIdList(Lists.newArrayList(fileId));
        MPanShareUrlVO shareUrl = shareService.createShareUrl(context);
        Assert.assertTrue(shareUrl != null && shareUrl.getShareId() != null && shareUrl.getShareId() > 0);

        //校验分享码
        String shareCode = shareUrl.getShareCode();
        Long shareId = shareUrl.getShareId();
        CheckShareCodeContext checkShareCodeContext = new CheckShareCodeContext();
        checkShareCodeContext.setShareId(shareId);
        checkShareCodeContext.setShareCode(shareCode + "_x");
        String token = shareService.checkShareCode(checkShareCodeContext);
        Assert.assertTrue(token != null && !token.isEmpty());
    }


    /**
     * 获取分享的详细信息
     */
    @Test
    public void testGetShareDetailInfoSuccess() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        //分享文件夹
        CreateShareUrlContext context = new CreateShareUrlContext();
        context.setShareName("测试分享");
        context.setUserId(userId);
        context.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        context.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        context.setShareFileIdList(Lists.newArrayList(fileId));
        MPanShareUrlVO shareUrl = shareService.createShareUrl(context);
        Assert.assertTrue(shareUrl != null && shareUrl.getShareId() != null && shareUrl.getShareId() > 0);

        //校验分享码
        String shareCode = shareUrl.getShareCode();
        Long shareId = shareUrl.getShareId();
        CheckShareCodeContext checkShareCodeContext = new CheckShareCodeContext();
        checkShareCodeContext.setShareId(shareId);
        checkShareCodeContext.setShareCode(shareCode);
        String token = shareService.checkShareCode(checkShareCodeContext);
        Assert.assertTrue(token != null && !token.isEmpty());

        //获取分享详情
        ShareDetailContext shareDetailContext = new ShareDetailContext();
        Long decShareId = (Long) JwtUtil.analyzeToken(token, ShareConstants.SHARE_ID);
        shareDetailContext.setShareId(decShareId);
        ShareDetailVO detail = shareService.detail(shareDetailContext);
        System.out.println(detail);
        Assert.assertNotNull(detail);
    }

    /**
     * 测试获取分享的简略信息
     */
    @Test
    public void testGetShareSimpleInfoSuccess() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        //分享文件夹
        CreateShareUrlContext context = new CreateShareUrlContext();
        context.setShareName("测试分享");
        context.setUserId(userId);
        context.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        context.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        context.setShareFileIdList(Lists.newArrayList(fileId));
        MPanShareUrlVO shareUrl = shareService.createShareUrl(context);
        Assert.assertTrue(shareUrl != null && shareUrl.getShareId() != null && shareUrl.getShareId() > 0);

        //获取分享简略信息
        ShareSimpleInfoContext shareSimpleInfoContext = new ShareSimpleInfoContext();
        shareSimpleInfoContext.setShareId(shareUrl.getShareId());
        ShareSimpleInfoVO shareSimpleInfoVO = shareService.simpleInfo(shareSimpleInfoContext);
        Assert.assertTrue(shareSimpleInfoVO != null && StringUtils.isNotBlank(shareSimpleInfoVO.getShareName()));
    }

    /**
     * 测试获取分享文件夹下一级文件以及子文件夹信息
     */
    @Test
    public void testGetChildFileListSuccess() {
        Long userId = userRegister();
        CurrentUserVO currentUser = current(userId);
        //创建文件夹 1
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹1");
        createFolderContext.setParentId(currentUser.getRootFileId());
        Long fileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(fileId != null && fileId > 0);

        //创建文件夹1-1
        createFolderContext = new CreateFolderContext();
        createFolderContext.setUserId(userId);
        createFolderContext.setFolderName("新建文件夹1-1");
        createFolderContext.setParentId(fileId);
        Long childFileId = userFileService.creatFolder(createFolderContext);
        Assert.assertTrue(childFileId != null && childFileId > 0);

        //分享文件夹
        CreateShareUrlContext context = new CreateShareUrlContext();
        context.setShareName("测试分享");
        context.setUserId(userId);
        context.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        context.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        context.setShareFileIdList(Lists.newArrayList(fileId,childFileId));
        MPanShareUrlVO shareUrl = shareService.createShareUrl(context);
        Assert.assertTrue(shareUrl != null && shareUrl.getShareId() != null && shareUrl.getShareId() > 0);

        //获取分享文件夹下一级信息
        QueryChildFileListContext queryChildFileListContext = new QueryChildFileListContext();
        queryChildFileListContext.setParentId(fileId);
        queryChildFileListContext.setShareId(shareUrl.getShareId());
        List<UserFileVO> voList = shareService.listChildFile(queryChildFileListContext);
        Assert.assertTrue(CollectionUtil.isNotEmpty(voList) && voList.size() == 1 && voList.get(0).getFilename().equals("新建文件夹1-1"));
    }


    /*-------------------------------------------------------------------------private-------------------------------------------------------------------------*/
    private static final String USERNAME = "bobo";
    private static final String PASSWORD = "12345678";
    private static final String QUESTION = "question";
    private static final String ANSWER = "answer";

    private Long userRegister() {
        UserRegisterContext context = getUserRegisterContext();
        return userService.register(context);
    }

    private CurrentUserVO current(Long userId) {
        return userService.getCurrentUser(userId);
    }

    private UserRegisterContext getUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        context.setQuestion(QUESTION);
        context.setAnswer(ANSWER);
        return context;
    }
}
