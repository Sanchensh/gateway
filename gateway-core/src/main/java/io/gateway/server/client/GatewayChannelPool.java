package io.gateway.server.client;

import io.gateway.config.GatewayServerProperties;
import io.gateway.exception.GatewayServerException;
import io.gateway.util.ChannelUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.collections.Pair;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
public enum GatewayChannelPool {
    //单例
    instance;
    private static final ConcurrentHashMap<String, ConcurrentLinkedDeque<Channel>> channelPool = new ConcurrentHashMap<>();
    private GatewayServerProperties properties;
    private HttpClient httpClient;
    private int poolSize;
    private int dividePoolSize;

    /**
     * @param channel 使用过的Channel
     * @param host    ip + ":" + port 也就是host
     */
    public void offer(Channel channel, String host) {
        ConcurrentLinkedDeque<Channel> channels = channelPool.get(host);
        if (channels == null) {
            channels = new ConcurrentLinkedDeque<>();
            channels.offerFirst(channel);
            channelPool.put(host, channels);
            return;
        }
        if (channels.size() < poolSize) { //成功将连接放回池中
            channels.offerFirst(channel);
        } else {//连接放回池中失败，则需要清除所有属性，避免jvm报错——GC overhead limit exceeded
            channel.closeFuture();
        }
    }

    /**
     * 删除池中指定的channel
     *
     * @param channel 指定的channel
     */
    public void removeChannel(Channel channel) {
        ConcurrentLinkedDeque<Channel> channels = channelPool.get(ChannelUtil.host(channel));
        if (!CollectionUtils.isEmpty(channels)) {
            channels.removeIf(chan -> (!chan.isActive() || !chan.isOpen() || !chan.isWritable()) &&
                    chan.id().asLongText().equals(channel.id().asLongText()));
        }
    }

    /**
     * 获取channel，
     *
     * @param ip   目标IP
     * @param port 目标端口
     * @return 如果是新连接，则返回ChannelFuture；如果是已有连接，则返回Channel
     */
    public Pair<Channel, Bootstrap> poll(String ip, int port, String host) {
        ConcurrentLinkedDeque<Channel> channels = channelPool.get(host);
        Channel channel = null;
        int i = 0;
        if (!CollectionUtils.isEmpty(channels)) {
            while (Objects.isNull(channel) && i++ < dividePoolSize) {
                channel = channels.pollFirst();
                if (Objects.isNull(channel)) {
                    break;
                }
                // 是否是active状态，可能节点挂掉
                if (!channel.isActive() || !channel.isOpen()) {
                    channel = null;
                }
                // 这里防止多线程情况下channel中的SessionContext被覆盖了
                if (Objects.nonNull(ChannelUtil.getSessionContext(channel))) {
                    channel = null;
                }
            }
        }
        if (Objects.isNull(channel) || !channel.isActive() || !channel.isOpen()) {
            if (Objects.isNull(httpClient)) {
                throw new GatewayServerException("Please initialize the http client");
            }
            return Pair.create(null, httpClient.newChannel(ip, port));
        }
        return Pair.create(channel, null);
    }

    public void init(GatewayServerProperties properties) {
        if (Objects.isNull(this.properties)) {
            this.properties = properties;
            poolSize = Math.max(properties.getClientPoolSize(), 32);
            dividePoolSize = poolSize >= 32 ? poolSize >> 1 : 16;
        }

        if (Objects.isNull(httpClient)) {
            this.httpClient = new HttpClient(properties);
        }
    }
}
