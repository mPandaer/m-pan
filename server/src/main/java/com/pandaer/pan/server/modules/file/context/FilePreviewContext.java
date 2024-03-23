package com.pandaer.pan.server.modules.file.context;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;

@Data
public class FilePreviewContext {

    private Long fileId;

    private HttpServletResponse response;

    private Long userId;
}
