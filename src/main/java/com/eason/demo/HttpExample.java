package com.eason.demo;

import com.eason.stress.http.HttpClient;
import com.eason.stress.core.StressStore;
import com.eason.stress.core.task.StressTask;

import java.util.HashMap;
import java.util.Map;

/**
 * http demo
 *
 * @author Eason
 * @date 2019/03/10
 **/
public class HttpExample {

    public static void main(String[] args) {
        /* init http client */
        HttpClient.init();

        Map<String, Object> param = new HashMap<>();

        param.put("content", "测试");

        StressStore.test(2, 10, new StressTask() {
            @Override
            public Object doTask() throws Exception {

                System.out.println(HttpClient.doGet("http://192.168.1.8:8088/httpdemo", param));

                return null;
            }
        }, 0);
    }
}
