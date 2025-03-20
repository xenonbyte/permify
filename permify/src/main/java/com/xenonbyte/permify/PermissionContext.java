package com.xenonbyte.permify;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

/**
 * 权限上下文
 *
 * @param <T>
 * @author xubo
 */
public interface PermissionContext<T> extends LifecycleOwner {

    /**
     * 获取上下文
     *
     * @return 权限上下文
     */
    Context getContext();

    /**
     * 获取Host
     *
     * @return 权限Host
     */
    T getHost();

    /**
     * 上下文销毁
     */
    void onDestroyed();
}
