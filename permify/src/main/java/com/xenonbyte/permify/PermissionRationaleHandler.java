package com.xenonbyte.permify;

/**
 * 权限询问处理器
 *
 * @author xubo
 */
public interface PermissionRationaleHandler {
    /**
     * 同意
     */
    void onAccepted();

    /**
     * 拒绝
     */
    void onDenied();
}
