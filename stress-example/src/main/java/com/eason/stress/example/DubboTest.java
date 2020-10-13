package com.eason.stress.example;

import com.alibaba.fastjson.JSON;
import com.alijk.notification.vto.req.NotificationBaseReqVTO;
import com.alijk.notification.vto.req.dingtalk.work.NotificationDingWorkTextReqVTO;
import com.eason.stress.core.StressStore;
import com.eason.stress.core.result.StressResult;
import com.eason.stress.core.task.StressTask;
import com.eason.stress.example.factory.DubboFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * @author Eason(bo.chenb)
 * @description
 * @email chenboeason@gmail.com
 * @date 2020-10-06
 **/
@Slf4j
public class DubboTest {

    static Long count = 0L;

    public static void main(String[] args) {

        NotificationDingWorkTextReqVTO textReqVTO = new NotificationDingWorkTextReqVTO();
        textReqVTO.setAppId("123");
        textReqVTO.setUserId("111111");
        textReqVTO.setContent("测试");

        NotificationBaseReqVTO baseReqVTO = textReqVTO;

        Object invoke = DubboFactory.invoke("nacos://192.168.x.xx:8848", null, "group",
                "1.0.0", "", "send",
                "30000", new String[]{""},
                new Object[]{baseReqVTO}, new HashMap<>());

        System.out.println(JSON.toJSONString(invoke));
    }
}
