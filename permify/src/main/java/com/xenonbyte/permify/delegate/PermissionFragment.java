package com.xenonbyte.permify.delegate;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;

import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;

import com.xenonbyte.permify.PermissionDataStore;

import java.util.Map;


/**
 * 权限Fragment实现
 * 基于{@link android.app.Fragment}
 *
 * @author xubo
 */
public class PermissionFragment extends Fragment implements PermissionFragmentHost {
    private final PermissionDataStore<ActivityResultCallback<Map<String, Boolean>>> mRuntimeCallbackStore = new PermissionDataStore<>();
    private final PermissionDataStore<ActivityResultCallback<Map<String, Boolean>>> mSpecialCallbackStore = new PermissionDataStore<>();
    private final PermissionDataStore<String[]> mSpecialPermsStore = new PermissionDataStore<>();
    private final PermissionRequestManager mPermissionRequestManager = new PermissionRequestManager();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionRequestManager.onCreate();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPermissionRequestManager.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPermissionRequestManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPermissionRequestManager.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPermissionRequestManager.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPermissionRequestManager.onDestroy();
        mRuntimeCallbackStore.clear();
        mSpecialCallbackStore.clear();
        mSpecialPermsStore.clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Map<String, Boolean> resultMap = new ArrayMap<>();
        for (int i = 0; i < permissions.length; i++) {
            resultMap.put(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
        }
        ActivityResultCallback<Map<String, Boolean>> resultCallback = mRuntimeCallbackStore.getAndRemove(requestCode);
        if (resultCallback != null) {
            resultCallback.onActivityResult(resultMap);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultCallback<Map<String, Boolean>> callback = mSpecialCallbackStore.getAndRemove(requestCode);
        String[] perms = mSpecialPermsStore.getAndRemove(requestCode);
        if (callback == null || perms == null) {
            return;
        }
        Map<String, Boolean> resultMap = new ArrayMap<>();
        for (String perm : perms) {
            Context context;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context = getContext();
            } else {
                context = getActivity();
            }
            boolean granted = SpecialPermissionsUtils.isGrantSpecialPermission(context, perm);
            resultMap.put(perm, granted);
        }
        callback.onActivityResult(resultMap);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mPermissionRequestManager.getLifecycle();
    }

    @Override
    public void requestSpecialPermissions(int requestCode, @NonNull String[] perms, ActivityResultCallback<Map<String, Boolean>> resultCallback) {
        mPermissionRequestManager.executeRequestAction(() -> {
            boolean allGranted = true;
            Map<String, Boolean> result = new ArrayMap<>();
            for (String perm : perms) {
                Context context;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context = getContext();
                } else {
                    context = getActivity();
                }
                boolean granted = SpecialPermissionsUtils.isGrantSpecialPermission(context, perm);
                result.put(perm, granted);
                if (!granted) {
                    allGranted = false;
                    try {
                        startActivityForResult(SpecialPermissionsUtils.getSpecialPermissionIntent(context, perm), requestCode);
                    } catch (Exception e) {
                        startActivityForResult(SpecialPermissionsUtils.getSpecialPermissionIntent2(context, perm), requestCode);
                    }
                }
            }
            if (allGranted) {
                resultCallback.onActivityResult(result);
            } else {
                mSpecialCallbackStore.save(requestCode, resultCallback);
                mSpecialPermsStore.save(requestCode, perms);
            }
        });
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestRuntimePermissions(int requestCode, @NonNull String[] perms, ActivityResultCallback<Map<String, Boolean>> resultCallback) {
        mPermissionRequestManager.executeRequestAction(() -> {
            mRuntimeCallbackStore.save(requestCode, resultCallback);
            requestPermissions(perms, requestCode);
        });
    }
}