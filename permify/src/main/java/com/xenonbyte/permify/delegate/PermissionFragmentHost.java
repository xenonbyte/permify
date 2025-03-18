package com.xenonbyte.permify.delegate;

import android.os.Build;

import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;

import java.util.Map;

/**
 * 真正的权限Fragment宿祖
 *
 * @author xubo
 */
public interface PermissionFragmentHost extends LifecycleOwner {

    /**
     * 请求权限
     *
     * @param requestCode    权限code
     * @param perms          权限集
     * @param resultCallback 结果回调
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestPermissions(int requestCode, @NonNull String[] perms, ActivityResultCallback<Map<String, Boolean>> resultCallback);
}
