package com.pandaer.pan.server.modules.share.context;

import com.pandaer.pan.server.modules.share.domain.MPanShare;
import com.pandaer.pan.server.modules.share.vo.ShareSimpleInfoVO;
import lombok.Data;

/**
 * 获取分享的简略信息的上下文对象
 */
@Data
public class ShareSimpleInfoContext {


    /**
     * 分享记录的ID
     */
    private Long shareId;

    /**
     * 分享记录实体对象
     */
    private MPanShare records;

    /**
     * 分享简略信息实体对象
     */
    private ShareSimpleInfoVO vo;

}
