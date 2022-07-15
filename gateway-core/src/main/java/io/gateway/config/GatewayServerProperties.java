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
    /**
     * 客户端连接gateway的超时时间
     */
    private int connectTimeout = 5;
    /**
     * 客户端重试次数
     */
    private int retry = 0;
    /**
     * 空闲时间
     */
    private int idle = 10;

    private int maxInitialSize = 8;

    private int maxHeaderSize = 8;

    private int maxChunkSize = 8;

    private int clientPoolSize = 64;

    private Boolean validHeader = true;

    public void check() {
        if (this.boss <= 0) {
            throw new IllegalArgumentException("The size of boss thread pool must be > 0");
        }
        if (this.work <= 0) {
            throw new IllegalArgumentException("The size of work thread pool must be > 0");
        }
        if (this.connectTimeout <= 0) {
            throw new IllegalArgumentException("The size of work thread pool must be > 0");
        }
        if (this.contentLength <= 0) {
            throw new IllegalArgumentException("The max content-length must be > 0");
        }
        if (this.retry < 0) {
            throw new IllegalArgumentException("The parameter of retry must be >= 0");
        }
        if (this.clientPoolSize <= 0) {
            throw new IllegalArgumentException("Client pool size must be > 0");
        }
    }
}
