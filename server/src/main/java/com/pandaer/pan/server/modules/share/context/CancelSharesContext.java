package com.pandaer.pan.server.modules.share.context;

import lombok.Data;

import java.util.List;

@Data
public class CancelSharesContext {

    private List<Long> shareIdList;

    private Long userId;
}
