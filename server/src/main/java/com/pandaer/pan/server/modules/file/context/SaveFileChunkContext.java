package com.pandaer.pan.server.modules.file.context;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SaveFileChunkContext {

    private String identifier;

    /**
     * 当前分片序号 从1开始
     */
    private Integer chunkNumber;

    private Long currentChunkSize;

    private MultipartFile file;

    private Long userId;

    private Integer totalChunks;

    private Integer merge;

    private String filename;


    private String realPath;
}
