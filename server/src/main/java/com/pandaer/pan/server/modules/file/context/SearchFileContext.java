package com.pandaer.pan.server.modules.file.context;

import lombok.Data;

import java.util.List;

@Data
public class SearchFileContext {

    private String keyword;

    private List<Integer> fileTypeList;

    private Long userId;
}
