package com.eason.stress.core.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES对称加密
 *
 * @author Eason
 * @date 2019/03/11
 **/
public class StressAesEncrypt {

    private static final Logger LOGGER = LoggerFactory.getLogger(StressAesEncrypt.class);

    /**
     * charSet字符集编码
     */
    private static final String charsetName = "utf-8";
    /**
     * 签名算法
     */
    private static final String SIGN_ALGORITHMS = "AES/ECB/PKCS5Padding";
    /**
     * 加密算法
     */
    private static final String KEY_ALGORITHM = "AES";

    private static final int LENGTH = 16;

    /**
     * 加密
     * @param content
     * @param encryptKey
     * @return
     */
    public static String encrypt(String content,String encryptKey) {
        if (content == null || encryptKey == null) {
            throw new NullPointerException("加密内容或加密key不能为null");
        }
        if( encryptKey.length() != LENGTH ){
            throw new RuntimeException("加密的encryptKey必须为16位");
        }

        try {
            Cipher cipher = Cipher.getInstance(SIGN_ALGORITHMS);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(charsetName), KEY_ALGORITHM));
            byte[] bytes = cipher.doFinal(content.getBytes(charsetName));
            String result = Base64.encodeBase64String(bytes);
            /* 解决Base64加密换行问题 */
            return result;
        } catch (Exception e) {
            LOGGER.error("加密失败:{}",content,e);
        }
        return null;

    }

    /**
     * 解密
     *
     * @param content
     * @param decryptKey
     * @return
     */
    public static String decrypt(String content,String decryptKey) {
        if (content == null || decryptKey == null) {
            throw new NullPointerException("解密内容或解密key不能为null");
        }
        if( decryptKey.length() != LENGTH ){
            throw new RuntimeException("解密的decryptKey必须为16位");
        }

        try{
            Cipher cipher = Cipher.getInstance(SIGN_ALGORITHMS);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(charsetName), KEY_ALGORITHM));
            byte[] bytes = Base64.decodeBase64(content);
            bytes = cipher.doFinal(bytes);
            return new String(bytes, charsetName);
        }catch (Exception e){
            LOGGER.error("解密失败:{}",content,e);
        }
        return null;

    }
}
