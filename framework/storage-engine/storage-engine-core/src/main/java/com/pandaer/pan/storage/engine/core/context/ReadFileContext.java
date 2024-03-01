package com.pandaer.pan.storage.engine.core.context;

import lombok.Data;

import java.io.OutputStream;

@Data
public class ReadFileContext {

    private String realFilePath;

    private OutputStream outputStream;
}
