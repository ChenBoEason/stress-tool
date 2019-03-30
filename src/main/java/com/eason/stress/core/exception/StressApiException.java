package com.eason.stress.core.exception;

/**
 * @author Eason@bianque
 * @date 2018/12/21
 **/
public class StressApiException extends Exception {
    private String            errCode;
    private String            errMsg;

    public StressApiException() {
        super();
    }

    public StressApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public StressApiException(String message) {
        super(message);
    }

    public StressApiException(Throwable cause) {
        super(cause);
    }

    public StressApiException(String errCode, String errMsg) {
        super(errCode + ":" + errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return this.errCode;
    }

    public String getErrMsg() {
        return this.errMsg;
    }
}
