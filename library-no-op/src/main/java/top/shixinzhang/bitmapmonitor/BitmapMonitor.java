package top.shixinzhang.bitmapmonitor;

import android.content.Context;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.Keep;

/**
 * Description:
 * <br>
 *
 * <br> Created by shixinzhang on 2022/5/8.
 *
 * <br> Email: shixinzhang2016@gmail.com
 *
 * <br> https://about.me/shixinzhang
 */
@Keep
public class BitmapMonitor {


    public interface BitmapInfoListener {
        void onBitmapInfoChanged(BitmapMonitorData data);
    }

    public interface CurrentSceneProvider {
        String getCurrentScene();
    }

    private final static String TAG = "BitmapMonitor-NO-OP";
    private static List<BitmapInfoListener> sListener = new LinkedList<>();
    private static Config sConfig;
    private static CurrentSceneProvider sCurrentSceneProvider;

    public static boolean init(Config config) {
        return false;
    }

    public static boolean start() {
        return start(null);
    }

    public static boolean start(CurrentSceneProvider provider) {
        return false;
    }

    public static void stop() {
    }

    public static void toggleFloatWindowVisibility(boolean show) {
    }

    public static BitmapMonitorData dumpBitmapInfo() {
        return null;
    }

    public static BitmapMonitorData dumpBitmapCount() {
        return null;
    }

    public static void addListener(BitmapInfoListener listener) {
    }

    public static void removeListener(BitmapInfoListener listener) {
    }

    @Keep
    public static void reportBitmapInfo(BitmapMonitorData info) {
    }

    @Keep
    public static String dumpJavaStack() {
        return null;
    }
    @Keep
    public static String getCurrentScene() {
        return null;
    }

    @Keep
    public static void reportBitmapFile(String file) {

    }

    public static boolean isDebug() {
        return false;
    }

    private static void log(String msg) {
        if (!isDebug()) {
            return;
        }
        Log.d(TAG, msg);
    }

    public static Config getConfig() {
        return sConfig;
    }

    public static class Config {

        private Config(Builder builder) {

        }


        public static final class Builder {

            public Builder() {
            }

            public Builder checkRecycleInterval(long val) {
                return this;
            }

            public Builder getStackThreshold(long val) {
                return this;
            }

            public Builder restoreImageThreshold(long val) {
                return this;
            }

            public Builder restoreImageDirectory(String val) {
                return this;
            }

            public Builder showFloatWindow(boolean val) {
                return this;
            }

            public Builder clearAllFileWhenRestartApp(boolean val) {
                return this;
            }

            public Builder clearFileWhenOutOfThreshold(boolean val) {
                return this;
            }

            public Builder diskCacheLimitBytes(long val) {
                return this;
            }

            public Builder isDebug(boolean val) {
                return this;
            }

            public Builder context(Context val) {
                return this;
            }

            public Config build() {
                return new Config(this);
            }
        }
    }
}
