package com.xenonbyte.permify.delegate;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * Activity权限处理委托
 *
 * @author xubo
 */
public class ActivityPermissionDelegate extends BasePermissionDelegate<Activity, FragmentManager> {

    public ActivityPermissionDelegate(Activity host) {
        super(host);
    }

    @Override
    public Context getContext() {
        return getHost();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean shouldShowRequestRuntimePermissionRationale23(@NonNull String perm) {
        return getHost().shouldShowRequestPermissionRationale(perm);
    }

    @Override
    FragmentManager getFragmentManager() {
        return getHost().getFragmentManager();
    }

    @Override
    PermissionFragmentHost getOrCreatePermissionFragmentHost(FragmentManager fragmentManager, String tag) {
        Fragment permissionsFragment = fragmentManager.findFragmentByTag(tag);
        PermissionFragment permissionFragment;
        if (permissionsFragment instanceof PermissionFragment) {
            permissionFragment = (PermissionFragment) permissionsFragment;
        } else {
            permissionFragment = new PermissionFragment();
            fragmentManager
                    .beginTransaction()
                    .add(permissionFragment, tag)
                    .commitAllowingStateLoss();
        }
        return permissionFragment;
    }

}
