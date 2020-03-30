package com.eason.stress.core;

import com.eason.stress.core.result.SimpleResultFormater;
import com.eason.stress.core.result.StressResult;
import com.eason.stress.core.result.StressResultFormater;
import com.eason.stress.core.task.StressTask;
import com.eason.stress.core.task.StressTester;

import java.io.StringWriter;

/**
 * @author Eason
 * @date 2019/02/18
 **/
public class StressStore {

    private static StressTester stressTester = new StressTester();
    private static SimpleResultFormater simpleResultFormater = new SimpleResultFormater();

    public static StressResult test(int concurrencyLevel, int totalRequests, StressTask stressTask) {
        return stressTester.test(concurrencyLevel, totalRequests, stressTask);
    }

    public static StressResult test(int concurrencyLevel, int totalRequests, StressTask stressTask, int warmUpTime) {
        return stressTester.test(concurrencyLevel, totalRequests, stressTask, warmUpTime);
    }

    public static void testAndPrint(int concurrencyLevel, int totalRequests, StressTask stressTask) {
        testAndPrint(concurrencyLevel, totalRequests, stressTask, (String)null);
    }

    public static void testAndPrint(int concurrencyLevel, int totalRequests, StressTask stressTask, String testName) {
        StressResult stressResult = test(concurrencyLevel, totalRequests, stressTask);
        String str = format(stressResult);
        System.out.println(str);
    }

    public static String format(StressResult stressResult) {
        return format(stressResult, simpleResultFormater);
    }

    public static String format(StressResult stressResult, StressResultFormater stressResultFormater) {
        StringWriter sw = new StringWriter();
        stressResultFormater.format(stressResult, sw);
        return sw.toString();
    }
}
