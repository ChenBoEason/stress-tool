package com.eason.stress.example;

import com.alibaba.fastjson.JSON;
import com.eason.stress.core.StressStore;
import com.eason.stress.core.http.HttpClientUtils;
import com.eason.stress.core.result.StressResult;
import com.eason.stress.core.task.StressTask;
import com.eason.stress.core.utils.DataCompressUtils;
import com.eason.stress.core.utils.JaxbUtils;
import com.eason.stress.core.utils.StressAesEncrypt;
import com.eason.stress.example.entity.BodyVO;
import com.eason.stress.example.entity.HeaderVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eason(bo.chenb)
 * @description
 * @date 2020-03-31
 **/
@Slf4j
public class WebServiceExample {

    /**
     * 文件数据
     */
    private static final String filePath = "/Users/eason/Downloads/demo.json";

    public static void main(String[] args) {

        String compressedData = readFile(filePath);


        long startTime = System.currentTimeMillis();
        StressResult test = StressStore.test(100, 1000, new StressTask() {
            @Override
            public Object doTask() throws Exception {

                Client client = null;

                BodyVO bodyVO = new BodyVO();
                bodyVO.setTaskId("123456789");
                bodyVO.setDataCompress(0);

                String body = new JaxbUtils(BodyVO.class).toXml(bodyVO);

                /* 创建webservice动态客户端 */
                JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
                client = clientFactory.createClient("http://192.168.1.49:8081/DATA/WebService?wsdl");
                HTTPConduit conduit = (HTTPConduit) client.getConduit();
                HTTPClientPolicy policy = new HTTPClientPolicy();
                /* 60秒钟 */
                long timeout = 60 * 1000;

                policy.setConnectionTimeout(timeout);
                policy.setReceiveTimeout(timeout);
                policy.setConnectionRequestTimeout(timeout);
                conduit.setClient(policy);
                /**动态invoke("方法名",参数1,参数2,参数3....)*/
                String operationName = "doService";

                try {
                    Object[] invoke = client.invoke(operationName, buildHeader(), body);
                    //log.info("{}", JSON.toJSONString(invoke));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }, 0);

       // log.info("{}", JSON.toJSONString(test));


        log.info("consume time:[{}ms]", System.currentTimeMillis() - startTime);
    }

    private static String buildHeader(){

        String appCode = "12345";
        String orgCode = "470000";
        String appKey = "425f83b01ad8489a93f9";
        String appSecret = "85738312c81d4791a17b33632b4c7853";
        String encryptKey = "1111111111111111";

        String tradeCode = "30001";

        HeaderVO headerVO = new HeaderVO();
        headerVO.setAppCode("12345");
        headerVO.setClientIp("192.168.1.1");
        headerVO.setOrgCode("470000");
        String requestTime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
        headerVO.setRequestTime(requestTime);
        headerVO.setExtendParam("扩展参数");

        headerVO.setTradeCode(tradeCode);


        String sign = new StringBuilder(orgCode)
                .append(appCode).append(tradeCode)
                .append(requestTime).toString();

        //System.out.println(sign);

        try {
            String encryptSign = StressAesEncrypt.encrypt(sign, encryptKey);
            headerVO.setSign(encryptSign);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String xml = new JaxbUtils(HeaderVO.class).toXml(headerVO);
        //log.info("{}", xml);
        return xml;
    }




    public static String readFile(String filePath) {
        File file = new File(filePath);
        FileInputStream fis = null;
        StringBuilder builder = new StringBuilder();
        try {
            fis = new FileInputStream(file);

            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = fis.read(buf)) != -1) {
                builder.append(new String(buf, 0, len));
            }

            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            //关资源
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
