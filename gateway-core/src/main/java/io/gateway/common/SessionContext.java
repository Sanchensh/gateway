package io.gateway.common;

import io.gateway.timer.HandleTimeout;
import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelId;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Data
@ToString
public class SessionContext {
    private String key;
    private Channel serverChannel;
    private Channel clientChannel;
    private FullHttpRequest request;
    private String targetURL;
    private long timeout;

    public SessionContext(Channel serverChannel, FullHttpRequest request) {
        String timeout = request.headers().get(Constants.TIMEOUT);
        this.timeout = StringUtils.isNumeric(timeout) ? Long.parseLong(timeout) : 5 * 1000;
        this.serverChannel = serverChannel;
        this.request = request;
        this.key = DefaultChannelId.newInstance().asLongText();
    }
}
