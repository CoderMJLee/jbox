package io.github.codermjlee.common.log.encoder;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author MJ
 */
public class DevEncoder extends PatternLayoutEncoder {
    public DevEncoder() {
        setCharset(StandardCharsets.UTF_8);
        setPattern("%d{HH:mm:ss.SSS} [%highlight(%-5p)] [%t] %red(%c{0}): %m%n");
    }
}
