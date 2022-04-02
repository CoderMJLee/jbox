package io.github.codermjlee.common.util.binary;

import java.util.Date;
import java.util.Random;
import java.util.zip.CRC32;

/**
 * 字节处理
 *
 * // 小端模式
 * Bytes le = Bytes.alloc().order(Order.LE);
 *
 * // 大端模式
 * Bytes be = Bytes.alloc().order(Order.BE);
 *
 * int v = 333;
 * // {0x4D, 0x01, 0x00, 0x00}
 * le.get(v);
 *
 * // {0x00, 0x00, 0x01, 0x4D}
 * be.order(Order.BE).get(v);
 *
 * // 333
 * le.getInt(new byte[]{0x4D, 0x01, 0x00, 0x00});
 * le.getInt(new byte[]{0x4D, 0x01});
 * le.getInt(new byte[]{0x4D, 0x01, 0x00, 0x00}, 0, 2);
 *
 * // 333
 * be.getInt(new byte[]{0x00, 0x00, 0x01, 0x4D});
 * be.getInt(new byte[]{0x01, 0x4D});
 * be.getInt(new byte[]{0x00, 0x00, 0x01, 0x4D}, 2, 2);
 *
 * @author MJ
 */
public class Bytes {
    public static final int INT_LEN = Integer.SIZE / Byte.SIZE;
    private Order order = Order.LE;
    private Bytes() {}

    public static Bytes alloc() {
        return new Bytes();
    }

    public Bytes order(Order order) {
        this.order = order;
        return this;
    }

    /*
        // LE
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);

        // BE
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
     */
    public byte[] get(int v) {
        return get(v, INT_LEN);
    }

    public byte[] get(int v, int len) {
        if (len < 1) return null;
        len = Math.min(len, INT_LEN);
        byte[] bytes = new byte[len];
        // 大小端模式
        boolean le = order == Order.LE;
        // 初始化索引
        int i = le ? 0 : (bytes.length - 1);
        for (int pos = 0; pos < len; pos++) {
            bytes[i] = (byte) (v >> (pos << 3) & 0xFF);
            if (le) { // 小端模式
                i++;
            } else { // 大端模式
                i--;
            }
        }
        return bytes;
    }

    public int getInt(byte ...bytes) {
        return getInt(bytes, 0, bytes.length);
    }

    /*
    // LE
        int res = 0;
        for(int i=0;i<b.length;i++){
            res |= (b[i] & 0xff) << (i << 3);
        }

        // BE
        int res = 0;
        for(int i=0;i<b.length;i++){
            res |= (b[i] & 0xff) << ((3 - i) << 3);
        }
     */
    public int getInt(byte[] bytes, int offset, int len) {
        if (invalid(bytes, offset, len)) return 0;
        // 取最小值
        len = Math.min(len, INT_LEN);
        // 大小端模式
        boolean le = order == Order.LE;
        // 初始化索引
        int end = offset + len - 1;
        int i = le ? offset : end;
        int res = 0;
        int posMax = len * Byte.SIZE;
        for (int pos = 0; pos < posMax; pos += Byte.SIZE) {
            res |= (bytes[i] & 0xFF) << pos;
            if (le) { // 小端模式
                i++;
            } else { // 大端模式
                i--;
            }
        }
        return res;
    }

    public Bytes println(int v) {
        println(get(v));
        return this;
    }

    /*
     * 拷贝src的字节数组的前len个字节，到dest的destPos位置
     *
     * 假设len = 2
     * LE: {0x4D, 0x01, 0x00, 0x00}, srcPos = 0, {0x4D, 0x01}
     * BE: {0x00, 0x00, 0x01, 0x4D}, srcPos = 2, {0x01, 0x4D}
     */
    public Bytes copy(int src, byte[] dest, int destPos, int len) {
        if (invalid(dest, destPos, len)) return this;
        len = Math.min(len, INT_LEN);
        // 开始位置
        int srcPos = 0;
        if (order == Order.BE) { // 如果是大端模式
            srcPos = INT_LEN - len;
        }
        System.arraycopy(
                get(src), srcPos,
                dest, destPos, len);
        return this;
    }

