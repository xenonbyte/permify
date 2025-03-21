package com.xenonbyte.permify.delegate;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Fragment权限处理委托
 *
 * @author xubo
 */
public class FragmentPermissionDelegate extends BasePermissionDelegate<Fragment, FragmentManager> {

    public FragmentPermissionDelegate(Fragment host) {
        super(host);
    }

    @Override
    public Context getContext() {
        return getHost().getContext();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean shouldShowRequestRuntimePermissionRationale23(@NonNull String perm) {
        return getHost().shouldShowRequestPermissionRationale(perm);
    }

    @Override
    FragmentManager getFragmentManager() {
        return getHost().getChildFragmentManager();
    }

    @Override
    PermissionFragmentHost getOrCreatePermissionFragmentHost(FragmentManager fragmentManager, String tag) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        PermissionFragmentX permissionFragment;
        if (fragment instanceof PermissionFragmentX) {
            permissionFragment = (PermissionFragmentX) fragment;
        } else {
            permissionFragment = new PermissionFragmentX();
            fragmentManager
                    .beginTransaction()
                    .add(permissionFragment, tag)
                    .commitAllowingStateLoss();
        }
        return permissionFragment;
    }

}
