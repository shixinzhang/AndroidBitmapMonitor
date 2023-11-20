package top.shixinzhang.bitmapmonitor.internal;

import android.os.Build;
import android.os.FileUtils;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import top.shixinzhang.bitmapmonitor.BitmapMonitor;

public class BitmapFileWatcher {

    /**
     * restore image directory path
     **/
    private static String mRootDirectoryPath;

    /**
     * max limit bytes ,default is 1 GB
     */
    private static long mLimitBytes = 1024 * 1024 * 1024;

    private static volatile long mUsedTotalSize;

    private static boolean sClearFileWhenOutOfThreshold;

    private static final List<File> mAllFiles = new CopyOnWriteArrayList<>();

    private static final List<BitmapFileListener> mFileListeners = new CopyOnWriteArrayList<>();

    private static final Object LOCK = new Object();

    /**
     * A prioritized {@link ThreadPoolExecutor} for running jobs in BitmapMonitor.
     */
    private static final ThreadPoolExecutor mFileWatcherExecutor;

    static {
        mFileWatcherExecutor = new ThreadPoolExecutor(0, 1,
                100L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    /**
     * The synchronization mechanism is implemented by CopyOnWriteArrayList
     **/
    public static void registerFileListener(BitmapFileListener fileListener) {
        mFileListeners.add(fileListener);
    }

    public static void unregisterFileListener(BitmapFileListener fileListener) {
        mFileListeners.remove(fileListener);
    }

    /**
     * 根据配置，决定是每次重启就删除之前的文件，还是在运行时超出后就删除
     *
     * @param bmpRootPath
     * @param clearAllFileWhenRestartApp
     * @param clearFileWhenOutOfThreshold
     * @param limitSize
     */
    public static void init(String bmpRootPath, boolean clearAllFileWhenRestartApp, boolean clearFileWhenOutOfThreshold, long limitSize) {
        mRootDirectoryPath = bmpRootPath;
        mLimitBytes = limitSize;
        sClearFileWhenOutOfThreshold = clearFileWhenOutOfThreshold;

        if (mRootDirectoryPath == null)
            throw new IllegalArgumentException("restore image directory must not be empty!");

        if (clearAllFileWhenRestartApp) {
            File file = new File(mRootDirectoryPath);
            if (file.exists()) {
                deleteDir(file);
            }
        }
        mFileWatcherExecutor.execute(BitmapFileWatcher::loadAllFileToMemory);
    }

    public static void startWatch(String path) {
        // FIXME: 2023/3/12
        BitmapMonitor.log("BitmapFileWatcher s1 startWatch " + sClearFileWhenOutOfThreshold + ", " + path + ", exist: " + new File(path).exists());
        if (!sClearFileWhenOutOfThreshold || TextUtils.isEmpty(path)) {
            return;
        }
        if (!new File(path).exists()) {
            BitmapMonitor.log("BitmapFileWatcher return, file not exist " + path);
            return;
        }
        mFileWatcherExecutor.execute(new WatchFileRunnable(path));
    }

    private static void loadAllFileToMemory() {
        synchronized (LOCK) {
            File rootPath = new File(mRootDirectoryPath);
            if (!rootPath.exists()) {
                return;
            }
            File[] files = rootPath.listFiles();
            if (files == null) return;

            mAllFiles.clear();
            mUsedTotalSize = 0;

            // collects total file size to determine whether to delete files
            for (File file : files) mUsedTotalSize += file.length();

            mAllFiles.addAll(Arrays.asList(files));
        }
    }

    private static void deleteFileFromRootDirectory(long over) {
        if (over > 0) return;
        synchronized (LOCK) {
            // sort by last change time, oldest files will be deleted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                HashMap<File, Long> fileByTime = new HashMap<>();
                mAllFiles.sort((file1, file2) -> {
                    Long t1 = fileByTime.get(file1);
                    Long t2 = fileByTime.get(file2);
                    if (t1 == null) {
                        fileByTime.put(file1, t1 = file1.lastModified());
                    }
                    if (t2 == null) {
                        fileByTime.put(file2, t2 = file2.lastModified());
                    }
                    return t1.compareTo(t2);
                });
            }
            // deleting files
            List<File> ret = new LinkedList<>();
            for (File file : mAllFiles) {
                over += file.length();
                mUsedTotalSize -= file.length();
                if (ret.add(file) && file.delete()) mAllFiles.remove(file);
                if (over > 0) break;
            }
            notifyAllListeners(ret);
        }
    }

    private static void notifyAllListeners(List<File> ret) {
        if (mFileListeners.isEmpty() || ret.isEmpty()) return;
        for (BitmapFileListener listener : mFileListeners) listener.onDeletedFileFromIOThread(ret);
    }

    public static boolean deleteDir(File dir) {

        if (dir == null)
            return false;

        try {
            if (!dir.isDirectory()) {
                return dir.delete();
            }

            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteDir(f);
                }

                if (BitmapMonitor.isDebug()) {
                    Log.d("BitmapMonitor", "deleteDir size: " + files.length);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static final class WatchFileRunnable implements Runnable {

        private final String mCurFilePath;

        public WatchFileRunnable(String filePath) {
            this.mCurFilePath = filePath;
        }

        @Override
        public void run() {

            BitmapMonitor.log("BitmapFileWatcher s2 WatchFileRunnable run " + mCurFilePath);

            File file = new File(mCurFilePath);
            if (!file.exists() || !file.isFile()) return;
            synchronized (LOCK) {
                mAllFiles.add(file);
                mUsedTotalSize += file.length();
                long over = mLimitBytes - mUsedTotalSize;

                if (BitmapMonitor.isDebug()) {
                    BitmapMonitor.log("WatchFileRunnable.run >> file size: " + file.length() + ", totalSize:" + mUsedTotalSize + ", over " + over);
                }

                if (over < 0) deleteFileFromRootDirectory(over);
            }
        }

    }

    public interface BitmapFileListener {
        void onDeletedFileFromIOThread(List<File> files);
    }
}
