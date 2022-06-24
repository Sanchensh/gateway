package io.gateway.thread;

import io.gateway.common.Constants;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class GatewayThreadFactory implements ThreadFactory {

    private static final ThreadGroup threadGroup = new ThreadGroup(Constants.APPLICATION_NAME);

    private final AtomicLong threadNumber = new AtomicLong(1);

    private final String namePrefix;

    private final boolean daemon;

    public static ThreadFactory create(String namePrefix, boolean daemon) {
        return new GatewayThreadFactory(namePrefix, daemon);
    }

    private GatewayThreadFactory(String namePrefix, boolean daemon) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(threadGroup, r, threadGroup.getName() + Constants.SHORT_LINE + namePrefix + Constants.SHORT_LINE + threadNumber.getAndIncrement());
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        thread.setDaemon(daemon);
        return thread;
    }
}
