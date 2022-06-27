package io.gateway.common;

/**
 * @author jlxu@telenav.cn
 * @date 2021/7/28/18:11
 */
public class Constants {
    public static final String SESSION_ATTRIBUTE = "session_attribute";
    public static final String GATEWAY_CLIENT_BOSS_NAME = "gateway_client_boss_name";
    public static final String GATEWAY_SERVER_BOSS_NAME = "gateway_boss_thread";
    public static final String GATEWAY_SERVER_WORK_NAME = "gateway_work_thread";
    public static final String COLON = ":";
    public static final String TIMEOUT = "timeout";
    public static final String APPLICATION_NAME = "infu-gateway";
    public static final String SHORT_LINE = "-";
    public static final int CLIENT_MAX_CONTENT_LENGTH = 1024 * 1024 * 64;
    public static final int SERVER_MAX_CONTENT_LENGTH = CLIENT_MAX_CONTENT_LENGTH;
    public static final int ALL_MAX_SIZE = 8192;
    public static final int ZERO = 0;
    public static final int DEFAULT_QUEUE_SIZE = 100;
    public static final int DEFAULT_KEEPALIVE_TIME = 60;
    public static final int DEFAULT_TICK_DURATION = 1;
    public final static String PRE_FILTER_NAME = "gateway_filter_name_";
}
