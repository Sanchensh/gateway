package io.sufe.gateway.thread;

import java.util.concurrent.*;

import static io.sufe.gateway.common.Constants.*;

public class GatewayThreadPool {
    private static final ProxyRejectHandler handler = new ProxyRejectHandler();
    private static final BlockingQueue queue = new LinkedBlockingQueue(DEFAULT_QUEUE_SIZE);
    //使用线程池将请求放入线程池中处理，服务器规格16c32g，则16<<6=1024
    public static final ThreadPoolExecutor ThreadPool = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() << 1,
            Runtime.getRuntime().availableProcessors() << 2,
            DEFAULT_KEEPALIVE_TIME,
            TimeUnit.SECONDS,
            queue,
            GatewayThreadFactory.create(APPLICATION_NAME, true), handler);

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
