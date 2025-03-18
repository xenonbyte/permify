package com.xenonbyte.permify;

import android.util.ArrayMap;

import java.util.Map;

/**
 * 权限结果存储
 *
 * @author xubo
 */
public class PermissionResultStore<ResultCallback> {
    private final Map<Integer, ResultCallback> mResultMap = new ArrayMap<>();
    private final Object mLock = new Object();

    public void save(int requestCode, ResultCallback callback) {
        if (callback == null) {
            return;
        }
        synchronized (mLock) {
            mResultMap.put(requestCode, callback);
        }
    }

    public ResultCallback getAndRemove(int requestCode) {
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
