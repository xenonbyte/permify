package com.xenonbyte.permify.delegate;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.NonNull;

/**
 * 特殊权限工具类
 *
 * @author xubo
 */
public class SpecialPermissionsUtils {

    /**
     * 是否特殊权限
     *
     * @param perm 权限
     * @return true特殊权限, false非特殊权限
     */
    public static boolean isSpecialPermission(@NonNull String perm) {
        if (perm.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) { //悬浮窗权限, Android 6+
            return true;
        } else if (perm.equals(Manifest.permission.WRITE_SETTINGS)) { //修改系统设置权限, Android 6+
            return true;
        } else if (perm.equals(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) { //访问所有文件权限, Android 11+
            return true;
        } else if (perm.equals(Manifest.permission.REQUEST_INSTALL_PACKAGES)) { //安装未知应用权限, Android 8+
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取特殊权限打开intent
     *
     * @param context 上下文
     * @param perm    权限
     * @return 特殊权限打开intent
     */
    public static Intent getSpecialPermissionIntent(@NonNull Context context, @NonNull String perm) {
        Intent intent = new Intent();
        if (perm.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) { //悬浮窗权限, Android 6+
            intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        } else if (perm.equals(Manifest.permission.WRITE_SETTINGS)) { //修改系统设置权限, Android 6+
            intent.setAction(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        } else if (perm.equals(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) { //访问所有文件权限, Android 11+
            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        } else if (perm.equals(Manifest.permission.REQUEST_INSTALL_PACKAGES)) {//安装未知应用权限, Android 8+
            intent.setAction(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        } else {
            return null;
        }
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        return intent;
    }

    /**
     * 获取特殊权限打开intent备选
     *
     * @param context 上下文
     * @param perm    权限
     * @return 特殊权限打开intent备选
     */
    public static Intent getSpecialPermissionIntent2(@NonNull Context context, @NonNull String perm) {
        Intent intent = new Intent();
        if (perm.equals(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) { //访问所有文件权限, Android 11+
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        }
        return intent;
    }

    /**
     * 是否授予特殊权限
     * <p>
     * 非特殊权限返回true
     *
     * @param context 上下文
     * @param perm    权限
     * @return true授予, false未授予
     */
    public static boolean isGrantSpecialPermission(@NonNull Context context, @NonNull String perm) {
        if (perm.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) { //悬浮窗权限, Android 6+
            return hasSystemAlertWindowPermission(context);
        } else if (perm.equals(Manifest.permission.WRITE_SETTINGS)) { //修改系统设置权限, Android 6+
            return hasWriteSettingPermission(context);
        } else if (perm.equals(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) { //访问所有文件权限, Android 11+
            return hasManageExternalStoragePermission();
        } else if (perm.equals(Manifest.permission.REQUEST_INSTALL_PACKAGES)) { //安装未知应用权限, Android 8+
            return hasInstallPackagePermission(context);
        } else {
            return true;
        }
    }

    /**
     * 是否有悬浮窗权限
     *
     * @param context
     * @return
     */
    private static boolean hasSystemAlertWindowPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else {
            return true;
        }
    }

    /**
     * 是否有修改系统设置权限
     *
     * @param context
     * @return
     */
    private static boolean hasWriteSettingPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.System.canWrite(context);
        } else {
            return true;
        }
    }

    /**
     * 是否有安装应用权限
     *
     * @param context
     * @return
     */
    private static boolean hasInstallPackagePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getPackageManager().canRequestPackageInstalls();
        } else {
            return true;
        }
    }

    /**
     * 是否有访问所有文件权限
     *
     * @return
     */
    private static boolean hasManageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return true;
        }
    }
}