    public static void println(byte[] bytes) {
        if (empty(bytes)) return;
        System.out.print("{");
        for (int i = 0; i < bytes.length; i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.format("0x%02X", bytes[i]);
        }
        System.out.println("}");
    }

    /*
     * 是否为空
     */
    public static boolean empty(byte[] bytes) {
        return bytes == null || bytes.length <= 0;
    }

    /*
     * 是否非法
     */
    public static boolean invalid(byte[] bytes, int offset, int len) {
        if (empty(bytes)) return true;
        if (offset < 0 || len < 1) return true;
        return offset + len > bytes.length;
        // 合法的情况：
        // len <= bytes.length - offset
        // offset <= bytes.length - len
    }

    /*
     * 截取[offset, offset + len)的字节数组
     */
    public static byte[] getBytes(byte[] bytes, int offset, int len) {
        if (invalid(bytes, offset, len)) return null;
        byte[] newBytes = new byte[len];
        System.arraycopy(bytes, offset, newBytes, 0, len);
        return newBytes;
    }

    public static boolean equals(byte[] bytes1, byte[] bytes2) {
        if (empty(bytes1) || empty(bytes2)) return false;
        if (bytes1.length != bytes2.length) return false;
        return equals(bytes1, 0, bytes2, 0, bytes1.length);
    }

    public static boolean equals(byte[] bytes1, int offset1, byte[] bytes2) {
        if (empty(bytes2)) return false;
        return equals(bytes1, offset1, bytes2, 0, bytes2.length);
    }

    /*
     * 若有一个为empty，返回的是false
     */
    public static boolean equals(byte[] bytes1, int offset1,
                                 byte[] bytes2, int offset2,
                                 int len) {
        if (invalid(bytes1, offset1, len)) return false;
        if (invalid(bytes2, offset2, len)) return false;

        for (int i = 0; i < len; i++) {
            if (bytes1[offset1 + i] != bytes2[offset2 + i]) {
                return false;
            }
        }

        return true;
    }

    public static int checksum(byte[] bytes) {
        return checksum(bytes, 0, bytes.length);
    }

    public static int checksum(byte[] bytes, int offset, int len) {
        if (invalid(bytes, offset, len)) return 0;
        CRC32 crc32 = new CRC32();
        crc32.update(bytes, offset, len);
        return (int) crc32.getValue();
    }

    /*
     * 生成一个随机的字节数组
     */
    public static byte[] randomBytes(int len) {
        if (len <= 0) return null;
        byte[] key = new byte[len];
        Random random = new Random(new Date().getTime());
        random.nextBytes(key);
        return key;
    }

    /**
     * 字节数组转十六进制字符串
     * @param value 整数值
     * @param bytesLen 字节数组长度
     * @return 十六进制字符串
     */
    public String bytes2hex(int value, int bytesLen) {
        return bytes2hex(get(value, bytesLen));
    }

    /**
     * 字节数组转十六进制字符串
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    public static String bytes2hex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String tmp = Integer.toHexString(b & 0XFF);
            if (tmp.length() == 1) {
                sb.append("0").append(tmp);
            } else {
                sb.append(tmp);
            }
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 字节数组转二进制字符串
     * @param bytes 字节数组
     * @return 二进制字符串
     */
    public static String bytes2binary(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String tmp = Integer.toBinaryString(b & 0XFF);
            int delta = 8 - tmp.length();
            for (int i = 0; i < delta; i++) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString().toUpperCase();
    }

    public static byte[] hex2bytes(String string) {
        if (string == null) return null;
        int len = string.length();
        if (len == 0) return null;
        byte[] bytes = new byte[len >> 1];
        for (int i = 0; i < len; i += 2) {
            String s = string.substring(i, i + 2);
            bytes[i >> 1] = Integer.valueOf(s, 16).byteValue();
        }
        return bytes;
    }

    public enum Order {
        BE, // Big Endian
        LE // Little Endian
    }
}
