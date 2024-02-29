package com.pandaer.pan.server.modules.file.context;


import com.pandaer.pan.server.modules.file.domain.MPanFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class SecFileUploadContext {

    /**
     * 解密后的文件夹ID
     */
    private Long parentId;

    private String filename;

    private String identifier;

    private Long userId;

    private MPanFile realFileEntity;
}
