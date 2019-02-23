package com.eason.demo;

import com.eason.stress.core.StressStore;
import com.eason.stress.core.task.StressTask;

/**
 * Hello world!
 */
public class Example {
    public static void main(String[] args) {

        StressStore.test(100, 1000, new StressTask() {
            @Override
            public Object doTask() throws Exception {
                /**
                 *  your task code
                 */
                System.out.println("Do my task.");
                return null;
            }
        }, 0);
    }
}
