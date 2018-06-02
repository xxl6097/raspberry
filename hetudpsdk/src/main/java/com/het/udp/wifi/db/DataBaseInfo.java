/*
 * -----------------------------------------------------------------
 * Copyright ?2014 clife - 和而泰家居在线网络科技有限公司
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: DataBaseInfo.java
 * Create: 2015/9/16 11:00
 */
package com.het.udp.wifi.db;

/**
 * Created by IntelliJ IDEA.
 * User: UUXIA
 * Date: 2015/9/16
 * Time: 11:00
 * Description:数据库的配置信息
 */
public final class DataBaseInfo {
    /**
     * 数据库的版本?? 数据库版本 如果更改了安装的时候会执行 onupgrade方法
     */
    public static final int VERSION = 10;

    /**
     * 数据库的扩展??
     */
    public static final String EXTENSION = ".db";
}
