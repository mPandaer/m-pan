package com.pandaer.pan.storage.engine.core.context;

import lombok.Data;

import java.io.InputStream;

@Data
public class StoreFileChunkContext {

    private String identifier;

    private InputStream inputStream;

    private Integer currentChunkNumber;

    private Long currentChunkSize;

    private String realPath;
}
