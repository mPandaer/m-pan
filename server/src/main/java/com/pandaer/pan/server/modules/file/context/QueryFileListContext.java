package com.pandaer.pan.server.modules.file.context;

import lombok.Data;

import java.util.List;

@Data
public class QueryFileListContext {

    private Long parentId;

    private List<Integer> fileTypeList;

    private Long userId;

    private Integer delFlag; //兼容后面的回收站的列表查询
}
