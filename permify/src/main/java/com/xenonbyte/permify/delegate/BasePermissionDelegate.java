package com.xenonbyte.permify.delegate;

import android.os.Build;

import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.Map;

/**
 * 权限委托基类
 *
 * @param <T>
 * @param <FM>
 * @author xubo
 */
public abstract class BasePermissionDelegate<T, FM> extends BasePermissionHandler<T> {
    private static final String FRAGMENT_TAG = "com.xenonbyte.permify:PermissionsFragment";
    private final PermissionFragmentHost mPermissionFragmentHost;

    public BasePermissionDelegate(T host) {
        super(host);
        FM fragmentManager = getFragmentManager();
        mPermissionFragmentHost = getOrCreatePermissionFragmentHost(fragmentManager, FRAGMENT_TAG);
        getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    source.getLifecycle().removeObserver(this);
                    onDestroyed();
                }
            }
        });
    }

    abstract FM getFragmentManager();

    abstract PermissionFragmentHost getOrCreatePermissionFragmentHost(FM fragmentManager, String tag);

    @Override
    public void directRequestSpecialPermissions(int requestCode, @NonNull String[] perms, ActivityResultCallback<Map<String, Boolean>> resultCallback) {
        mPermissionFragmentHost.requestSpecialPermissions(requestCode, perms, resultCallback);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public final void directRequestRuntimePermissions23(int requestCode, @NonNull String[] perms, ActivityResultCallback<Map<String, Boolean>> resultCallback) {
        mPermissionFragmentHost.requestRuntimePermissions(requestCode, perms, resultCallback);
    }

    @NonNull
    @Override
    public final Lifecycle getLifecycle() {
        return mPermissionFragmentHost.getLifecycle();
    }
}
