package com.pandaer.pan.server.modules.file.context;

import com.pandaer.pan.server.modules.file.domain.MPanFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SaveFileContext {
    private String filename;

    private String identifier;

    private Long totalSize;

    private MultipartFile fileData;

    private String realPath;

    private Long userId;

    private MPanFile realFileEntity;
}
