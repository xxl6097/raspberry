package com.het.udp.wifi.exception;

/**
 * ————————————————————————————————
 * Copyright (C) 2014-2016, by het, Shenzhen, All rights reserved.
 * ————————————————————————————————
 * <p>
 * <p>描述：</p>
 * 名称:  <br>
 * 作者: uuxia<br>
 * 版本: 1.0<br>
 * 日期: 2016/11/28 14:52<br>
 **/
public class ProtocolErrorException extends Exception {
    public ProtocolErrorException() {
    }

    public ProtocolErrorException(String detailMessage) {
        super(detailMessage);
    }

    public ProtocolErrorException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ProtocolErrorException(Throwable throwable) {
        super(throwable);
    }
}
