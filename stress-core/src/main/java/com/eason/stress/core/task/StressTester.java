package com.eason.stress.core.task;

import com.eason.stress.core.StressContext;
import com.eason.stress.core.StressThreadWorker;
import com.eason.stress.core.result.SimpleResultFormater;
import com.eason.stress.core.result.StressResult;
import com.eason.stress.core.utils.StatisticsUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eason
 * @date 2019/02/18
 **/
@Slf4j
public class StressTester {

    private int defaultWarmUpTime = 1600;

    private ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("stress-pool-%d").build();

    private StressTask emptyTestService = new StressTask() {
        @Override
        public Object doTask() throws Exception {
            return null;
        }
    };

    static {
        warnSelf();
    }

    protected static void warnSelf() {
        for (int i = 0; i < 50; ++i) {
            StressTester benchmark = new StressTester();
            benchmark.test(10, 100, (StressTask) null, 0);
        }

    }

    protected void warmUp(int warmUpTime, StressTask testService) {
        log.info("warm up start, warmUpTime:[{}]", warmUpTime);
        for (int i = 0; i < warmUpTime; ++i) {
            try {
                testService.doTask();
            } catch (Exception e) {
                log.error("Test exception", e);
            }
        }
        log.info("warm up end, warmUpTime:[{}]", warmUpTime);
    }


    public StressResult test(int concurrencyLevel, int totalRequests, StressTask stressTask) {
        return this.test(concurrencyLevel, totalRequests, stressTask, this.defaultWarmUpTime);
    }


    public StressResult test(int concurrencyLevel, int totalRequests, StressTask stressTask, int warmUpTime) {
        if (stressTask == null) {
            stressTask = this.emptyTestService;
        }
        /* 是否预热 */
        this.warmUp(warmUpTime, stressTask);

        /* 计算每个线程请求多少次 */
        int everyThreadCount = totalRequests / concurrencyLevel;
        /* 设置辅助类线程，用于结果统计，栅栏等待所有线程运行完成 */
        CyclicBarrier threadStartBarrier = new CyclicBarrier(concurrencyLevel);
        /* 计数使用，所有子线程任务执行完才视为完全结束，保证所有都完成 */
        CountDownLatch threadEndLatch = new CountDownLatch(concurrencyLevel);
        // 采用原子性操作，在该业务中 常规 i++、++i 存在一定安全性问题
        AtomicInteger failedCounter = new AtomicInteger();

        StressContext stressContext = new StressContext();
        stressContext.setTestService(stressTask);
        stressContext.setEveryThreadCount(everyThreadCount);
        stressContext.setThreadStartBarrier(threadStartBarrier);
        stressContext.setThreadEndLatch(threadEndLatch);
        stressContext.setFailedCounter(failedCounter);

        ExecutorService executorService = new ThreadPoolExecutor(concurrencyLevel, concurrencyLevel * 2, 0L,
                TimeUnit.MILLISECONDS, new SynchronousQueue<>(),
                namedThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());

        Executors.newFixedThreadPool(concurrencyLevel);
        List<StressThreadWorker> workers = new ArrayList(concurrencyLevel);

        int realTotalRequests;
        StressThreadWorker worker;
        // 添加工作任务
        for (realTotalRequests = 0; realTotalRequests < concurrencyLevel; ++realTotalRequests) {
            worker = new StressThreadWorker(stressContext, everyThreadCount);
            workers.add(worker);
        }
        // 提交任务
        for (realTotalRequests = 0; realTotalRequests < concurrencyLevel; ++realTotalRequests) {
            worker = (StressThreadWorker) workers.get(realTotalRequests);
            executorService.submit(worker);
        }

        try {
            threadEndLatch.await();
        } catch (InterruptedException e) {
            log.error("InterruptedException", e);
        }
        // 关闭线程
        executorService.shutdownNow();
        realTotalRequests = everyThreadCount * concurrencyLevel;
        // 失败请求次数
        int failedRequests = failedCounter.get();
        /* 执行结果 */
        StressResult stressResult = new StressResult();

        StressTester.SortResult sortResult = this.getSortedTimes(workers);
        List<Long> allTimes = sortResult.allTimes;
        stressResult.setAllTimes(allTimes);
        List<Long> trheadTimes = sortResult.trheadTimes;
        long totalTime = (Long) trheadTimes.get(trheadTimes.size() - 1);

        stressResult.setTestsTakenTime(totalTime);
        stressResult.setFailedRequests(failedRequests);
        stressResult.setTotalRequests(realTotalRequests);
        stressResult.setConcurrencyLevel(concurrencyLevel);
        stressResult.setWorkers(workers);

        return stressResult;
    }


    protected StressTester.SortResult getSortedTimes(List<StressThreadWorker> workers) {
        List<Long> allTimes = new ArrayList();
        List<Long> trheadTimes = new ArrayList();
        Iterator iterator = workers.iterator();

        while (iterator.hasNext()) {
            StressThreadWorker worker = (StressThreadWorker) iterator.next();
            List<Long> everyWorkerTimes = worker.getEveryTimes();
            long workerTotalTime = StatisticsUtils.getTotal(everyWorkerTimes);
            trheadTimes.add(workerTotalTime);
            Iterator var10 = everyWorkerTimes.iterator();

            while (var10.hasNext()) {
                Long time = (Long) var10.next();
                allTimes.add(time);
            }
        }

        Collections.sort(allTimes);
        Collections.sort(trheadTimes);
        StressTester.SortResult result = new StressTester.SortResult();
        result.allTimes = allTimes;
        result.trheadTimes = trheadTimes;
        return result;
    }

    class SortResult {
        List<Long> allTimes;
        List<Long> trheadTimes;

        SortResult() {
        }
    }

}
