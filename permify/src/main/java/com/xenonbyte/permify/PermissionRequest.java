package com.xenonbyte.permify;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 权限请求
 *
 * @author xubo
 */
public class PermissionRequest {
    private final PermissionDispatcher mDispatcher;
    private final Set<String> mPermissions = new LinkedHashSet<>();
    private PermissionResult mResult;

    private PermissionRequest(Activity activity) {
        mDispatcher = new PermissionDispatcher(activity);
    }

    private PermissionRequest(Fragment fragment) {
        mDispatcher = new PermissionDispatcher(fragment);
    }

    /**
     * 通过Activity构建权限请求
     *
     * @param activity 页面Activity
     * @return 权限请求实例
     */
    public static PermissionRequest with(Activity activity) {
        return new PermissionRequest(activity);
    }

    /**
     * 通过Fragment构建权限请求
     *
     * @param fragment 页面Fragment
     * @return 权限请求实例
     */
    public static PermissionRequest with(Fragment fragment) {
        return new PermissionRequest(fragment);
    }

    /**
     * 添加请求权限
     *
     * @param permissions 请求权限集
     * @return 权限请求实例
     */
    public PermissionRequest addPermissions(String... permissions) {
        for (String permission : permissions) {
            mPermissions.add(permission);
        }
        return this;
    }

    /**
     * 设置结果回调
     *
     * @param result 权限请求结果
     * @return 权限请求实例
     */
    public PermissionRequest onResult(PermissionResult result) {
        this.mResult = result;
        return this;
    }

    /**
     * 发起权限请求
     */
    public void request() {
        String[] perms = mPermissions.toArray(new String[mPermissions.size()]);
        mDispatcher.requestPermissions(perms, mResult);
    }
}