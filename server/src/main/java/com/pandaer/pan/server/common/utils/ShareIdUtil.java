package com.pandaer.pan.server.common.utils;



public class ShareIdUtil {
    private static final ThreadLocal<Long> SHARE_ID = new ThreadLocal<>();

    public static void setShareId(Long shareId) {
        SHARE_ID.set(shareId);
    }

    public static Long getShareId() {
        Long shareId = SHARE_ID.get();
        if (shareId == null) {
            return -1L;
        }
        return shareId;
    }
}
