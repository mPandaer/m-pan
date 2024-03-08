package com.pandaer.pan.server.modules.share.context;

import com.pandaer.pan.server.modules.share.domain.MPanShare;
import lombok.Data;
import java.util.List;

@Data
public class CreateShareUrlContext {

    /**
     * 分享名称
     */
    private String shareName;

    /**
     * 分享类型（0 有提取码）
     */
    private Integer shareType;

    /**
     * 分享时间类型（0 永久有效；1 7天有效；2 30天有效）
     */
    private Integer shareDayType;


    /**
     * 分享的文件ID列表
     */
    private List<Long> shareFileIdList;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 保存的分享记录
     */
    private MPanShare shareRecord;
}
