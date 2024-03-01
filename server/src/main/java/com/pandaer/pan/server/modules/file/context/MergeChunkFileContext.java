package com.pandaer.pan.server.modules.file.context;



import com.pandaer.pan.server.modules.file.domain.MPanFileChunk;
import lombok.Data;

import java.util.List;

@Data
public class MergeChunkFileContext {


    private Long parentId;

    private String filename;

    private String identifier;

    private Long totalSize;

    private Long totalChunks;

    private Long userId;

    private Long realFileId;

    private List<MPanFileChunk> chunkList;

    private String realFilePath;

}
