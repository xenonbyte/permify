package com.xenonbyte.permify;

import android.app.Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.xenonbyte.permify.delegate.ActivityPermissionDelegate;
import com.xenonbyte.permify.delegate.BasePermissionHandler;
import com.xenonbyte.permify.delegate.FragmentActivityPermissionDelegate;
import com.xenonbyte.permify.delegate.FragmentPermissionDelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 权限处理分发
 *
 * @author xubo
 */
class PermissionDispatcher {
    private final BasePermissionHandler mPermissionHandler;
    private final AtomicInteger mRequestCode = new AtomicInteger(0);
    private final PermissionDataStore<PermissionResult> mCallbackStore = new PermissionDataStore<>();

    PermissionDispatcher(Activity activity) {
        if (activity instanceof FragmentActivity) {
            mPermissionHandler = new FragmentActivityPermissionDelegate((FragmentActivity) activity);
        } else {
            mPermissionHandler = new ActivityPermissionDelegate(activity);
        }
        init();
    }

    PermissionDispatcher(Fragment fragment) {
        mPermissionHandler = new FragmentPermissionDelegate(fragment);
        init();
    }

    private void init() {
        mPermissionHandler.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    lifecycleOwner.getLifecycle().removeObserver(this);
                    mCallbackStore.clear();
                }
            }
        });
    }

    /**
     * 请求权限列表
     *
     * @param perms        权限列表
     * @param specialPerms 特殊权限列表
     * @param callback     结果回调
     */
    @MainThread
    void requestPermissions(@NonNull String[] perms, @NonNull String[] specialPerms, PermissionResult callback) {
        requestPermissions(perms, specialPerms, false, callback);
    }

    /**
     * 请求权限列表
     *
     * @param perms         权限列表
     * @param specialPerms  特殊权限列表
     * @param skipRationale 跳过权限询问检测
     * @param callback      结果回调
     */
    @MainThread
    private void requestPermissions(@NonNull String[] perms, @NonNull String[] specialPerms, boolean skipRationale, PermissionResult callback) {
        if (isDestroyed()) {
            return;
        }
        int requestCode = mRequestCode.incrementAndGet();
        mCallbackStore.save(requestCode, callback);
        if (!skipRationale && callback instanceof PermissionRationale && shouldShowRationale(perms)) {
            ((PermissionRationale) callback).showRationaleUI(mPermissionHandler.getContext(), new PermissionRationaleHandler() {
                @Override
                public void onAccepted() {
                    requestPermissions(perms, specialPerms, true, mCallbackStore.getAndRemove(requestCode));
                }

                @Override
                public void onDenied() {

                }
            });
            return;
        }
        mPermissionHandler.directRequestPermissions(requestCode, perms, specialPerms, (ActivityResultCallback<Map<String, Boolean>>) resultMap -> {
            if (isDestroyed()) {
                return;
            }
            List<String> grantPerms = new ArrayList<>();
            List<String> denyPerms = new ArrayList<>();
            List<String> denyForeverPerms = new ArrayList<>();
            boolean success = true;
            for (int i = 0; i < perms.length; i++) {
                String perm = perms[i];
                Boolean grant = resultMap.get(perm);
                if (grant != null && grant) {
                    grantPerms.add(perm);
                } else {
                    if (!mPermissionHandler.shouldShowRequestPermissionRationale(perm)) {
                        denyForeverPerms.add(perm);
                    } else {
                        denyPerms.add(perm);
                    }
                    success = false;
                }
            }
            for (int i = 0; i < specialPerms.length; i++) {
                String perm = specialPerms[i];
                Boolean grant = resultMap.get(perm);
                if (grant != null && grant) {
                    grantPerms.add(perm);
                } else {
                    denyPerms.add(perm);
                    success = false;
                }
            }
            PermissionResult resultCallback = mCallbackStore.getAndRemove(requestCode);
            if (resultCallback != null) {
                resultCallback.onResult(success, grantPerms, denyPerms, denyForeverPerms);
            }
        });
    }

    /**
     * 请求权限是否需要询问
     *
     * @param perms 权限集
     * @return true需要, false不需要
     */
    private boolean shouldShowRationale(@NonNull String... perms) {
        for (String perm : perms) {
            if (mPermissionHandler.shouldShowRequestPermissionRationale(perm)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生命周期是否销毁
     *
     * @return 是否销毁状态
     */
    private boolean isDestroyed() {
        return mPermissionHandler.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED;
    }

}
