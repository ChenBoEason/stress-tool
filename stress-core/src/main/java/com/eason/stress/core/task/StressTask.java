package com.eason.stress.core.task;

/**
 * 并发主体
 *
 * @author Eason
 * @date 2019/02/18
 **/
public interface StressTask {
    Object doTask() throws Exception;
}
