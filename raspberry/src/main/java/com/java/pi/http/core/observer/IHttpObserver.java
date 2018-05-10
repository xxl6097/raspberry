package com.java.pi.http.core.observer;

import com.java.pi.http.core.http.NanoHTTPD;

public interface IHttpObserver<T> {
    NanoHTTPD.Response onHttpSession(T data);
}
