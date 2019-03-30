package com.eason.demo;

import com.eason.stress.core.utils.StressSignature;

/**
 *  签名测试
 *
 * @author Eason
 * @date 2019/03/12
 **/
public class SignatureDemo {

    public static void main(String[] args) {
        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJbn0ZPEaIR9bDe8XdbRUbBEoIoS5QPu886fUFt1FejzgQeLBaKK/D/U18dvOctMcR1XaYIkVPFOAR/7HR8sTaaweri8zQVOh/mu/vUud0AN89AB9ixwl5d24lzhqAujBJ+ZGrltNQ3KnxIGTgFL6loI7UhL+b49OT9IGwfqs/ONAgMBAAECgYAHVZiKy0i+Kb9k/Rd9fZpwDAHWfCEb3hUE9ZrI+ymXsyASDwh01Db63jwnkgkXz60OvUUgQWZMfRtSY9jQP029yV/oZVc2QIH1GjEwx1eoXgJ8+uiZUtxVJMiRXLebINCwE492xW19xBMNVgPaCo31nHUsyM/GMsNr/1IIgKaviQJBANHhQ5foa382dXCY0HXgsO8Z8Y+9Hx5fSx+fTWOtP6QoUMegH+HN1x7q76nazg15JAeNPV70IRyhwUZYq7VMlgUCQQC4EPeBT3ImQy6EQfBbZ1g778pxNj2RGOSiML6Tiqb9xTCjqhy86Q567vPXYbHqUHyjN+DQFXdPW5M+0XbGvRXpAkBRPKckKmNJdzPX5F5z8geqVNlqcKKV5/60+71BwrJqgOxbiIAur7T/k6I2lanCD0zB24qQsWALrBSafQHhiAZdAkEApuoL5+FJXFUzy4+YDOssYj3S0NMlBX0TZ9jYGSd0w0+cCe5rXQviq6wgx0G6ewc93yz+2vN8XCRo5GBCW3qAcQJAe1r/CMqU+jLqA9Ju6eXQ6t/bvZEa5+OEAw/q3KhWxIDWxOForJUBKaxXAa3Dh8D+cyLUJKyX+APpo/0u9Uk59A==";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCW59GTxGiEfWw3vF3W0VGwRKCKEuUD7vPOn1BbdRXo84EHiwWiivw/1NfHbznLTHEdV2mCJFTxTgEf+x0fLE2msHq4vM0FTof5rv71LndADfPQAfYscJeXduJc4agLowSfmRq5bTUNyp8SBk4BS+paCO1IS/m+PTk/SBsH6rPzjQIDAQAB";
        String content = "创建";
        String sign = StressSignature.rsaSign(content, privateKey, StressSignature.CHARSET_UTF8, "SHA1WithRSA");
        System.out.println(sign);
        String sign1 = sign + "+";
        System.out.println(sign1);
        System.out.println(StressSignature.doCheck(content, sign, publicKey, StressSignature.CHARSET_UTF8, "SHA1WithRSA"));
        System.out.println(StressSignature.doCheck(content, sign1, publicKey, StressSignature.CHARSET_UTF8, "SHA1WithRSA"));
    }
}
