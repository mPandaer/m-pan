package com.pandaer.pan.server.common.stream.event;

import lombok.Data;

import java.io.Serializable;

@Data
public class TestEvent implements Serializable {

    private String message;
}
