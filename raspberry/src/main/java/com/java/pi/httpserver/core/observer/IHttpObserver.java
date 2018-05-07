package com.java.pi.httpserver.core.observer;

public interface IHttpObserver<T> {
    String onHttpSession(T data);
}
