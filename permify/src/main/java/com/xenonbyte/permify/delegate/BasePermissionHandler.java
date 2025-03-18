package com.xenonbyte.permify.delegate;

import android.os.Build;
import android.util.ArrayMap;

import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;

import com.xenonbyte.permify.PermissionContext;
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

    public BasePermissionHandler(T host) {
        this.mHost = host;
    }

    @Override
    public final T getHost() {
        return mHost;
    }

    @Override
    public final void directRequestPermissions(int requestCode, @NonNull String[] perms, ActivityResultCallback<Map<String, Boolean>> resultCallback) {
        if (isDestroyed()) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Map<String, Boolean> resultMap = new ArrayMap<>();
            int permLength = perms.length;
            for (int i = 0; i < permLength; i++) {
                resultMap.put(perms[i], true);
            }
            if (resultCallback != null) {
                resultCallback.onActivityResult(resultMap);
            }
            return;
        }
        directRequestPermissions23(requestCode, resultCallback, perms);
    }

    @Override
    public final boolean shouldShowRequestPermissionRationale(@NonNull String perm) {
        if (isDestroyed()) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        return shouldShowRequestPermissionRationale23(perm);
    }

    /**
     * 是否销毁
     *
     * @return true销毁, false未销毁
     */
    private boolean isDestroyed() {
        return getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED;
    }

    /**
     * 请求权限
     *
     * @param requestCode
     * @param perms
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public abstract void directRequestPermissions23(int requestCode, ActivityResultCallback<Map<String, Boolean>> resultCallback, @NonNull String... perms);

    /**
     * 请求权限是时否需要显示权限理由
     *
     * @param perm
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public abstract boolean shouldShowRequestPermissionRationale23(@NonNull String perm);
}
