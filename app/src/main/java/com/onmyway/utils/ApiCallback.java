package com.onmyway.utils;

public interface ApiCallback<T>{
    void OnComplete(T result);
}