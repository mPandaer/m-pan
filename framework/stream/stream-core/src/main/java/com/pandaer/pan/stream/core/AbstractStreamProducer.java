package com.pandaer.pan.stream.core;

import com.pandaer.pan.core.exception.MPanFrameworkException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 消息发送者的顶级父类
 */
public abstract class AbstractStreamProducer implements IStreamProducer {

    private Map<String, MessageChannel> channelMap;

    @Autowired
    public void setChannelMap(Map<String, MessageChannel> channelMap) {
        this.channelMap = channelMap;
    }

    @Override
    public boolean sendMessage(String channelName, Object deploy) {
        return sendMessage(channelName, deploy,new HashMap<>());
    }

    /**
     * 发送消息的抽象方法
     * 1. 参数校验
     * 2. 发送前的钩子函数
     * 3. 发送消息
     * 4. 发送后的钩子函数
     * @param channelName 通道的名字
     * @param deploy 消息载体
     * @param headers 消息头(消息元信息)
     * @return
     */
    @Override
    public boolean sendMessage(String channelName, Object deploy, Map<String, Object> headers) {
        if (StringUtils.isBlank(channelName) || Objects.isNull(deploy)) {
            throw new MPanFrameworkException("channelName and deploy can not be empty");
        }
        if (Objects.isNull(channelMap)) {
            throw new MPanFrameworkException("channelMap can not be found");
        }
        MessageChannel channel = channelMap.get(channelName);
        if (Objects.isNull(channel)) {
            throw new MPanFrameworkException("channel can not be empty");
        }
        Message<Object> message = MessageBuilder.createMessage(deploy, new MessageHeaders(headers));
        preSend(message);
        boolean res = channel.send(message);
        afterSend(message,res);
        return res;
    }

    protected abstract void afterSend(Message<Object> message, boolean res);

    protected abstract void preSend(Message<Object> message);
}
