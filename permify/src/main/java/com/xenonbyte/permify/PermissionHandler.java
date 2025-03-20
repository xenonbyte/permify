package com.xenonbyte.permify;

import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;

import java.util.Map;

/**
 * 权限处理接口
 *
 * @author xubo
 */
public interface PermissionHandler {

    /**
     * 请求权限
     *
     * @param requestCode    权限请求code
     * @param perms          权限集
     * @param specialPerms   特殊权限集
     * @param resultCallback 权限结果回调
     */
    void directRequestPermissions(int requestCode, @NonNull String[] perms, @NonNull String[] specialPerms, ActivityResultCallback<Map<String, Boolean>> resultCallback);

    /**
     * 请求权限是否需要显示权限理由
     *
     * @param perm 权限
     * @return true需要, false不需要
     */
    boolean shouldShowRequestPermissionRationale(@NonNull String perm);
}
