package com.pandaer.pan.server.modules.share.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.pandaer.pan.bloom.filter.core.BloomFilterManager;
import com.pandaer.pan.core.constants.MPanConstants;
import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.utils.IdUtil;
import com.pandaer.pan.core.utils.JwtUtil;
import com.pandaer.pan.core.utils.UUIDUtil;
import com.pandaer.pan.server.common.cache.ManualCacheService;
import com.pandaer.pan.server.common.config.PanServerConfigProperties;
import com.pandaer.pan.server.common.event.log.ErrorLogEvent;
import com.pandaer.pan.server.modules.file.constants.FileConstants;
import com.pandaer.pan.server.modules.file.context.CopyFileContext;
import com.pandaer.pan.server.modules.file.context.FileDownloadContext;
import com.pandaer.pan.server.modules.file.converter.FileConverter;
import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.service.IUserFileService;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.server.modules.share.bf.ShareSimpleInfoBloomFilterInterceptor;
import com.pandaer.pan.server.modules.share.constants.ShareConstants;
import com.pandaer.pan.server.modules.share.context.*;
import com.pandaer.pan.server.modules.share.converter.ShareConverter;
import com.pandaer.pan.server.modules.share.domain.MPanShare;
import com.pandaer.pan.server.modules.share.domain.MPanShareFile;
import com.pandaer.pan.server.modules.share.enums.ShareDayTypeEnum;
import com.pandaer.pan.server.modules.share.enums.ShareStatusEnum;
import com.pandaer.pan.server.modules.share.service.IShareFileService;
import com.pandaer.pan.server.modules.share.service.IShareService;
import com.pandaer.pan.server.modules.share.mapper.MPanShareMapper;
import com.pandaer.pan.server.modules.share.vo.*;
import com.pandaer.pan.server.modules.user.domain.MPanUser;
import com.pandaer.pan.server.modules.user.service.IUserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理分享的相关业务
 */
