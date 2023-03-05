package top.shixinzhang.bitmapmonitor.internal;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class VisibilityTracker implements Application.ActivityLifecycleCallbacks {

    private int startedActivityCount = 0;

    /**
     * Visible activities are any activity started but not stopped yet. An activity can be paused
     * yet visible: this will happen when another activity shows on top with a transparent background
     * and the activity behind won't get touch inputs but still need to render / animate.
     */
    private boolean hasVisibleActivities = false;

    private boolean lastUpdate = false;

    private static final List<AppVisibilityListener> mAppVisibilityListeners = new CopyOnWriteArrayList<>();

    /**
     * The synchronization mechanism is implemented by CopyOnWriteArrayList
     **/
    public static void registerVisibilityListener(AppVisibilityListener appVisibilityListener) {
        mAppVisibilityListeners.add(appVisibilityListener);
    }

    public static void unregisterVisibilityListener(AppVisibilityListener appVisibilityListener) {
        mAppVisibilityListeners.remove(appVisibilityListener);
    }

    /*package*/ VisibilityTracker() {
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        startedActivityCount++;
        if (!hasVisibleActivities && startedActivityCount == 1) {
            hasVisibleActivities = true;
            updateVisible();
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if (startedActivityCount > 0) {
            startedActivityCount--;
        }
        if (hasVisibleActivities && startedActivityCount == 0 && !activity.isChangingConfigurations()) {
            hasVisibleActivities = false;
            updateVisible();
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    private void updateVisible() {
        if (hasVisibleActivities != lastUpdate) {
            lastUpdate = hasVisibleActivities;
            for (AppVisibilityListener listener : mAppVisibilityListeners)
                listener.onAppVisibility(lastUpdate);
        }
    }

    public interface AppVisibilityListener {
        void onAppVisibility(boolean visible);
    }

}
