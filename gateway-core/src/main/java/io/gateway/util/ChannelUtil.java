/*
 * Copyright (c) 2014 AsyncHttpClient Project. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at
 *     http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package io.gateway.util;

import io.gateway.common.Constants;
import io.gateway.common.SessionContext;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.Objects;

import static io.gateway.common.Constants.SESSION_ATTRIBUTE;


/**
 * Channel工具类
 */
public class ChannelUtil {

    public static final AttributeKey<SessionContext> attributeKey = AttributeKey.valueOf(SESSION_ATTRIBUTE);

    //获取SessionContext
    public static SessionContext getSessionContext(Channel channel) {
        if (Objects.isNull(channel)){
            return null;
        }
        return channel.attr(attributeKey).get();
    }

    //设置ChannelAttribute属性
    public static void attributeSessionContext(Channel channel, SessionContext sessionContext) {
        sessionContext.setClientChannel(channel);
        Attribute<SessionContext> attr = channel.attr(attributeKey);
        attr.set(sessionContext);
    }

    //清除SessionContext
    public static void clearSessionContext(Channel channel) {
        if (Objects.nonNull(channel)) {
            channel.attr(attributeKey).set(null);
        }
    }

    /**
     * 获取host => ip + ":" + port
     *
     * @param channel 目标channel
     * @return ip + ":" + port
     */
    public static String host(Channel channel) {
        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        return address.getHostString() + Constants.COLON + address.getPort();
    }
}
