package com.eason.stress.core.utils;

/**
 * StressKeyPair
 *
 * @author Eason
 * @date 2019/03/12
 **/
public class StressKeyPair {

    private String publicKey;

    private String privateKey;

    public StressKeyPair() {
    }

    public StressKeyPair(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
