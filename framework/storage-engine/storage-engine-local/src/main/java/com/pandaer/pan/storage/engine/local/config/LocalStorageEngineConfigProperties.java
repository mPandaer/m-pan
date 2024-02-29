package com.pandaer.pan.storage.engine.local.config;

import com.pandaer.pan.core.utils.FileUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("pan.storage.engine.local")
public class LocalStorageEngineConfigProperties {

    private String basePath = FileUtil.genDefaultBasePath();
    private String chunkBasePath = FileUtil.genDefaultChunkBasePath();
}
