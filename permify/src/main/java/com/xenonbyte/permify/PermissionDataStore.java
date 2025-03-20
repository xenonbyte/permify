package com.xenonbyte.permify;

import android.util.ArrayMap;

import androidx.annotation.NonNull;

import java.util.Map;

/**
 * 权限数据存储
 *
 * @author xubo
 */
public class PermissionDataStore<Result> {
    private final Map<Integer, Result> mResultMap = new ArrayMap<>();
    private final Object mLock = new Object();

    public @NonNull Object getLock() {
        return mLock;
    }

    public void save(int requestCode, Result callback) {
        if (callback == null) {
            return;
        }
        synchronized (mLock) {
            mResultMap.put(requestCode, callback);
        }
    }

    public Result getAndRemove(int requestCode) {
        synchronized (mLock) {
            return mResultMap.remove(requestCode);
        }
    }

    public void clear() {
        synchronized (mLock) {
            mResultMap.clear();
        }
    }

}
