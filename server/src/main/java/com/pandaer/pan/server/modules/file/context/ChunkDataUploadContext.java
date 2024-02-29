package com.pandaer.pan.server.modules.file.context;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChunkDataUploadContext {

    private String filename;

    private String identifier;

    private Long totalSize;

    private Integer totalChunks;

    /**
     * 当前分片序号 从1开始
     */
    private Integer currentChunkNumber;

    private Long currentChunkSize;

    private MultipartFile fileData;

    private Long userId;
}
