package com.mj.common.util.binary;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class TripleDeses {
    private static final String Algorithm = "DESede"; // 定义 加密算法,可用
    // DES,DESede,Blowfish
    private static final byte[] DEFAULT_KEY = {0x11, 0x22, 0x4F, 0x58,
        (byte) 0x88, 0x38, 0x28, 0x25, 0x25, 0x79, 0x51, (byte) 0xCB, 0x30,
        0x40, 0x36, 0x28, 0x35, 0x29, 0x11, 0x4B, 0x40, (byte) 0xE8, 0x76,
        0x68}; // 24字节的密钥

    // keybyte为加密密钥，长度为24字节
    // src为被加密的数据缓冲区（源）
    public static byte[] encryptMode(byte[] keybyte, byte[] src) {
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

            // 加密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // keybyte为加密密钥，长度为24字节
    // src为加密后的缓冲区
    public static byte[] decryptMode(byte[] keybyte, byte[] src) {
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

            // 解密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 转换成十六进制字符串
    public static String byte2ReadableHex(byte[] b, int len) {
        return byte2hex(b, len, true);
    }

    // 转换成十六进制字符串
    public static String byte2hex(byte[] b) {
        int len = (b == null) ? 0 : b.length;
        return byte2hex(b, len, false);
    }

    private static String byte2hex(byte[] b, int len, boolean readable) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; n < len; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
            if (readable) {
                hs.append(" ");
            }
        }
        stmp = hs.toString().toUpperCase();
        return stmp;
    }

    public static byte[] hex2byte(String hex) {
        int cnt = hex.length() / 2;
        byte[] ret = new byte[cnt];
        for (int i = 0; i < ret.length; ++i) {
            String h = hex.substring(i * 2, i * 2 + 2);
            int x = Integer.parseInt(h, 16);
            if (x > 128) {
                x = x - 256;
            }
            ret[i] = (byte) x;
        }
        return ret;
    }

    public static String decrypt(String line, byte[] key) {
        return new String(decryptMode(key, hex2byte(line)));
    }

    public static String encrypt(String line, byte[] key) {
        return byte2hex(encryptMode(key, line.getBytes()));
    }

    public static String encrypt(String line) {
        return encrypt(line, DEFAULT_KEY);
    }

    public static String decrypt(String line) {
        return decrypt(line, DEFAULT_KEY);
    }
}
