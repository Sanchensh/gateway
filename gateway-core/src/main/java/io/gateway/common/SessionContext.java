package io.gateway.common;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Data;

@Data
public class SessionContext {
    private String key ;
    private Channel serverChannel;
    private Channel clientChannel;
    private Throwable throwable;
    private FullHttpRequest request;
    private String targetURL;
    private long timeout;
    public SessionContext(long timeout,Channel serverChannel){
        this.timeout = timeout;
        this.serverChannel = serverChannel;
    }
    public SessionContext(Channel serverChannel){
        this.serverChannel = serverChannel;
    }
}
