package com.pandaer.pan.server.modules.share.converter;


import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import com.pandaer.pan.server.modules.share.context.BatchSaveShareFileContext;
import com.pandaer.pan.server.modules.share.context.CancelSharesContext;
import com.pandaer.pan.server.modules.share.context.CreateShareUrlContext;
import com.pandaer.pan.server.modules.share.domain.MPanShare;
import com.pandaer.pan.server.modules.share.po.CancelSharesPO;
import com.pandaer.pan.server.modules.share.po.CreateShareUrlPO;
import com.pandaer.pan.server.modules.share.vo.MPanShareUrlListVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ShareConverter {

    @Mapping(target = "userId",expression = "java(com.pandaer.pan.server.common.utils.UserIdUtil.getUserId())")
    @Mapping(target = "shareFileIdList",expression = "java(createShareUrlPO.getShareFileIdList().stream().map(com.pandaer.pan.core.utils.IdUtil::decrypt).collect(java.util.stream.Collectors.toList()))")
    CreateShareUrlContext PO2ContextInCreateShareUrl(CreateShareUrlPO createShareUrlPO);


    @Mapping(target = "shareId",expression = "java(context.getShareRecord().getShareId())")
    @Mapping(target = "fileIdList",source = "context.shareFileIdList")
    BatchSaveShareFileContext context2ContextInSaveShareFile(CreateShareUrlContext context);

    MPanShareUrlListVO entity2VOInListShare(MPanShare mPanShare);


    @Mapping(target = "userId",expression = "java(com.pandaer.pan.server.common.utils.UserIdUtil.getUserId())")
    @Mapping(target = "shareIdList",expression = "java(cancelSharesPO.getShareIdList().stream().map(com.pandaer.pan.core.utils.IdUtil::decrypt).collect(java.util.stream.Collectors.toList()))")
    CancelSharesContext PO2ContextInCancelShares(CancelSharesPO cancelSharesPO);

    UserFileVO entity2VOInGetDetailInfo(MPanUserFile mPanUserFile);
}
