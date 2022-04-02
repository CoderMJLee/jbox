package io.github.codermjlee.common.util.binary;

import io.github.codermjlee.common.util.io.Resources;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

/**
 * RSA加解密
 *
 * @author MJ
 */
public class Rsas {
    public static String privateKey;
    public static String publicKey;
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    static {
        try (
            Scanner privateScanner = new Scanner(Resources.getInputStream("private.key"));
            Scanner publicScanner = new Scanner(Resources.getInputStream("public.key"))
        ) {
            privateKey = privateScanner.next();
            publicKey = publicScanner.next();
        } catch (Exception e) {
            //
        }
    }

    /**
     * 用私钥对信息生成数字签名
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return 数字签名
     * @throws Exception 可能会出错
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey.getBytes(CHARSET));
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return new String(Base64.getEncoder().encode(signature.sign()), CHARSET);
    }

    public static String sign(String data, String privateKey) throws Exception {
        return sign(data.getBytes(CHARSET), privateKey);
    }

    /**
     * 校验数字签名
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     * @return 签名是否正确
     * @throws Exception 可能会出错
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey.getBytes(CHARSET));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.getDecoder().decode(sign.getBytes(CHARSET)));
    }

    /**
     * 私钥解密
     * @param data 已加密数据
     * @param key  私钥(BASE64编码)
     * @return 解密后的数据
     * @throws Exception 可能会出错
     */
    public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key.getBytes(CHARSET));
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        return crypt(data, key, pkcs8KeySpec, true, false);
    }

    /**
     * 公钥解密
     * @param data 已加密数据
     * @param key  公钥(BASE64编码)
     * @return 解密后的数据
     * @throws Exception 可能会出错
     */
    public static byte[] decryptByPublicKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key.getBytes(CHARSET));
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        return crypt(data, key, x509KeySpec, false, false);
    }

    private static byte[] crypt(byte[] data,
                                String keyString,
                                EncodedKeySpec keySpec,
                                boolean privateKey,
                                boolean encrypt) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key key;
        if (privateKey) {
            key = keyFactory.generatePrivate(keySpec);
        } else {
            key = keyFactory.generatePublic(keySpec);
        }
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        if (encrypt) {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }
        int inputLen = data.length;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            return out.toByteArray();
        }
    }

    /**
     * 公钥加密
     * @param data 源数据
     * @param key  公钥(BASE64编码)
     * @return 加密后的数据
     * @throws Exception 可能会出错
     */
    public static byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key.getBytes(CHARSET));
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        return crypt(data, key, x509KeySpec, false, true);
    }

    /**
     * 私钥加密
     * @param data 源数据
     * @param key  私钥(BASE64编码)
     * @return 加密后的数据
     * @throws Exception 可能会出错
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key.getBytes(CHARSET));
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        return crypt(data, key, pkcs8KeySpec, true, true);
    }

    public static String encrypt(String data) {
        return encryptByPublicKey(data, publicKey);
    }

    public static String decrypt(String data) {
        return decryptByPrivateKey(data, privateKey);
    }

    public static String encryptByPublicKey(String data, String publicKey) {
        try {
            return new String(Base64.getEncoder().encode(encryptByPublicKey(data.getBytes(CHARSET), publicKey)), CHARSET);
        } catch (Exception ignored) {
            return data;
        }
    }

    public static String decryptByPrivateKey(String data, String privateKey) {
        try {
            byte[] bytes = Base64.getDecoder().decode(data.getBytes(CHARSET));
            return new String(decryptByPrivateKey(bytes, privateKey), CHARSET);
        } catch (Exception ignored) {
            return data;
        }
    }
}
