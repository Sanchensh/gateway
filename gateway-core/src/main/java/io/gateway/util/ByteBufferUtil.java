package io.gateway.util;

import io.gateway.common.SessionContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;

import java.util.Objects;

public class ByteBufferUtil {

    public static void safeRelease(SessionContext sessionContext) {
        safeRelease(sessionContext.getRequest());
    }

    public static void safeRelease(FullHttpRequest request) {
        if (Objects.nonNull(request) && request.refCnt() > 0) {
            ReferenceCountUtil.safeRelease(request);
        }
    }
}
