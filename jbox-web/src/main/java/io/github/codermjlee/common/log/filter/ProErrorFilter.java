package io.github.codermjlee.common.log.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @author MJ
 */
public class ProErrorFilter extends LevelFilter {
    public ProErrorFilter() {
        setLevel(Level.ERROR);
        setOnMatch(FilterReply.ACCEPT);
        setOnMismatch(FilterReply.DENY);
    }
}
