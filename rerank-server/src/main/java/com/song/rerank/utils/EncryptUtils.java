package com.song.rerank.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密
 * @author Zheng Jie
 * @date 2018-11-23
 */

public class EncryptUtils {

    private static final String STR_PARAM = "Passw0rd";

    private static Cipher cipher;

    private static final IvParameterSpec IV = new IvParameterSpec(STR_PARAM.getBytes(StandardCharsets.UTF_8));

    private static DESKeySpec getDesKeySpec(String source) throws Exception {
        if (source == null || source.length() == 0){
            return null;
        }
        cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        String strKey = "Passw0rd";
        return new DESKeySpec(strKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 對稱加密
     */
    public static String desEncrypt(String source) throws Exception {
        DESKeySpec desKeySpec = getDesKeySpec(source);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IV);
        return byte2hex(
                cipher.doFinal(source.getBytes(StandardCharsets.UTF_8))).toUpperCase();
    }

    /**
     * 對稱解密
     */
    public static String desDecrypt(String source) throws Exception {
        byte[] src = hex2byte(source.getBytes(StandardCharsets.UTF_8));
        DESKeySpec desKeySpec = getDesKeySpec(source);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IV);
        byte[] retByte = cipher.doFinal(src);
        return new String(retByte);
    }

    private static String byte2hex(byte[] inStr) {
        String stmp;
        StringBuilder out = new StringBuilder(inStr.length * 2);
        for (byte b : inStr) {
            stmp = Integer.toHexString(b & 0xFF);
            if (stmp.length() == 1) {
                // 如果是0至F的單位字符串，則添加0
                out.append("0").append(stmp);
            } else {
                out.append(stmp);
            }
        }
        return out.toString();
    }

    private static byte[] hex2byte(byte[] b) {
        int size = 2;
        if ((b.length % size) != 0){
            throw new IllegalArgumentException("長度不是偶數");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += size) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }


    public static String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());

            // 將 byte[] 轉為十六進位字串
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0'); // 補零
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
}