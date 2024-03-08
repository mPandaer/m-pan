package com.pandaer.pan.server.common.config;

import com.pandaer.pan.core.constants.MPanConstants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "pan.server")
public class PanServerConfigProperties {

    @Value("${server.port}")
    private Integer serverPort;

    private final Integer chunkFileExpirationDays = MPanConstants.ONE_INT;

    private final String sharePrefix = "http://localhost:"+serverPort+"/share/";
}
