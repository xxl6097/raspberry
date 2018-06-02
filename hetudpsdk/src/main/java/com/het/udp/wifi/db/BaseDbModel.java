/*
 * -----------------------------------------------------------------
 * Copyright ?2014 clife - 和而泰家居在线网络科技有限公司
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: BaseDbModel.java
 * Create: 2015/9/16 10:56
 */
package com.het.udp.wifi.db;

import android.content.ContentValues;

/**
 * Created by IntelliJ IDEA.
 * User: UUXIA
 * Date: 2015/9/16
 * Time: 10:56
 * Description: 表模型的基础接口
 */
public interface BaseDbModel<T> {
    /**
     * 将表模型数据封装为ContentValues.
     *
     * @return 封装后的ContentValues
     */
    ContentValues toContentValues();
}
