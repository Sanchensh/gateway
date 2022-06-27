package io.gateway.common;

import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelId;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SessionContext {
    private String key = DefaultChannelId.newInstance().asLongText();
    private Channel serverChannel;
    private Channel clientChannel;
    private FullHttpRequest request;
    private String targetURL;
    private long timeout;

    public SessionContext(long timeout, Channel serverChannel) {
        this.timeout = timeout;
        this.serverChannel = serverChannel;
    }

    public SessionContext(Channel serverChannel) {
        this(5 * 1000, serverChannel);
    }
}
