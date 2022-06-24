package io.gateway.config;

import lombok.Data;

@Data
public class GatewayServerProperties {
    /**
     * 设置服务端口
     */
    private int port = 8080;
    /**
     * 服务端boss线程基数：规则为 核数 * master
     */
    private int boss = 1;
    /**
     * 服务端worker线程基数
     */
    private int work = 2;
    /**
     * 请求的超时时间设置
     */
    private int timeout = 10;
    /**
     * POST 请求体的最大长度，1024 * 1024 * contentLength
     */
    private int contentLength = 64;
    /**
     * 调用服务的线程基数
     */
    private int client = 2;

    private int connectTimeout = 5;
}
