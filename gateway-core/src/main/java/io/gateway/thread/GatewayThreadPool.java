package io.gateway.thread;

import io.gateway.common.Constants;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class GatewayThreadPool {
    private static final ProxyRejectHandler handler = new ProxyRejectHandler();
    private static final BlockingQueue queue = new LinkedBlockingQueue(Constants.DEFAULT_QUEUE_SIZE);
    //使用线程池将请求放入线程池中处理，服务器规格16c32g，则16<<6=1024
    public static final ThreadPoolExecutor ThreadPool = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() << 1,
            Runtime.getRuntime().availableProcessors() << 2,
            Constants.DEFAULT_KEEPALIVE_TIME,
            TimeUnit.SECONDS,
            queue,
            GatewayThreadFactory.create(Constants.APPLICATION_NAME, true), handler);

    static {
        ThreadPool.allowCoreThreadTimeOut(true);
    }

    private static class ProxyRejectHandler implements RejectedExecutionHandler {
        //重新执行
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            r.run();
        }
    }
}
