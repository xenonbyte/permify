package com.xenonbyte.permify.delegate;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.LinkedList;
import java.util.Map;

/**
 * 权限Fragment实现
 * 基于{@link Fragment}
 *
 * @author xubo
 */
public class PermissionFragmentX extends Fragment implements ActivityResultCallback<Map<String, Boolean>>, PermissionFragmentHost {
    private ActivityResultLauncher<String[]> mPermissionLauncher;
    private final LinkedList<ActivityResultCallback<Map<String, Boolean>>> mPermissionCallbacks = new LinkedList<>();
    private final PermissionRequestManager mPermissionRequestManager = new PermissionRequestManager();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), this);
    }

    @Override
    public void onActivityResult(Map<String, Boolean> resultMap) {
        ActivityResultCallback<Map<String, Boolean>> resultCallback = mPermissionCallbacks.pollFirst();
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
        mPermissionCallbacks.clear();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermissions(int requestCode, @NonNull String[] perms, ActivityResultCallback<Map<String, Boolean>> resultCallback) {
        mPermissionRequestManager.executeRequestAction(() -> {
            if (mPermissionLauncher != null) {
                mPermissionCallbacks.addLast(resultCallback);
                mPermissionLauncher.launch(perms);
            }
        });
    }

}
