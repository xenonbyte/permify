package com.xenonbyte.permify;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * 权限请求结果
 *
 * @author xubo
 */
public interface PermissionResult {

    /**
     * 结果回调
     *
     * @param success          是否成功（申请权限列表都被允许）
     * @param grantPerms       允许的权限列表
     * @param denyPerms        拒绝的权限列表
     * @param denyForeverPerms 永久拒绝的权限列表
     */
    void onResult(boolean success, @NonNull List<String> grantPerms, @NonNull List<String> denyPerms, @NonNull List<String> denyForeverPerms);
}
