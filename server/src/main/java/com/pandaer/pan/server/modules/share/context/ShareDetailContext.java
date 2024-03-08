package com.pandaer.pan.server.modules.share.context;

import com.pandaer.pan.server.modules.share.domain.MPanShare;
import com.pandaer.pan.server.modules.share.vo.ShareDetailVO;
import lombok.Data;

@Data
public class ShareDetailContext {

    private Long shareId;

    private MPanShare share;

    private ShareDetailVO vo;
}
