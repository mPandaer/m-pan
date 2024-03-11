package com.pandaer.pan.server.modules.share.converter;

import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.server.modules.share.context.BatchSaveShareFileContext;
import com.pandaer.pan.server.modules.share.context.CancelSharesContext;
import com.pandaer.pan.server.modules.share.context.CreateShareUrlContext;
import com.pandaer.pan.server.modules.share.context.SaveShareFileContext;
import com.pandaer.pan.server.modules.share.domain.MPanShare;
import com.pandaer.pan.server.modules.share.po.CancelSharesPO;
import com.pandaer.pan.server.modules.share.po.CreateShareUrlPO;
import com.pandaer.pan.server.modules.share.po.SaveShareFilePO;
import com.pandaer.pan.server.modules.share.vo.MPanShareUrlListVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-03-11T18:22:40+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 1.8.0_402 (Oracle Corporation)"
)
@Component
public class ShareConverterImpl implements ShareConverter {

    @Override
    public CreateShareUrlContext PO2ContextInCreateShareUrl(CreateShareUrlPO createShareUrlPO) {
        if ( createShareUrlPO == null ) {
            return null;
        }

        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();

        createShareUrlContext.setShareName( createShareUrlPO.getShareName() );
        createShareUrlContext.setShareType( createShareUrlPO.getShareType() );
        createShareUrlContext.setShareDayType( createShareUrlPO.getShareDayType() );

        createShareUrlContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );
        createShareUrlContext.setShareFileIdList( createShareUrlPO.getShareFileIdList().stream().map(com.pandaer.pan.core.utils.IdUtil::decrypt).collect(java.util.stream.Collectors.toList()) );

        return createShareUrlContext;
    }

    @Override
    public BatchSaveShareFileContext context2ContextInSaveShareFile(CreateShareUrlContext context) {
        if ( context == null ) {
            return null;
        }

        BatchSaveShareFileContext batchSaveShareFileContext = new BatchSaveShareFileContext();

        List<Long> list = context.getShareFileIdList();
        if ( list != null ) {
            batchSaveShareFileContext.setFileIdList( new ArrayList<Long>( list ) );
        }
        batchSaveShareFileContext.setUserId( context.getUserId() );

        batchSaveShareFileContext.setShareId( context.getShareRecord().getShareId() );

        return batchSaveShareFileContext;
    }

    @Override
    public MPanShareUrlListVO entity2VOInListShare(MPanShare mPanShare) {
        if ( mPanShare == null ) {
            return null;
        }

        MPanShareUrlListVO mPanShareUrlListVO = new MPanShareUrlListVO();

        mPanShareUrlListVO.setShareId( mPanShare.getShareId() );
        mPanShareUrlListVO.setShareName( mPanShare.getShareName() );
        mPanShareUrlListVO.setShareUrl( mPanShare.getShareUrl() );
        mPanShareUrlListVO.setShareCode( mPanShare.getShareCode() );
        mPanShareUrlListVO.setShareStatus( mPanShare.getShareStatus() );
        mPanShareUrlListVO.setShareDayType( mPanShare.getShareDayType() );
        mPanShareUrlListVO.setShareType( mPanShare.getShareType() );
        mPanShareUrlListVO.setShareEndTime( mPanShare.getShareEndTime() );
        mPanShareUrlListVO.setCreateTime( mPanShare.getCreateTime() );

        return mPanShareUrlListVO;
    }

    @Override
    public CancelSharesContext PO2ContextInCancelShares(CancelSharesPO cancelSharesPO) {
        if ( cancelSharesPO == null ) {
            return null;
        }

        CancelSharesContext cancelSharesContext = new CancelSharesContext();

        cancelSharesContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );
        cancelSharesContext.setShareIdList( cancelSharesPO.getShareIdList().stream().map(com.pandaer.pan.core.utils.IdUtil::decrypt).collect(java.util.stream.Collectors.toList()) );

        return cancelSharesContext;
    }

    @Override
    public UserFileVO entity2VOInGetDetailInfo(MPanUserFile mPanUserFile) {
        if ( mPanUserFile == null ) {
            return null;
        }

        UserFileVO userFileVO = new UserFileVO();

        userFileVO.setFileId( mPanUserFile.getFileId() );
        userFileVO.setParentId( mPanUserFile.getParentId() );
        userFileVO.setFilename( mPanUserFile.getFilename() );
        userFileVO.setFolderFlag( mPanUserFile.getFolderFlag() );
        userFileVO.setFileSizeDesc( mPanUserFile.getFileSizeDesc() );
        userFileVO.setFileType( mPanUserFile.getFileType() );
        userFileVO.setUpdateTime( mPanUserFile.getUpdateTime() );

        return userFileVO;
    }

    @Override
    public SaveShareFileContext PO2ContextInSaveFileList(SaveShareFilePO saveShareFilePO) {
        if ( saveShareFilePO == null ) {
            return null;
        }

        SaveShareFileContext saveShareFileContext = new SaveShareFileContext();

        saveShareFileContext.setShareId( com.pandaer.pan.server.common.utils.ShareIdUtil.getShareId() );
        saveShareFileContext.setUserId( com.pandaer.pan.server.common.utils.UserIdUtil.getUserId() );
        saveShareFileContext.setFileIdList( saveShareFilePO.getFileIdList().stream().map(com.pandaer.pan.core.utils.IdUtil::decrypt).collect(java.util.stream.Collectors.toList()) );
        saveShareFileContext.setTargetParentId( com.pandaer.pan.core.utils.IdUtil.decrypt(saveShareFilePO.getTargetParentId()) );

        return saveShareFileContext;
    }
}
