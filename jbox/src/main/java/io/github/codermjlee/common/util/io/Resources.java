package io.github.codermjlee.common.util.io;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author MJ
 */
public class Resources {
    public static InputStream getInputStream(String path) {
        if (path == null) return null;
        try {
            return new ClassPathResource(path).getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public static String getString(String path) {
        InputStream is = getInputStream(path);
        try {
            return is == null ? null : IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }
}
