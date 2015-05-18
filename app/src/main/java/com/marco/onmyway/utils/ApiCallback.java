package com.marco.onmyway.utils;

public abstract class ApiCallback<T>{
    public abstract void OnComplete(T result);
}