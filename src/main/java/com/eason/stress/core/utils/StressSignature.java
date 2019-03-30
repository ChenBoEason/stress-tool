package com.eason.stress.core.utils;

import sun.misc.BASE64Decoder;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * rsa签名
 *
 * @author Eason
 * @date 2019/03/11
 **/
public class StressSignature {

    /**
     * UTF-8字符集
     **/
    public static final String CHARSET_UTF8 = "UTF-8";

    public static final String CHARSET_GBK = "GBK";

    public static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";

    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    public static final String ALGORITHM = "RSA";


    public static String rsaSign(String content, String privateKey) {
        return rsaSign(content, privateKey, CHARSET_UTF8, SIGN_SHA256RSA_ALGORITHMS);
    }

    public static String rsaSign(String content, String privateKey, String charset, String algorithm) {
        try {
            PrivateKey priKey = getPrivateKey(privateKey);
            /* MD5withRSA  SHA1WithRSA SHA256WithRSA */
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(priKey);
            if (isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }
            byte[] signed = signature.sign();

            return Base64.getEncoder().encodeToString(signed);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean doCheck(String content, String sign, String publicKey) {
        return doCheck(content, sign, publicKey, CHARSET_UTF8, SIGN_SHA256RSA_ALGORITHMS);
    }

    public static boolean doCheck(String content, String sign, String publicKey, String charset, String algorithm) {
        try {
            PublicKey pubKey = getPublicKey(publicKey);
            /* MD5withRSA  SHA1WithRSA SHA256WithRSA */
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(pubKey);
            byte[] encodedKey = content.getBytes(charset);

            signature.update(encodedKey);

            sign.getBytes(CHARSET_UTF8);

            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param value
     * @return
     */
    private static boolean isEmpty(String value) {
        int strLen;
        if (value == null || (strLen = value.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(value.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    private static PrivateKey getPrivateKey(String key) {
        try {
            byte[] keyBytes;
            keyBytes = (new BASE64Decoder()).decodeBuffer(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
        } catch (Exception e) {
            return null;
        }
    }

    private static PublicKey getPublicKey(String key) {
        try {
            byte[] keyBytes;
            keyBytes = (new BASE64Decoder()).decodeBuffer(key);
            /*  */
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            return null;
        }
    }
}
