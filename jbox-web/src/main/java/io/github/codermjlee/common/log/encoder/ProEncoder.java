package io.github.codermjlee.common.log.encoder;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author MJ
 */
public class ProEncoder extends PatternLayoutEncoder {
    public ProEncoder() {
        setCharset(StandardCharsets.UTF_8);
        setPattern("%d{HH:mm:ss.SSS} %c{0}: %m%n");
    }
}
