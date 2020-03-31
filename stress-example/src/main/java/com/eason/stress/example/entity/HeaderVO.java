package com.eason.stress.example.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author Eason(bo.chenb)
 * @description 头参数信息
 * @date 2019-12-13
 **/
@XmlRootElement(name = "Header")
@XmlAccessorType(XmlAccessType.FIELD)
public class HeaderVO implements Serializable {

    private static final long serialVersionUID = 1602300225728699835L;
    /**
     * Varchar(10) 是 交易码,由各业务接口指定。
     */
    @XmlElement(name = "TradeCode", required = true)
    private String tradeCode;

    /**
     * Varchar(64) 是 资源平台分配
     */
    @XmlElement(name = "OrgCode", required = true)
    private String orgCode;

    /**
     * Varchar(16) 是 应用code，由平台提供，上传时携带
     */
    @XmlElement(name = "AppCode", required = true)
    private String appCode;

    /**
     * 签名，由发起者进行签名处理
     */
    @XmlElement(name = "Sign", required = true)
    private String sign;

    /**
     * DateTime 是 请求时间，格式为： yyyy-MM-dd HH:mm:ss
     */
    @XmlElement(name = "RequestTime", required = true)
    private String requestTime;

    /**
     * Varchar(32) 是 请求终端的 IP 地址， IPv4，格式如:192.168.10.2
     *
     */
    @XmlElement(name="ClientIp",required = true)
    private String clientIp;

    /**
     * 否 扩展参数 json格式
     */
    @XmlElement(name = "RequestExtendParam")
    private String extendParam;

    private String callbackUrl;

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getExtendParam() {
        return extendParam;
    }

    public void setExtendParam(String extendParam) {
        this.extendParam = extendParam;
    }

    @Override
    public String toString() {
        return "HeaderVO{" +
                "tradeCode='" + tradeCode + '\'' +
                ", orgCode='" + orgCode + '\'' +
                ", appCode='" + appCode + '\'' +
                ", sign='" + sign + '\'' +
                ", requestTime='" + requestTime + '\'' +
                ", clientIp='" + clientIp + '\'' +
                ", extendParam='" + extendParam + '\'' +
                '}';
    }
}
