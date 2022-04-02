package io.github.codermjlee.common.util.binary;

import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * @author MJ
 */
public class MD5s {
    public static String md5(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int len;
            while ((len = fis.read(buf)) != -1) {
                md5.update(buf, 0, len);
            }
            return new String(Hex.encodeHex(md5.digest()));
        } catch (Exception e) {
            return null;
        }
    }

    public static String md5(String text) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(text.getBytes());
            return new String(Hex.encodeHex(md5.digest()));
        } catch (Exception e) {
            return null;
        }
    }
}
