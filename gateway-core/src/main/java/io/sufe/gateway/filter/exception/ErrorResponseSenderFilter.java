package io.sufe.gateway.filter.exception;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.sufe.gateway.common.SessionContext;
import io.sufe.gateway.filter.AbstractFilter;
import io.sufe.gateway.filter.AbstractFilterContext;
import lombok.extern.slf4j.Slf4j;

import static io.netty.channel.ChannelFutureListener.CLOSE;

//直接打印错误信息，需要转化为es错误格式
@Slf4j
public class ErrorResponseSenderFilter extends AbstractFilter {

    public static String DEFAULT_NAME = PRE_FILTER_NAME + ErrorResponseSenderFilter.class.getSimpleName().toUpperCase();

    @Override
    public String name() {
        return DEFAULT_NAME;
    }

    @Override
    public void run(AbstractFilterContext filterContext, SessionContext sessionContext) {
        // 保证只会发送一次
//        if (sessionContext.isBodySend()) {
//            sessionContext.getServerChannel().close();
//            return;
//        }
//        sessionContext.setBodySend(true);
//        ByteBuf body = sessionContext.getErrorResponseBody();
//        FullHttpResponse errorResponse = new DefaultFullHttpResponse(sessionContext.getResponseHttpVersion(), sessionContext.getHttpResponseStatus(), body == null ? Unpooled.EMPTY_BUFFER : body, false);;
//        errorResponse.headers().add(sessionContext.getResponseHttpHeaders());
//        sessionContext.getServerChannel().writeAndFlush(errorResponse)
//                .addListener(CLOSE)
//                .addListener(future -> SessionContextManager.SINGLETON.setSessionContext(sessionContext));
    }

}
