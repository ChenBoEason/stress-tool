package com.eason.stress.core.result;

import java.io.Writer;

/**
 * @author Eason
 * @date 2019/02/18
 **/
public interface StressResultFormater {

    void format(StressResult result, Writer writer);
}
