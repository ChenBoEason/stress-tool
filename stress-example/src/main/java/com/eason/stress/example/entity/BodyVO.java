package com.eason.stress.example.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author Eason(bo.chenb)
 * @description
 *      请求参数体
 * @date 2019-12-13
 **/
@XmlRootElement(name = "Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class BodyVO implements Serializable {
    private static final long serialVersionUID = -6718509227466754896L;

    /**
     * Varchar(16) 是 标准集code
     */
    @XmlElement(name = "StandardCode", required = true)
    private String standardCode;


    /**
     * Varchar(16) 是 用于定义服务提供方要求的入参业务数据
     * 是否进行压缩，0（不压缩），1（压缩），默认值为 0
     */
    @XmlElement(name = "DataCompress", required = true)
    private Integer dataCompress;

    /**
     * Varchar(16) 是 任务上传id
     */
    @XmlElement(name = "TaskId")
    private String taskId;

    /**
     * 接收到的原始业务数据
     */
    @XmlElement(name = "BusinessData")
    private String businessData;

    /**
     * 已处理后的明文业务数据 json格式
     */
    private String processedData;



    public String getStandardCode() {
        return standardCode;
    }

    public void setStandardCode(String standardCode) {
        this.standardCode = standardCode;
    }


    public Integer getDataCompress() {
        return dataCompress;
    }

    public void setDataCompress(Integer dataCompress) {
        this.dataCompress = dataCompress;
    }


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getBusinessData() {
        return businessData;
    }

    public void setBusinessData(String businessData) {
        this.businessData = businessData;
    }

    public String getProcessedData() {
        return processedData;
    }

    public void setProcessedData(String processedData) {
        this.processedData = processedData;
    }

    @Override
    public String toString() {
        return "BodyVO{" +
                "standardCode='" + standardCode + '\'' +
                ", dataCompress=" + dataCompress +
                ", taskId='" + taskId + '\'' +
                ", businessData='" + businessData + '\'' +
                ", processedData='" + processedData + '\'' +
                '}';
    }
}
