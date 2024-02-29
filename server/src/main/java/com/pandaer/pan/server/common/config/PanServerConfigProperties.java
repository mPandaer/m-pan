package com.pandaer.pan.server.common.config;

import com.pandaer.pan.core.constants.MPanConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "pan.server")
public class PanServerConfigProperties {

    private final Integer chunkFileExpirationDays = MPanConstants.ONE_INT;
}
