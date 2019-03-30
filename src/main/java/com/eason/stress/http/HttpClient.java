package com.eason.stress.http;

import com.alibaba.fastjson.JSON;
import com.eason.stress.core.utils.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * httpclient
 *
 * @author Eason
 * @date 2019/03/10
 **/
public class HttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static PoolingHttpClientConnectionManager poolConnManager;

    private static final int DEFULT_MAX_TOTAL_POOL = 10;

    private static final int DEFULT_MAX_CONPER_ROUTE = 10;

    private static final int DEFULT_SOCKT_TIMEOUT = 100000;

    private static final int DEFULT_CON_REQ_TIEMOUT = 100000;

    private static final int DEFULT_CON_TIEMOUT = 100000;

    /**
     * 同步httpclient
     */
    private static CloseableHttpClient httpClient;

    public static void init() {
        init(DEFULT_MAX_TOTAL_POOL,
                DEFULT_MAX_CONPER_ROUTE,
                DEFULT_SOCKT_TIMEOUT,
                DEFULT_CON_REQ_TIEMOUT,
                DEFULT_CON_TIEMOUT);
    }

    /**
     * 初始化http客户端
     *
     * @param maxTotalPool
     * @param maxConPerRoute
     * @param socketTimeout
     * @param connectionRequestTimeout 连接请求超时时间
     * @param connectTimeout           连接超时时间
     */
    public static void init(Integer maxTotalPool, Integer maxConPerRoute, Integer socketTimeout, Integer connectionRequestTimeout, Integer connectTimeout) {
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
        poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // Increase max total connection to 200
        poolConnManager.setMaxTotal(maxTotalPool);
        // Increase default max connection per route to 20
        poolConnManager.setDefaultMaxPerRoute(maxConPerRoute);
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(socketTimeout).build();
        poolConnManager.setDefaultSocketConfig(socketConfig);

        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        CloseableHttpClient closeableHttpClient = HttpClients.custom()
                .setConnectionManager(poolConnManager).setDefaultRequestConfig(requestConfig).build();
        if (poolConnManager != null && poolConnManager.getTotalStats() != null) {
            LOGGER.info("now client pool " + poolConnManager.getTotalStats().toString());
        }
        httpClient = closeableHttpClient;
    }

    public static String doGet(String url) {

        /*申明http get请求*/
        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse response = null;
        try {
            /*发起请求*/
            response = httpClient.execute(httpGet);
            /*状态为200表示成功*/
            if (response.getStatusLine().getStatusCode() == 200) {

                return EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
            }
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String doGet(String url, Map<String, Object> map) {
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (map != null && map.size() > 0) {
                map.keySet().forEach(key -> uriBuilder.setParameter(key, map.get(key).toString()));
            }

            return doGet(uriBuilder.build().toString());
        } catch (Exception e) {
            return null;
        }

    }


    public static HttpResult doPost(String url, String params) {
        return doPost(url, null, params);
    }

    /**
     * 同步POST请求
     * 向指定的url发送一次post请求,参数是字符串
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 返回结果, 请求失败时返回null
     * @apiNote http接口处用 @RequestBody接收参数
     */
    public static HttpResult doPost(String url, Map<String, String> headerMap, String params) {
        if (StringUtils.isEmpty(url)) {
            throw new NullPointerException("URL is NULL");
        }

        if (StringUtils.isEmpty(params)) {
            throw new NullPointerException("Request Param is NULL");
        }

        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        try {
            if (headerMap != null && headerMap.size() > 0) {
                Header[] headers = buildHeader(headerMap);
                httpPost.setHeaders(headers);
            }

            // http实体
            HttpEntity se = new StringEntity(params, ContentType.APPLICATION_JSON);
            // 设置实体数据
            httpPost.setEntity(se);

            response = httpClient.execute(httpPost);
            HttpResult result = new HttpResult();

            result.setCode(response.getStatusLine().getStatusCode() + "");

            if (response.getEntity() != null) {
                result.setBody(EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET));
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static HttpResult doPost(String url, Map<String, Object> paramMap) {
        return doPost(url, null, paramMap);
    }

    /**
     * 同步POST请求
     * 向指定的url发送一次post请求,参数是字符串
     *
     * @param url       请求地址
     * @param headerMap 请求头信息
     * @param paramMap  请求参数
     * @return 返回结果, 请求失败时返回null
     * @apiNote http接口处用 @RequestBody接收参数
     */
    public static HttpResult doPost(String url, Map<String, String> headerMap, Map<String, Object> paramMap) {
        if (StringUtils.isEmpty(url)) {
            throw new NullPointerException("baseUrl不能为空");
        }

        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        try {
            if (headerMap != null && headerMap.size() > 0) {
                Header[] headers = buildHeader(headerMap);
                httpPost.setHeaders(headers);
            }
            httpPost.setEntity(buildEntity(paramMap));

            response = httpClient.execute(httpPost);

            HttpResult result = new HttpResult();

            result.setCode(response.getStatusLine().getStatusCode() + "");

            if (response.getEntity() != null) {
                result.setBody(EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET));
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

    private static HttpEntity buildEntity(Map<String, Object> paramMap) {
        if (paramMap == null || paramMap.size() == 0) {
            return null;
        }
        String paramJson = JSON.toJSONString(paramMap);
        return new StringEntity(paramJson, ContentType.APPLICATION_JSON);
    }

}
