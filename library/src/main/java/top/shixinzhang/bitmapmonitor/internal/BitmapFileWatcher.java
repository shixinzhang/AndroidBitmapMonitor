package top.shixinzhang.bitmapmonitor.internal;

import android.os.Build;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    private static final List<File> mAllFiles = new CopyOnWriteArrayList<>();

    private static final List<BitmapFileListener> mFileListeners = new CopyOnWriteArrayList<>();

    private static final Object LOCK = new Object();

    /**
     * A prioritized {@link ThreadPoolExecutor} for running jobs in BitmapMonitor.
     */
    private static final ThreadPoolExecutor mFileWatcherExecutor;

    static {
        mFileWatcherExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());
        mFileWatcherExecutor.execute(BitmapFileWatcher::loadAllFileToMemory);
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

    public static void config(String bmpRootPath, long limitSize) {
        mRootDirectoryPath = bmpRootPath;
        mLimitBytes = limitSize;
    }

    public static void startWatch(String path) {
        if (mRootDirectoryPath == null)
            throw new IllegalArgumentException("restore image directory must not be empty!");
        File file = new File(mRootDirectoryPath);
        if (!file.exists())
            throw new IllegalArgumentException("please check the restore image director is valid!!!");
        mFileWatcherExecutor.execute(new WatchFileRunnable(path));
    }

    private static void loadAllFileToMemory() {
        synchronized (LOCK) {
            File rootPath = new File(mRootDirectoryPath);
            File[] files = rootPath.listFiles();
            if (files == null) return;

            // collects total file size to determine whether to delete files
            mUsedTotalSize = 0;
            for (File file : files) mUsedTotalSize += file.length();

            mAllFiles.addAll(Arrays.asList(files));
        }
    }

    private static void deleteFileFromRootDirectory(long over) {
        if (over > 0) return;
        synchronized (LOCK) {
            // sort by last change time, oldest files will be deleted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mAllFiles.sort((file, t1) -> {
                    if (file.lastModified() > t1.lastModified()) return 1;
                    else if (file.lastModified() < t1.lastModified()) return -1;
                    return 0;
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

    private static final class WatchFileRunnable implements Runnable {

        private final String mCurFilePath;

        public WatchFileRunnable(String filePath) {
            this.mCurFilePath = filePath;
        }

        @Override
        public void run() {
            File file = new File(mCurFilePath);
            if (!file.exists() || !file.isFile()) return;
            synchronized (LOCK) {
                mAllFiles.add(file);
                mUsedTotalSize += file.length();
                long over = mLimitBytes - mUsedTotalSize - file.length();
                if (over < 0) deleteFileFromRootDirectory(over);
            }
        }

    }

    public interface BitmapFileListener {
        void onDeletedFileFromIOThread(List<File> files);
    }
}
