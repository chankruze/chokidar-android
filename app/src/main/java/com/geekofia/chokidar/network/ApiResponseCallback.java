package com.geekofia.chokidar.network;

public interface ApiResponseCallback<T> {
    void onSuccess(T result);

    void onError(Throwable error);
}
