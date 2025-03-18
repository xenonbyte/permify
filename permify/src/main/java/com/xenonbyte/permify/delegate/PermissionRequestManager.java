package com.xenonbyte.permify.delegate;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限请求管理
 *
 * @author xubo
 */
public class PermissionRequestManager implements LifecycleOwner {
    private final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private final List<Runnable> mPendingTasks = new ArrayList<>();

    void onCreate() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    void onStart() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    void onResume() {
        for (Runnable task : mPendingTasks) {
            task.run();
        }
        mPendingTasks.clear();
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    void onPause() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
    }

    void onStop() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    void onDestroy() {
        mPendingTasks.clear();
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }

    @NonNull
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

    /**
     * 执行请求Action
     *
     * @param requestAction 请求Action
     */
    void executeRequestAction(Runnable requestAction) {
        executeAfterResume(requestAction);
    }

    /**
     * 任务在Resume之后执行
     *
     * @param task 需要执行的任务
     */
    private void executeAfterResume(Runnable task) {
        if (mLifecycleRegistry.getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            task.run();
        } else {
            mPendingTasks.add(task);
        }
    }
}
