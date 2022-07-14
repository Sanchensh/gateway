package io.gateway.common;

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
    private long timeout = 5;

    public SessionContext(Channel serverChannel, FullHttpRequest request) {
        String timeout = request.headers().get(Constants.TIMEOUT);
        if (StringUtils.isNumeric(timeout)) {
            long t = Long.parseLong(timeout);
            this.timeout = t > 0 ? t : 5;
        }
        this.serverChannel = serverChannel;
        this.request = request;
        this.key = DefaultChannelId.newInstance().asLongText();
    }
}
