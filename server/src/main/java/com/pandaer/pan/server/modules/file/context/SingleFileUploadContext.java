package com.pandaer.pan.server.modules.file.context;

import com.pandaer.pan.server.modules.file.domain.MPanFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class SingleFileUploadContext {

    private Long parentId;

    private String filename;

    private String identifier;

    private Long totalSize;

    private MultipartFile fileData;

    private Long userId;

    private MPanFile realFileEntity;

}
