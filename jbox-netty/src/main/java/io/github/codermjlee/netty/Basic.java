package io.github.codermjlee.netty;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author MJ
 */
public abstract class Basic {
    protected static final LoggingHandler LOG_HANDLER = new LoggingHandler(LogLevel.INFO);
    /**
     * 启动
     */
    public abstract void start();

    /**
     * 关闭连接
     */
    public abstract void stop();
}
