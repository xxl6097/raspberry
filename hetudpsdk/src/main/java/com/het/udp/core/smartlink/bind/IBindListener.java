package com.het.udp.core.smartlink.bind;


/**
 * Created by uuxia on 2015/4/14.
 */
public interface IBindListener {
    /**
     * 绑定进度
     *
     * @param persent
     */
    void onBindProgress(int persent);

    /**
     * 绑定结束
     */
    void onBindFinish(Object bindMap);
}