@Service
public class ShareServiceImpl extends ServiceImpl<MPanShareMapper, MPanShare>
    implements IShareService, ApplicationContextAware {

    private final IUserFileService userFileService;

    private final IShareFileService shareFileService;

    private final IUserService userService;

    private final PanServerConfigProperties properties;

    private final ShareConverter shareConverter;

    private final FileConverter fileConverter;

    private final ShareSimpleInfoBloomFilterInterceptor bloomFilterInterceptor;

    private final ManualCacheService<MPanShare> shareManualCacheService;

    @Autowired
    public ShareServiceImpl(IUserFileService userFileService,
                            IShareFileService shareFileService,
                            IUserService userService, PanServerConfigProperties properties,
                            ShareConverter shareConverter, FileConverter fileConverter,
                            ShareSimpleInfoBloomFilterInterceptor bloomFilterInterceptor,
                            @Qualifier("shareCacheService") ManualCacheService<MPanShare> shareManualCacheService) {
        this.userFileService = userFileService;
        this.shareFileService = shareFileService;
        this.userService = userService;
        this.properties = properties;
        this.shareConverter = shareConverter;
        this.fileConverter = fileConverter;
        this.bloomFilterInterceptor = bloomFilterInterceptor;
        this.shareManualCacheService = shareManualCacheService;
    }


    private ApplicationContext applicationContext;



    /**
     * 创建分享并生成链接
     * 1. 校验文件是否存在
     * 2. 拼装分享记录保存数据哭
     * 3. 保存分享记录与文件记录之间的关联关系
     * 4. 拼装返回实体VO
     * @param context 创建分享并生成访问链接的上下文对象
     * @return 包含详细关于文件分享的信息
     */
    @Override
    @Transactional(rollbackFor = MPanBusinessException.class)
    public MPanShareUrlVO createShareUrl(CreateShareUrlContext context) {
        //检查文件列表的合法性
        checkFileIdList(context);
        //保存分享记录
        saveShareRecord(context);
        //建立当前分享与文件之间的关系
        saveShareFileRecord(context);
        //返回定义的响应实体
        MPanShareUrlVO vo = assembleShareUrlVO(context);
        //增量添加数据到布隆过滤器中 todo(布隆过滤器的增量增加有待优化)
        bloomFilterInterceptor.getFilter().put(vo.getShareId());
        return vo;
    }


    /**
     * 获取当前登录用户的分享链接列表
     * @param context 上下文对象
     * @return 分享链接相关信息
     */
    @Override
    public List<MPanShareUrlListVO> listShare(ListShareContext context) {
        LambdaQueryWrapper<MPanShare> query = new LambdaQueryWrapper<>();
        query.eq(MPanShare::getCreateUser,context.getUserId());
        List<MPanShare> shareList = list(query);
        if (shareList == null || shareList.isEmpty()) {
            return Lists.newArrayList();
        }
        return shareList.stream().map(shareConverter::entity2VOInListShare).collect(Collectors.toList());
    }


    /**
     * 取消分享链接
     * 具体的业务逻辑
     * 1. 检查shareId的合法性
     * 2. 删除分享记录
     * 3. 删除分享记录与文件记录之间的关联关系
     * @param context 上下文对象
     */
    @Override
    @Transactional(rollbackFor = MPanBusinessException.class)
    public void cancelShares(CancelSharesContext context) {
        checkShareIdAndUserId(context);
        doDeleteShareRecords(context);
        doDeleteShareFileRecords(context);
    }


    /**
     * 校验分享码
     * 具体的业务逻辑
     * 1. 校验shareId的合法性
     * 2. 比对shareCode的正确性
     * 3. 返回token
     * @param context 上下文对象
     * @return 校验成功后的Token
     */
    @Override
    public String checkShareCode(CheckShareCodeContext context) {
        MPanShare share = checkShareStatus(context.getShareId());
        context.setShare(share);
        doCheckShareCode(context);
        return genShareToken(context);
    }


    /**
     * 1.检查分享的合法性 分享的状态 分享的有效期
     * 2. 查询分享用户的信息
     * 3. 查询分享的文件列表
     * @param context 上下文对象
     * @return 分享的详细信息
     */
    @Override
    public ShareDetailVO detail(ShareDetailContext context) {
        MPanShare share = checkShareStatus(context.getShareId());
        context.setShare(share);
        initShareVO(context);
        assembleMainShareInfo(context);
        assembleShareFileList(context);
        assembleShareUserInfo(context);
        return context.getVo();
    }

    /**
     *查询分享的简单详情
     * 1.检查分享的合法性 分享的状态 分享的有效期
     * 2. 查询分享用户的信息
     * 3. 查询分享的文件列表
     * @param context 上下文对象
     * @return 分享简略信息
     */
    @Override
    public ShareSimpleInfoVO simpleInfo(ShareSimpleInfoContext context) {
        MPanShare share = checkShareStatus(context.getShareId());
        context.setRecords(share);
        initShareSimpleVO(context);
        assembleMainShareSimpleInfo(context);
        assembleShareUserSimpleInfo(context);
        return context.getVo();
    }

    /**
     * 获取分享文件夹下一级文件列表
     * 1.校验parentId 以及 ShareId的合法性
     * 2.根据parentId 获取文件列表信息
     * 3.拼装VO对象列表返回
     * @param context 上下文对象
     * @return 直接子文件以及子文件夹
     */
    @Override
    public List<UserFileVO> listChildFile(QueryChildFileListContext context) {
        checkParentIdAndShareId(context);
        return getChildFileList(context);
    }

    /**
     * 保存文件列表到我的网盘
     * 具体业务逻辑
     * 1.校验文件ID,分享ID,以及文件与分享ID之间的对应关系，以及目标文件夹是否存在
     * 2.根据文件ID获取到全部的文件ID
     * 3.保存文件与用户之间的关系记录
     * @param context 上下文对象
     */
    @Override
    public void saveFileList(SaveShareFileContext context) {
        checkShareAndFileList(context);
        checkTargetParent(context);
        doSaveFileList(context);
    }

    /**
     * 分享的文件下载
     * 1. 校验分享状态以及文件ID的合法性
     * 2. 委托userFileService download
     * @param context 上下文对象
     */
    @Override
    public void shareDownload(ShareDownloadContext context) {
        checkShareStatus(context.getShareId());
        checkFileIdInShare(context.getShareId(),Lists.newArrayList(context.getFileId()));
        doShareDownload(context);
    }

    /**
     * 根据文件列表刷新分享的状态信息
     * 具体的逻辑
     * 1. 根据文件ID获取到可能影响的shareId
     * 2. 具体判断文件是否真正的影响到了分享状态
     * @param allFileList 上下文对象
     */
    @Override
    public void refreshShareStatus(List<MPanUserFile> allFileList) {
        List<Long> allFileIdList = allFileList.stream().map(MPanUserFile::getFileId).collect(Collectors.toList());
        //全部可能受影响的分享Id
        Set<Long> shareIdList = shareFileService.getShareIdListByFileId(allFileIdList);
        //判断是否真的受到了影响
        shareIdList.forEach(id -> {
            if (isFileDeletedInShare(id)) {
                doChangeShareStatus(id,ShareStatusEnum.FILE_DELETED.getCode());
            }else {
                doChangeShareStatus(id,ShareStatusEnum.NORMAL.getCode());
            }
        });
    }

    /**
     * 滚动查询分享的ID
     * @param startId 起始ID
     * @param limit 窗口大小
     * @return 查询到的分享ID
     */
    @Override
    public List<Long> rollingGetShareId(Long startId, Long limit) {
        LambdaQueryWrapper<MPanShare> query = new LambdaQueryWrapper<>();
        query.select(MPanShare::getShareId)
                .gt(MPanShare::getShareId,startId)
                .orderByAsc(MPanShare::getShareId)
                .last("limit " + limit);
        return listObjs(query,obj -> (Long) obj);
    }

    /**
     * 根据分享Id改变分享的状态
     * @param id 分享ID
     */
    private void doChangeShareStatus(Long id,Integer shareStatus) {
        MPanShare share = getById(id);
        share.setShareStatus(shareStatus);
        if (!updateById(share)) {
            ErrorLogEvent event = new ErrorLogEvent(this, "更新分享状态失败 分享Id为: " + id, MPanConstants.ZERO_LONG);
            applicationContext.publishEvent(event);
        }
    }

    /**
     * 根据分享Id判断当前的分享是否会受到了当前事件的影响
     * @param id 分享ID
     * @return 分享的文件夹中是否有删除的文件
     */
    private boolean isFileDeletedInShare(Long id) {
        List<Long> fileIdList = shareFileService.getFileIdListInCurShare(id);
        //判断这些文件是否存在有被删除
        return checkFileDeletedWithEvent(fileIdList);
    }

    /**
     * 检查这些文件是否被删除
     * @param fileIdList 文件ID列表
     * @return 使用由于某个事件，分享的文件被删除
     */
    private boolean checkFileDeletedWithEvent(List<Long> fileIdList) {
        List<MPanUserFile> userFileList = userFileService.listByIds(fileIdList);
        List<MPanUserFile> allRecords = userFileService.findAllRecords(userFileList);
        Set<Integer> delFlagSet = allRecords.stream().map(MPanUserFile::getDelFlag).collect(Collectors.toSet());
        return delFlagSet.size() > 1 || delFlagSet.contains(FileConstants.YES);
    }

    /**
     * 执行文件下载
     * @param context
     */
    private void doShareDownload(ShareDownloadContext context) {
        FileDownloadContext fileDownloadContext = new FileDownloadContext();
        fileDownloadContext.setUserId(context.getUserId());
        fileDownloadContext.setFileId(context.getFileId());
        fileDownloadContext.setResponse(context.getResponse());
        userFileService.shareDownload(fileDownloadContext);
    }

    private void checkFileIdInShare(Long shareId, ArrayList<Long> fileIdList) {
        LambdaQueryWrapper<MPanShareFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanShareFile::getShareId,shareId);
        List<Long> shareFileList =
                shareFileService.list(query).stream().map(MPanShareFile::getFileId).collect(Collectors.toList());
        if (!new HashSet<>(shareFileList).containsAll(fileIdList)) {
            throw new MPanBusinessException("要下载的文件存在不合法");
        }
    }

    /**
     * 1.根据文件ID获取到全部的文件ID
     * 2.保存文件与用户之间的关系记录
     * @param context
     */
    private void doSaveFileList(SaveShareFileContext context) {
        CopyFileContext copyFileContext = new CopyFileContext();
        copyFileContext.setCopyFileIdList(context.getFileIdList());
        copyFileContext.setTargetParentId(context.getTargetParentId());
        copyFileContext.setUserId(context.getUserId());
        userFileService.copyFile(copyFileContext);
    }

    /**
     * 校验目标文件夹是否存在
     * @param context
     */
    private void checkTargetParent(SaveShareFileContext context) {
        Long targetParentId = context.getTargetParentId();
        MPanUserFile targetFolder = userFileService.getById(targetParentId);
        if (Objects.isNull(targetFolder) || Objects.equals(targetFolder.getFolderFlag(),FileConstants.NO)) {
            throw new MPanBusinessException("目标路径不存在或者不是一个目录");
        }
    }

    /**
     * 1.校验文件ID,分享ID,以及文件与分享ID之间的对应关系
     * @param context
     */
    private void checkShareAndFileList(SaveShareFileContext context) {
        List<Long> fileIdList = context.getFileIdList();
        if (Objects.isNull(fileIdList) || fileIdList.isEmpty()) {
            throw new MPanBusinessException("文件列表为空");
        }
        Long shareId = context.getShareId();
        LambdaQueryWrapper<MPanShareFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanShareFile::getShareId,shareId)
                .in(MPanShareFile::getFileId,fileIdList);
        int res = shareFileService.count(query);
        if (fileIdList.size() != res) {
            throw new MPanBusinessException("要保存的文件列表存在不合法文件");
        }
    }

    private List<UserFileVO> getChildFileList(QueryChildFileListContext context) {
        Long parentId = context.getParentId();
        LambdaQueryWrapper<MPanUserFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanUserFile::getParentId,parentId);
        List<MPanUserFile> entityList = userFileService.list(query);
        return entityList.stream().map(fileConverter::entity2VOInQueryFileList).collect(Collectors.toList());
    }

    /**
     * 校验parentId 以及 ShareId的合法性
     * @param context
     */
    private void checkParentIdAndShareId(QueryChildFileListContext context) {
        Long parentId = context.getParentId();
        Long shareId = context.getShareId();
        if (shareId == null || parentId == null) {
            throw new MPanBusinessException("parentId和shareId不能为空");
        }
        LambdaQueryWrapper<MPanShareFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanShareFile::getShareId,shareId)
                .eq(MPanShareFile::getFileId,parentId);
        int res = shareFileService.count(query);
        if (res == 0) {
            throw new MPanBusinessException("文件夹不存在");
        }
    }


    /**
     * 组装SimpleInfo的用户信息
     * @param context
     */
    private void assembleShareUserSimpleInfo(ShareSimpleInfoContext context) {
        Long userId = context.getRecords().getCreateUser();
        MPanUser userEntity = userService.getById(userId);
        ShareUserInfoVO shareUserInfoVO = new ShareUserInfoVO();
        shareUserInfoVO.setUsername(encryptUsername(userEntity.getUsername()));
        shareUserInfoVO.setUserId(userEntity.getUserId());
        context.getVo().setShareUserInfoVO(shareUserInfoVO);
    }

    /**
     * 组装simpleInfoVO的主要信息
     * @param context
     */
    private void assembleMainShareSimpleInfo(ShareSimpleInfoContext context) {
        ShareSimpleInfoVO vo = context.getVo();
        MPanShare records = context.getRecords();
        vo.setShareId(records.getShareId());
        vo.setShareName(records.getShareName());
    }

    /**
     * 初始化一个简略信息实体
     * @param context
     */
    private void initShareSimpleVO(ShareSimpleInfoContext context) {
        ShareSimpleInfoVO shareSimpleInfoVO = new ShareSimpleInfoVO();
        context.setVo(shareSimpleInfoVO);
    }

    /**
     * 根据用户ID 返回用户信息
     * @param context
     */
    private void assembleShareUserInfo(ShareDetailContext context) {
        Long userId = context.getShare().getCreateUser();
        MPanUser user = userService.getById(userId);
        ShareUserInfoVO shareUserInfoVO = new ShareUserInfoVO();
        shareUserInfoVO.setUserId(userId);
        shareUserInfoVO.setUsername(encryptUsername(user.getUsername()));
        context.getVo().setUserInfo(shareUserInfoVO);
    }

    private String encryptUsername(String username) {
        return username.substring(0,2) + "****" + username.substring(username.length() - 2);
    }


    /**
     * 1. 查询出分享的文件ID
     * 2. 根据文件ID查询出文件的信息
     * @param context
     */
    private void assembleShareFileList(ShareDetailContext context) {
        Long shareId = context.getShareId();
        LambdaQueryWrapper<MPanShareFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanShareFile::getShareId,shareId);
        List<MPanShareFile> list = shareFileService.list(query);
        if (list == null || list.isEmpty()) {
            throw new MPanBusinessException("分享文件丢失");
        }
        List<Long> fileIdList = list.stream().map(MPanShareFile::getFileId).collect(Collectors.toList());
        List<MPanUserFile> userFileList = userFileService.listByIds(fileIdList);
        context.getVo().setUserFileList(userFileList.stream().map(shareConverter::entity2VOInGetDetailInfo).collect(Collectors.toList()));
    }

    private void assembleMainShareInfo(ShareDetailContext context) {
        MPanShare share = context.getShare();
        ShareDetailVO vo = context.getVo();
        vo.setShareId(share.getShareId());
        vo.setShareName(share.getShareName());
        vo.setShareDayType(share.getShareDayType());
        vo.setCreateTime(share.getCreateTime());
        vo.setShareEndTime(share.getShareEndTime());
    }

    /**
     * 初始化分享的VO
     * @param context
     */
    private void initShareVO(ShareDetailContext context) {
        ShareDetailVO shareDetailVO = new ShareDetailVO();
        context.setVo(shareDetailVO);
    }


    @Override
    public boolean removeById(Serializable id) {
        return shareManualCacheService.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return shareManualCacheService.removeByIds(idList);
    }

    @Override
    public boolean updateById(MPanShare entity) {
        return shareManualCacheService.updateById(entity.getShareId(),entity);
    }

    @Override
    public boolean updateBatchById(Collection<MPanShare> entityList) {
        Map<Long, MPanShare> list = entityList.stream().collect(Collectors.toMap(MPanShare::getShareId, entity -> entity));
        return shareManualCacheService.updateByIds(list);
    }

    @Override
    public MPanShare getById(Serializable id) {
        return shareManualCacheService.getById(id);
    }

    @Override
    public List<MPanShare> listByIds(Collection<? extends Serializable> idList) {
        return shareManualCacheService.getByIds(idList);
    }


    /*===========================================================================PRIVATE===========================================================================*/
    /**
     * 检查文件列表的合法性
     * @param context 上下文
     */
    private void checkFileIdList(CreateShareUrlContext context) {
        //获取用户提供的文件ID
        List<Long> shareFileIdList = context.getShareFileIdList();

        //根据用户提供的文件ID查询用户文件信息
        LambdaQueryWrapper<MPanUserFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanUserFile::getUserId,context.getUserId())
                .in(MPanUserFile::getFileId,shareFileIdList);
        List<MPanUserFile> userFileList = userFileService.list(query);

        //判断用户的与实际的大小是否一致
        if (userFileList.size() != shareFileIdList.size()) {
            throw new MPanBusinessException("文件列表不合法");
        }
        //判断文件列表的用户是否属于当前登录用户
        Set<Long> userIdSet = userFileList.stream().map(MPanUserFile::getUserId).collect(Collectors.toSet());
        if (userIdSet.size() > 1 || !userIdSet.contains(context.getUserId())) {
            throw new MPanBusinessException("文件列表不合法");
        }
    }

    /**
     * 拼装返回实体VO
     * @param context
     * @return
     */
    private MPanShareUrlVO assembleShareUrlVO(CreateShareUrlContext context) {
        MPanShareUrlVO vo = new MPanShareUrlVO();
        MPanShare record = context.getShareRecord();
        vo.setShareId(record.getShareId());
        vo.setShareName(record.getShareName());
        vo.setShareUrl(record.getShareUrl());
        vo.setShareCode(record.getShareCode());
        vo.setShareStatus(record.getShareStatus());
        return vo;
    }


    /**
     * 保存文件记录与分享记录之间的关联关系
     * 委托给ShareFileService
     * @param context
     */
    private void saveShareFileRecord(CreateShareUrlContext context) {
        BatchSaveShareFileContext batchSaveShareFileContext = shareConverter.context2ContextInSaveShareFile(context);
        shareFileService.batchSaveShareFile(batchSaveShareFileContext);
    }


    /**
     * 保存分享记录
     * @param context 上下文
     */
    private void saveShareRecord(CreateShareUrlContext context) {
        MPanShare shareRecord = new MPanShare();
        Long shareId = IdUtil.get();
        shareRecord.setShareId(shareId);
        shareRecord.setShareName(context.getShareName());
        shareRecord.setShareType(context.getShareType());
        shareRecord.setShareDayType(context.getShareDayType());
        Integer days = ShareDayTypeEnum.getDaysByCode(context.getShareDayType());
        shareRecord.setShareDay(days);
        shareRecord.setShareEndTime(DateUtil.offsetDay(new Date(),days));
        shareRecord.setShareUrl(genShareUrl(shareId));
        shareRecord.setShareCode(genShareCode());
        shareRecord.setShareStatus(ShareStatusEnum.NORMAL.getCode());
        shareRecord.setCreateUser(context.getUserId());
        shareRecord.setCreateTime(new Date());
        if (!save(shareRecord)) {
            throw new MPanBusinessException("分享记录保存失败");
        }
        context.setShareRecord(shareRecord);
    }


    /**
     * 创建提取码
     * @return
     */
    private String genShareCode() {
        return RandomStringUtils.randomAlphanumeric(4).toLowerCase();
    }

    /**
     * 生成分享链接
     * @param shareId
     * @return
     */
    private String genShareUrl(Long shareId) {
        String sharePrefix = properties.getSharePrefix();
        if (!sharePrefix.endsWith(MPanConstants.SLASH_STR)) {
            sharePrefix = sharePrefix + MPanConstants.SLASH_STR;
        }
        return sharePrefix + URLEncoder.encode(IdUtil.encrypt(shareId));
    }


    /**
     * 删除分享与文件记录的关联关系
     * @param context
     */
    private void doDeleteShareFileRecords(CancelSharesContext context) {
        LambdaQueryWrapper<MPanShareFile> query = new LambdaQueryWrapper<>();
        query.eq(MPanShareFile::getCreateUser,context.getUserId())
                .in(MPanShareFile::getShareId,context.getShareIdList());
        if (!shareFileService.remove(query)) {
            throw new MPanBusinessException("分享文件记录删除失败");
        }
    }

    /**
     * 删除分享记录
     * @param context
     */
    private void doDeleteShareRecords(CancelSharesContext context) {
        LambdaQueryWrapper<MPanShare> query = new LambdaQueryWrapper<>();
        query.eq(MPanShare::getCreateUser,context.getUserId())
                .in(MPanShare::getShareId,context.getShareIdList());
        if (!remove(query)) {
            throw new MPanBusinessException("分享记录删除失败");
        }
    }

    /**
     * 校验分享ID和用户ID的合法性
     * @param context
     */
    private void checkShareIdAndUserId(CancelSharesContext context) {
        List<Long> shareIdList = context.getShareIdList();
        LambdaQueryWrapper<MPanShare> query = new LambdaQueryWrapper<>();
        query.eq(MPanShare::getCreateUser,context.getUserId())
                .in(MPanShare::getShareId,shareIdList);
        List<MPanShare> shareList = list(query);
        if (shareList.size() != shareIdList.size()) {
            throw new MPanBusinessException("分享ID列表存在不合法ID");
        }
        Set<Long> userIdSet = shareList.stream().map(MPanShare::getCreateUser).collect(Collectors.toSet());
        if (userIdSet.size() > 1 || !userIdSet.contains(context.getUserId())) {
            throw new MPanBusinessException("登录用户没有操作权限");
        }
    }


    private String genShareToken(CheckShareCodeContext context) {
        MPanShare share = context.getShare();
        return JwtUtil.generateToken(UUIDUtil.getUUID(),
                ShareConstants.SHARE_ID, share.getShareId(), ShareConstants.ONE_HOUR_LONG);
    }

    private void doCheckShareCode(CheckShareCodeContext context) {
        MPanShare share = context.getShare();
        if (!Objects.equals(share.getShareCode(),context.getShareCode())) {
            throw new MPanBusinessException("提取码错误");
        }
    }

    /**
     * 检查分享记录的合法性
     * @param shareId
     * @return
     */
    private MPanShare checkShareStatus(Long shareId) {
        MPanShare share = getById(shareId);
        if (Objects.isNull(share)) {
            throw new MPanBusinessException("分享被取消或者不存在");
        }
        if(Objects.equals(ShareStatusEnum.FILE_DELETED.getCode(),share.getShareStatus())){
            throw new MPanBusinessException("分享文件丢失");
        }
        if (!Objects.equals(ShareDayTypeEnum.PERMANENT_VALIDITY.getCode(),share.getShareStatus())
                && share.getShareEndTime().before(new Date())) {
            throw new MPanBusinessException("分享已经过期");
        }

        return share;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}




