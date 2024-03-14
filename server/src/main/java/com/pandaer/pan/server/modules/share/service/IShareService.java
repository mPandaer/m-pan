package com.pandaer.pan.server.modules.share.service;

import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.server.modules.share.context.*;
import com.pandaer.pan.server.modules.share.domain.MPanShare;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pandaer.pan.server.modules.share.vo.MPanShareUrlListVO;
import com.pandaer.pan.server.modules.share.vo.MPanShareUrlVO;
import com.pandaer.pan.server.modules.share.vo.ShareDetailVO;
import com.pandaer.pan.server.modules.share.vo.ShareSimpleInfoVO;

import java.util.List;

/**
* @author pandaer
* @description 针对表【m_pan_share(用户分享表)】的数据库操作Service
* @createDate 2024-02-25 18:38:16
*/
public interface IShareService extends IService<MPanShare> {


    /**
     * 创建分享链接
     * @param context
     * @return
     */
    MPanShareUrlVO createShareUrl(CreateShareUrlContext context);


    /**
     * 获取当前用户的分享链接列表
     * @param context
     * @return
     */
    List<MPanShareUrlListVO> listShare(ListShareContext context);


    /**
     * 取消分享链接
     * @param context
     */
    void cancelShares(CancelSharesContext context);


    /**
     * 校验分享码
     * @param context
     * @return
     */
    String checkShareCode(CheckShareCodeContext context);

    /**
     * 获取分享详情
     * @param context
     * @return
     */
    ShareDetailVO detail(ShareDetailContext context);

    ShareSimpleInfoVO simpleInfo(ShareSimpleInfoContext context);


    /**
     * 获取下一级的文件列表
     * @param context
     * @return
     */
    List<UserFileVO> listChildFile(QueryChildFileListContext context);


    /**
     * 保存文件列表到我的网盘
     * @param context
     */
    void saveFileList(SaveShareFileContext context);

    /**
     * 分享文件下载
     * @param context
     */
    void shareDownload(ShareDownloadContext context);

    /**
     * 根据文件列表刷新分享的状态信息
     * @param allDeleteFileList
     */
    void refreshShareStatus(List<MPanUserFile> allDeleteFileList);

    List<Long> rollingGetShareId(Long startId, Long limit);
}
