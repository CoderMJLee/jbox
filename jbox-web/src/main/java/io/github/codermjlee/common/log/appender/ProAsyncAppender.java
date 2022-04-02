package io.github.codermjlee.common.log.appender;

import ch.qos.logback.classic.AsyncAppender;

/**
 * @author MJ
 */
public class ProAsyncAppender extends AsyncAppender {
    public ProAsyncAppender() {
        setQueueSize(1024);
        setDiscardingThreshold(0);
    }
}
