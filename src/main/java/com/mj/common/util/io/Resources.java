package com.mj.common.util.io;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

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
}
