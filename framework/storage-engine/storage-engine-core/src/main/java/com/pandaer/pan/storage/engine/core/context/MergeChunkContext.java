package com.pandaer.pan.storage.engine.core.context;

import lombok.Data;

import java.util.List;

@Data
public class MergeChunkContext {

    private String filename;

    private String identifier;

    private Long totalSize;

    private List<String> chunkPathList;

    private String realFilePath;
}
