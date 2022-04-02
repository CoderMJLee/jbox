package io.github.codermjlee.common.prop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author MJ
 */
@ConfigurationProperties("mj-web")
@Component
@Getter
@Setter
public class MJWebProp {
    private Path path;

    @Getter
    @Setter
    public static class Path {
        private String base;
        private String open;
        private String log;
        private String tmp;
        private String download;
    }
}
