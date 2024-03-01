package com.pandaer.pan.server.modules.file.vo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pandaer.pan.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("文件夹树节点")
public class FolderTreeNodeVO implements Serializable {

    @ApiModelProperty("文件夹id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    @ApiModelProperty("父文件夹id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    @ApiModelProperty("文件夹名称")
    private String label;

    @ApiModelProperty("子文件夹")
    private List<FolderTreeNodeVO> children = new ArrayList<>();

    public void print() {
        String jsonString = JSON.toJSONString(this);
        System.out.println(jsonString);
    }
}
