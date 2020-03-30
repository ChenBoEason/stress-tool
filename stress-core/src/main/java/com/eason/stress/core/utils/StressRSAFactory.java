package com.eason.stress.core.utils;


import com.eason.stress.core.constant.StressCoreConstants;
import com.eason.stress.core.exception.StressApiException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * RSA生成工厂
 *
 * @author Eason@bianque
 * @date 2018/12/24
 **/
public class StressRSAFactory {
    /**
     *  调用该方法生成1024位的一对密钥（包含公钥、私钥）
     *
     * @return
     * @throws Exception
     */
    public static StressKeyPair generateKeyPair() throws StressApiException {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(StressCoreConstants.algorithm,
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());

            keyPairGen.initialize(StressCoreConstants.KEY_SIZE, new SecureRandom());
            KeyPair keyPair = keyPairGen.generateKeyPair();
            /* 公钥 */
            String publickey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            /* 私钥 */
            String privatekey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

            /* 使用扁鹊自定义的公钥密钥； 也就是转换生成的公钥密钥 */
            return new StressKeyPair(publickey, privatekey);
        } catch (Exception e) {
            throw new StressApiException("生成公钥密钥失败", e);
        }
    }

}
