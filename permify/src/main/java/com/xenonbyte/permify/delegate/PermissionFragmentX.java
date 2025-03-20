package com.xenonbyte.permify.delegate;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.xenonbyte.permify.PermissionDataStore;

import java.util.LinkedList;
import java.util.Map;

/**
 * 权限Fragment实现
 * 基于{@link androidx.fragment.app.Fragment}
 *
 * @author xubo
 */
public class PermissionFragmentX extends Fragment implements ActivityResultCallback<Map<String, Boolean>>, PermissionFragmentHost {
    private ActivityResultLauncher<String[]> mRuntimePermissionLauncher;
    private final LinkedList<ActivityResultCallback<Map<String, Boolean>>> mRuntimePermissionCallbacks = new LinkedList<>();
    private final PermissionDataStore<ActivityResultCallback<Map<String, Boolean>>> mSpecialCallbackStore = new PermissionDataStore<>();
    private final PermissionDataStore<String[]> mSpecialPermsStore = new PermissionDataStore<>();
    private final PermissionRequestManager mPermissionRequestManager = new PermissionRequestManager();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mRuntimePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), this);
    }

    @Override
    public void onActivityResult(Map<String, Boolean> resultMap) {
        ActivityResultCallback<Map<String, Boolean>> resultCallback = mRuntimePermissionCallbacks.pollFirst();
        if (resultCallback != null) {
            resultCallback.onActivityResult(resultMap);
        }
    }

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
    public void onStop() {
        super.onStop();
        mPermissionRequestManager.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPermissionRequestManager.onDestroy();
        mRuntimePermissionCallbacks.clear();
        mSpecialCallbackStore.clear();
        mSpecialPermsStore.clear();
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

    @Override
    public void requestSpecialPermissions(int requestCode, @NonNull String[] perms, ActivityResultCallback<Map<String, Boolean>> resultCallback) {
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
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestRuntimePermissions(int requestCode, @NonNull String[] perms, ActivityResultCallback<Map<String, Boolean>> resultCallback) {
        mPermissionRequestManager.executeRequestAction(() -> {
            if (mRuntimePermissionLauncher != null) {
                mRuntimePermissionCallbacks.addLast(resultCallback);
                mRuntimePermissionLauncher.launch(perms);
            }
        });
    }

}
