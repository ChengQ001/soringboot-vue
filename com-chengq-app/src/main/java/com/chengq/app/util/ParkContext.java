package com.chengq.app.util;

/**
 * 当前请求选中的园区（由 {@code X-Park-Id} 请求头解析，在线程内有效）
 */
public final class ParkContext {

    private static final ThreadLocal<Long> CURRENT = new ThreadLocal<>();

    private ParkContext() {}

    public static void setParkId(Long parkId) {
        CURRENT.set(parkId);
    }

    public static Long getParkId() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
