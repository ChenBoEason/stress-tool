package com.eason.stress.example;

import com.alibaba.fastjson.JSON;
import com.eason.stress.core.StressStore;
import com.eason.stress.core.result.StressResult;
import com.eason.stress.core.task.StressTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hello world!
 */
@Slf4j
public class Example {

    static Long count = 0L;

    public static void main(String[] args) {

        StressResult test = StressStore.test(100, 100, new StressTask() {
            @Override
            public Object doTask() throws Exception {
                /**
                 *  your task code
                 */

                log.info("Do my task. {}", ++count);
                return null;
            }
        }, 0);

        log.info("{}", JSON.toJSONString(test));
    }
}
