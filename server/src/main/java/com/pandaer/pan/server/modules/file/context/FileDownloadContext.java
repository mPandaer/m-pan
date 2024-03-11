package com.pandaer.pan.server.modules.file.context;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;

@Data
public class FileDownloadContext {

    private Long fileId;

    private HttpServletResponse response;

    private Long userId;
}
