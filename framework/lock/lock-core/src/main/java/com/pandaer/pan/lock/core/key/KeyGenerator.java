package com.pandaer.pan.lock.core.key;

import com.pandaer.pan.lock.core.LockContext;

public interface KeyGenerator {

    /**
     * 生成锁的key
     * @param context
     * @return
     */
    String generateKey(LockContext context);
}
