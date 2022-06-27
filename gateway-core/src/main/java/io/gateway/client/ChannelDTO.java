package io.gateway.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChannelDTO {
    private Channel channel;
    private Bootstrap bootstrap;
}