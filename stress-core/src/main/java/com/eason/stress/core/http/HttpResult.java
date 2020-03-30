package com.eason.stress.core.http;

/**
 * httpclient
 *
 * @author Eason
 * @date 2019/03/10
 **/
public class HttpResult {

    private String code;

    private String body;

    public HttpResult() {
    }

    public HttpResult(String code, String body) {
        this.code = code;
        this.body = body;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
