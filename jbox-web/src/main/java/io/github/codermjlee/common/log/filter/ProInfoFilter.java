package io.github.codermjlee.common.log.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @author MJ
 */
public class ProInfoFilter extends LevelFilter {
    public ProInfoFilter() {
        setLevel(Level.INFO);
        setOnMatch(FilterReply.ACCEPT);
        setOnMismatch(FilterReply.DENY);
    }
}
