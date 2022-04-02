package io.github.codermjlee.netty.prop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author MJ
 */
@ConfigurationProperties("mj-netty")
@Component
@Getter
@Setter
public class MJNettyProp {
    private Ws ws;

    @Getter
    @Setter
    public static class Ws {
        private long timeout;
        private int port;
    }
}
