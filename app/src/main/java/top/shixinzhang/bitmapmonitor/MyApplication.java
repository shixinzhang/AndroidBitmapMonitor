package top.shixinzhang.bitmapmonitor;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Description: BitmapMonitor example application
 * <br>
 *
 * <br> Created by shixinzhang on 2022/5/9.
 *
 * <br> Email: shixinzhang2016@gmail.com
 *
 * <br> https://about.me/shixinzhang
 */
public class MyApplication extends Application {

    public static Activity sCurrentActivity;

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);


        long checkInterval = 10;
        long threshold = 100 * 1024;
        long restoreImageThreshold = 100 * 1024;;
        String dir = this.getExternalFilesDir("bitmap_monitor").getAbsolutePath();

        BitmapMonitor.Config config = new BitmapMonitor.Config.Builder()
                .checkRecycleInterval(checkInterval)
                .getStackThreshold(threshold)
                .restoreImageThreshold(restoreImageThreshold)
                .restoreImageDirectory(dir)
                .showFloatWindow(true)
                .persistDataInDisk(true)
                .isDebug(true)
                .context(this)
                .build();
        BitmapMonitor.init(config);

        BitmapMonitor.addListener(new BitmapMonitor.BitmapInfoListener() {
            @Override
            public void onBitmapInfoChanged(final BitmapMonitorData data) {
                Log.d("bitmapmonitor", "onBitmapInfoChanged: " + data);
            }
        });

        BitmapMonitor.start(new BitmapMonitor.CurrentSceneProvider() {
            @Override
            public String getCurrentScene() {
                if (sCurrentActivity != null) {
                    return sCurrentActivity.getClass().getSimpleName();
                }
                return null;
            }
        });

        BitmapMonitorData bitmapMonitorData = BitmapMonitor.dumpBitmapCount();

        //用于清理内存
        registerComponentCallbacks(new MemoryTrimCallback());

        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
    }

    private class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            sCurrentActivity = activity;
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    }

    private static class MemoryTrimCallback implements ComponentCallbacks2 {

        @Override
        public void onTrimMemory(int level) {
            if (level >= TRIM_MEMORY_MODERATE) {
                // TODO: 2022/8/5 clear cache
            }
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {}

        @Override
        public void onLowMemory() {
            // TODO: 2022/8/5 降级策略
        }
    }
}
