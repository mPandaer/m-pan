package com.pandaer.pan.storage.engine.core.context;

import lombok.Data;

import java.io.InputStream;

@Data
public class StoreFileContext {

    private String filename;

    private Long totalSize;

    private InputStream inputStream;

    private String realPath;

}
