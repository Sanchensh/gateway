package io.gateway.util;

import io.gateway.common.SessionContext;
import io.netty.util.ReferenceCountUtil;
import io.gateway.exception.GatewayServerException;
import io.gateway.server.ProxyRunner;
import io.gateway.timer.TimerController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteBufManager {

    public static void deepSafeRelease(Object msg) {
        ReferenceCountUtil.release(msg);
    }

    /**
     * 异常时候关闭channel
     * @param sessionContext
     * @param customException
     */
    public static void close(SessionContext sessionContext, GatewayServerException customException) {
        TimerController.stopTimer(sessionContext);//关闭超时
        ProxyRunner.errorProcess(sessionContext, customException);
    }
}
