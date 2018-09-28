package com.kimascend.light.api;


import android.support.annotation.NonNull;

import static com.kimascend.light.api.Status.ERROR;
import static com.kimascend.light.api.Status.LOADING;
import static com.kimascend.light.api.Status.SUCCESS;

/**
 * 对返回的网路请求结果进行带状态的封装
 *
 * @param <T>
 */
public class Resource<T> {

    @NonNull
    public final Status status;
    @NonNull
    public final T data;
    @NonNull
    public final String message;

    private Resource(@NonNull Status status, @NonNull T data, @NonNull String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(T data,String message) {
        return new Resource<>(SUCCESS, data, message);
    }

    public static <T> Resource<T> loading(T data) {
        return new Resource<>(LOADING, data, "");
    }

    public static <T> Resource<T> error(T data, String msg) {
        return new Resource<>(ERROR, data, msg);
    }


}
