package com.het.httpserver.core.observer;

public interface IHttpObserver<T> {
    String onHttpSession(T data);
}
