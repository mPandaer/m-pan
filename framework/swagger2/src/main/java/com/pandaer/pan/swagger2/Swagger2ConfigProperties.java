package com.pandaer.pan.swagger2;

import com.pandaer.pan.core.constants.MPanConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "swagger2")
@Data
public class Swagger2ConfigProperties {
    // 是否启用swagger
    private boolean enable = true;

    // 文档信息
    private String title = "M-Pan API文档";
    private String description = "M-Pan API文档";
    private String version = "1.0";
    private String termsOfServiceUrl = "http://localhost:8080/doc.html";
    private String groupName = "M-Pan";

    // 作者信息
    private String contactName = "pandaer";
    private String contactUrl = "https://www.github.com/mPandaer";
    private String contactEmail = "liwenhaolx@gamil.com";

    // 扫描包路径
    private String basePackage = MPanConstants.BASE_COMPONENT_PACKAGE_PATH;

}
