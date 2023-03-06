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

    private final static String TAG = "BitmapMonitor";
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
        return sConfig != null && sConfig.isDebug;
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
        //检查 Bitmap 是否回收的间隔，单位：秒
        long checkRecycleInterval;
        //超过这个阈值后获取堆栈，单位 byte
        long getStackThreshold;
        //超过这个阈值后，保存像素数据为图片，以便分析内容，单位 byte
        long restoreImageThreshold;
        // 本地图片缓存写入上限，单位为 byte，默认大小为 1 GB
        long diskCacheLimitBytes = 1024 * 1024 * 1024;
        //图片还原保存路径
        String restoreImageDirectory;
        //是否展示悬浮窗，开启后可以实时查看数据
        boolean showFloatWindow;
        boolean isDebug;
        //图片数据是否持久化到磁盘
        boolean persistDataInDisk;

        //建议用 application context
        Context context;

        private Config(Builder builder) {
            checkRecycleInterval = builder.checkRecycleInterval;
            getStackThreshold = builder.getStackThreshold;
            restoreImageThreshold = builder.restoreImageThreshold;
            restoreImageDirectory = builder.restoreImageDirectory;
            showFloatWindow = builder.showFloatWindow;
            isDebug = builder.isDebug;
            persistDataInDisk = builder.persistDataInDisk;
            context = builder.context;
        }


        public static final class Builder {
            private long checkRecycleInterval;
            private long getStackThreshold;
            private long restoreImageThreshold;
            private String restoreImageDirectory;
            private boolean showFloatWindow;
            private boolean isDebug;
            private boolean persistDataInDisk;
            private Context context;

            public Builder() {
            }

            public Builder checkRecycleInterval(long val) {
                checkRecycleInterval = val;
                return this;
            }

            public Builder getStackThreshold(long val) {
                getStackThreshold = val;
                return this;
            }

            public Builder restoreImageThreshold(long val) {
                restoreImageThreshold = val;
                return this;
            }

            public Builder restoreImageDirectory(String val) {
                restoreImageDirectory = val;
                return this;
            }

            public Builder showFloatWindow(boolean val) {
                showFloatWindow = val;
                return this;
            }

            public Builder isDebug(boolean val) {
                isDebug = val;
                return this;
            }

            public Builder context(Context val) {
                context = val;
                return this;
            }

            public Config build() {
                return new Config(this);
            }
        }

        @Override
        public String toString() {
            return "Config:\n" +
                    "checkRecycleInterval=" + checkRecycleInterval +
                    ", \ngetStackThreshold=" + getStackThreshold +
                    ", \nrestoreImageThreshold=" + restoreImageThreshold +
                    ", \nrestoreImageDirectory='" + restoreImageDirectory + '\'' +
                    ", \nshowFloatWindow=" + showFloatWindow +
                    ", \nisDebug=" + isDebug;
        }
    }
}
