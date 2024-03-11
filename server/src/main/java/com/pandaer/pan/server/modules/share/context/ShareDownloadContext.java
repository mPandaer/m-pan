package com.pandaer.pan.server.modules.share.context;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;

@Data
public class ShareDownloadContext {
    private Long shareId;

    private Long fileId;

    private Long userId;

    private HttpServletResponse response;
}
