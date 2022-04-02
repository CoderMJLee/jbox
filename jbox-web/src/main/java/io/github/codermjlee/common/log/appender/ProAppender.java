package io.github.codermjlee.common.log.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.rolling.RollingFileAppender;
import io.github.codermjlee.common.log.Filenames;

/**
 * @author MJ
 */
public abstract class ProAppender extends RollingFileAppender<ILoggingEvent> {

    @Override
    public void setContext(Context context) {
        super.setContext(context);

        setFile(Filenames.filepath(this, context) + ".log");
    }
}
