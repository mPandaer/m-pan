package com.pandaer.pan.server.modules.share.context;

import com.pandaer.pan.server.modules.share.domain.MPanShare;
import lombok.Data;

@Data
public class CheckShareCodeContext {
    private Long shareId;

    private String shareCode;

    private MPanShare share;
}
