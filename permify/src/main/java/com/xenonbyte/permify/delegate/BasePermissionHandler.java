package com.xenonbyte.permify.delegate;

import android.os.Build;
import android.util.ArrayMap;

import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.xenonbyte.permify.PermissionContext;
import com.xenonbyte.permify.PermissionDataStore;
import com.xenonbyte.permify.PermissionHandler;

import java.util.Map;

/**
 * 权限处理基类
 *
 * @param <T>
 * @author xubo
 */
public abstract class BasePermissionHandler<T> implements PermissionContext<T>, PermissionHandler {
    private final T mHost;
    private final PermissionDataStore<Map<String, Boolean>> mResultStore = new PermissionDataStore<>();
    private final PermissionDataStore<ActivityResultCallback<Map<String, Boolean>>> mCallbackStore = new PermissionDataStore<>();

    public BasePermissionHandler(T host) {
        this.mHost = host;
    }

    @Override
    public final T getHost() {
        return mHost;
    }

    @Override
    public void onDestroyed() {
        mResultStore.clear();
        mCallbackStore.clear();
    }

    @Override
    public final void directRequestPermissions(int requestCode, @NonNull String[] perms, @NonNull String[] specialPerms, ActivityResultCallback<Map<String, Boolean>> resultCallback) {
        saveCallback(requestCode, resultCallback);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Map<String, Boolean> resultMap = new ArrayMap<>();
            int permLength = perms.length;
            for (int i = 0; i < permLength; i++) {
                resultMap.put(perms[i], true);
            }
            saveResult(requestCode, resultMap);
            requestSpecialPermissions(requestCode, specialPerms);
        } else {
            directRequestRuntimePermissions23(requestCode, perms, result -> {
                saveResult(requestCode, result);
                requestSpecialPermissions(requestCode, specialPerms);
            });
        }
    }

    @Override
    public final boolean shouldShowRequestPermissionRationale(@NonNull String perm) {
        if (SpecialPermissionsUtils.isSpecialPermission(perm)) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        return shouldShowRequestRuntimePermissionRationale23(perm);
    }

    /**
     * 请求特殊权限
     *
     * @param requestCode 权限code
     * @param perms       权限集
     */
    private void requestSpecialPermissions(int requestCode, @NonNull String[] perms) {
        directRequestSpecialPermissions(requestCode, perms, result -> {
            saveResult(requestCode, result);
            callResult(requestCode);
        });
    }

    /**
     * 保存权限请求结果
     *
     * @param requestCode 权限请求code
     * @param resultMap   权限请求结果
     */
    private void saveResult(int requestCode, @NonNull Map<String, Boolean> resultMap) {
        Object lock = mResultStore.getLock();
        synchronized (lock) {
            Map<String, Boolean> result = mResultStore.getAndRemove(requestCode);
            if (result == null) {
                result = new ArrayMap<>();
            }
            result.putAll(resultMap);
            mResultStore.save(requestCode, result);
        }
    }

    /**
     * 保存权限请求回掉
     *
     * @param requestCode    权限请求code
     * @param resultCallback 权限请求callback
     */
    private void saveCallback(int requestCode, ActivityResultCallback<Map<String, Boolean>> resultCallback) {
        mCallbackStore.save(requestCode, resultCallback);
    }

    /**
     * 回调权限请求结果
     *
     * @param requestCode 权限请求code
     */
    private void callResult(int requestCode) {
        ActivityResultCallback<Map<String, Boolean>> resultCallback = mCallbackStore.getAndRemove(requestCode);
        if (resultCallback != null) {
            Map<String, Boolean> result = mResultStore.getAndRemove(requestCode);
            if (result == null) {
                result = new ArrayMap<>();
            }
            resultCallback.onActivityResult(result);
        }
    }

    /**
     * 请求特殊权限
     *
     * @param requestCode  权限请求code
     * @param specialPerms 特殊权限集
     */
    public abstract void directRequestSpecialPermissions(int requestCode, @NonNull String[] specialPerms, ActivityResultCallback<Map<String, Boolean>> resultCallback);

    /**
     * 请求运行时权限
     *
     * @param requestCode 权限请求code
     * @param perms       权限集
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public abstract void directRequestRuntimePermissions23(int requestCode, @NonNull String[] perms, ActivityResultCallback<Map<String, Boolean>> resultCallback);

    /**
     * 请求运行时权限是时否需要显示权限理由
     *
     * @param perm 权限
     * @return true需要, false不需要
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public abstract boolean shouldShowRequestRuntimePermissionRationale23(@NonNull String perm);
}
