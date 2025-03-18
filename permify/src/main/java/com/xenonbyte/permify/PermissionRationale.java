package com.xenonbyte.permify;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * 包含权限引导的权限请求结果
 *
 * @author xubo
 */
public interface PermissionRationale extends PermissionResult {

    /**
     * 显示权限询问UI
     *
     * @param context
     * @param callback
     * @return
     */
    void showRationaleUI(@NonNull Context context, @NonNull PermissionRationaleHandler callback);
}
