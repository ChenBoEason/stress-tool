package com.eason.stress.core.http;

import com.eason.stress.core.utils.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Eason(bo.chenb)
 * @description
 * @date 2019-09-12
 **/
public class HttpClientUtils {

    private static final String DEFAULT_CHARSET = "UTF-8";

    public static CloseableHttpClient getHttpClient(CoreParam coreParam){

        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContexts.custom().loadTrustMaterial(null,
                    new TrustSelfSignedStrategy())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslsf)
                .build();
        PoolingHttpClientConnectionManager poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolConnManager.setMaxTotal(coreParam.getMaxTotalPool());
        poolConnManager.setDefaultMaxPerRoute(coreParam.getMaxConPerRoute());
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(coreParam.getSocketTimeout()).build();
        poolConnManager.setDefaultSocketConfig(socketConfig);

        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(coreParam.getConnectionRequestTimeout())
                .setConnectTimeout(coreParam.getConnectTimeout()).setSocketTimeout(coreParam.getSocketTimeout()).build();
        CloseableHttpClient closeableHttpClient = HttpClients.custom()
                .setConnectionManager(poolConnManager).setDefaultRequestConfig(requestConfig).build();
        if (poolConnManager != null && poolConnManager.getTotalStats() != null) {

        }
        return closeableHttpClient;
    }


    public static void closeHttpClient(CloseableHttpClient httpClient){
        if(httpClient != null){
            try {
                httpClient.close();
            }catch (Exception e){

            }
        }
    }


    public static String doPost(String baseUrl, Map<String, String> headerMap, String params,
                                CloseableHttpClient httpClient) throws Exception{

        if(httpClient == null){
            throw new NullPointerException("请初始化HttpClientUtils.init");
        }

        if (StringUtils.isEmpty(baseUrl)) {
            throw new NullPointerException("baseUrl不能为空");
        }

        if (StringUtils.isEmpty(params)) {
            throw new NullPointerException("missing post String");
        }

        HttpPost httpPost = new HttpPost(baseUrl);
        CloseableHttpResponse response = null;

        try {
            Header[] headers = buildHeader(headerMap);
            httpPost.setHeaders(headers);
            // http实体
            HttpEntity se = new StringEntity(params, ContentType.APPLICATION_JSON);
            // 设置实体数据
            httpPost.setEntity(se);

            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, DEFAULT_CHARSET);
            }

            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }

    }


    public static String doGet(String url, CloseableHttpClient httpClient) throws Exception{

        if (url == null) {
            return null;
        }

        /*申明http get请求*/
        HttpGet httpGet = new HttpGet(url);

        try {
            /*发起请求*/
            CloseableHttpResponse response = httpClient.execute(httpGet);
            /*状态为200表示成功*/
            if (response.getStatusLine().getStatusCode() == 200) {

                return EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
            }
            return null;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private static Header[] buildHeader(Map<String, String> headerMap) {
        List<Header> headers = new ArrayList<>();
        for (String key : headerMap.keySet()) {
            String value = headerMap.get(key);
            BasicHeader header = new BasicHeader(key, value);
            headers.add(header);
        }
        return headers.toArray(new Header[headers.size()]);
    }

    public static class CoreParam {
        /**
         * 最多的连接数
         */
        private Integer maxTotalPool = 5;
        /**
         * 针对一个域名同时间正在使用的最多的连接数
         */
        private Integer maxConPerRoute = 5;
        private Integer socketTimeout = 10_000;
        private Integer connectionRequestTimeout = 10_000;
        private Integer connectTimeout = 10_000;

        public Integer getMaxTotalPool() {
            return maxTotalPool;
        }

        public void setMaxTotalPool(Integer maxTotalPool) {
            this.maxTotalPool = maxTotalPool;
        }

        public Integer getMaxConPerRoute() {
            return maxConPerRoute;
        }

        public void setMaxConPerRoute(Integer maxConPerRoute) {
            this.maxConPerRoute = maxConPerRoute;
        }

        public Integer getSocketTimeout() {
            return socketTimeout;
        }

        public void setSocketTimeout(Integer socketTimeout) {
            this.socketTimeout = socketTimeout;
        }

        public Integer getConnectionRequestTimeout() {
            return connectionRequestTimeout;
        }

        public void setConnectionRequestTimeout(Integer connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
        }

        public Integer getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
        }
    }
}
