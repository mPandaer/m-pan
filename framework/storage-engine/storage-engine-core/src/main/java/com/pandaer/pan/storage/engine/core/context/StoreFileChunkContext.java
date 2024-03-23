package com.pandaer.pan.storage.engine.core.context;

import lombok.Data;

import java.io.InputStream;

@Data
public class StoreFileChunkContext {

    private String identifier;

    private InputStream inputStream;

    private Integer chunkNumber;

    private Long currentChunkSize;

    private String realPath;

    private Integer totalChunks;

    private Long userId;

    private String filename;
}
