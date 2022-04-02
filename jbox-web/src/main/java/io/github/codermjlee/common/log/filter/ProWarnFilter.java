package io.github.codermjlee.common.log.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @author MJ
 */
public class ProWarnFilter extends LevelFilter {
    public ProWarnFilter() {
        setLevel(Level.WARN);
        setOnMatch(FilterReply.ACCEPT);
        setOnMismatch(FilterReply.DENY);
    }
}
