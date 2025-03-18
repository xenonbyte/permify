package com.xenonbyte.permify.delegate;

import android.os.Build;

import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;

import java.util.Map;

/**
 * 权限委托基类
 *
 * @param <T>
 * @param <FM>
 * @author xubo
 */
public abstract class BasePermissionDelegate<T, FM> extends BasePermissionHandler<T> {
    private static final String FRAGMENT_TAG = "studio.longcin.permission:PermissionsFragment";
    private final PermissionFragmentHost mPermissionFragmentHost;

    public BasePermissionDelegate(T host) {
        super(host);
        FM fragmentManager = getFragmentManager();
        mPermissionFragmentHost = getOrCreatePermissionFragmentHost(fragmentManager, FRAGMENT_TAG);
    }

    abstract FM getFragmentManager();

    abstract PermissionFragmentHost getOrCreatePermissionFragmentHost(FM fragmentManager, String tag);

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public final void directRequestPermissions23(int requestCode, ActivityResultCallback<Map<String, Boolean>> resultCallback, @NonNull String... perms) {
        mPermissionFragmentHost.requestPermissions(requestCode, perms, resultCallback);
    }

    @NonNull
    @Override
    public final Lifecycle getLifecycle() {
        return mPermissionFragmentHost.getLifecycle();
    }
}
