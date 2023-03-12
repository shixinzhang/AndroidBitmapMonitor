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

        //1.初始化
        long checkInterval = 5;
        long threshold = 100 * 1024;
        long restoreImageThreshold = 100 * 1024;;
        String dir = this.getExternalFilesDir("bitmap_monitor").getAbsolutePath();
        Log.d("bitmapmonitor", "restoreImageDirectory: " + dir);

        BitmapMonitor.Config config = new BitmapMonitor.Config.Builder()
                .checkRecycleInterval(checkInterval)    //检查图片是否被回收的间隔，单位：秒 （建议不要太频繁，默认 5秒）
                .getStackThreshold(threshold)           //获取堆栈的阈值，当一张图片占据的内存超过这个数值后就会去抓栈
                .restoreImageThreshold(restoreImageThreshold)   //还原图片的阈值，当一张图占据的内存超过这个数值后，就会还原出一张原始图片
                .restoreImageDirectory(dir)             //保存还原后图片的目录
                .showFloatWindow(true)                  //是否展示悬浮窗，可实时查看内存大小（建议只在 debug 环境打开）
                .clearAllFileWhenRestartApp(true)      //重启后清除本地所有文件（目前不支持展示历史数据，所以默认清除本地所有 2023.03.12）
                .clearFileWhenOutOfThreshold(true)     //运行时超出阈值就清理，阈值为 diskCacheLimitBytes
                .diskCacheLimitBytes(10 * 1024 * 1024)  //本地图片缓存写入上限，单位为 byte，默认大小为 512MB，超出后会立刻删除
                .isDebug(true)
                .context(this)
                .build();
        BitmapMonitor.init(config);

        //2.注册数据监听
        BitmapMonitor.addListener(new BitmapMonitor.BitmapInfoListener() {
            @Override
            public void onBitmapInfoChanged(final BitmapMonitorData data) {
                Log.d("bitmapmonitor", "onBitmapInfoChanged: " + data);
            }
        });

        //3.开始监控
//        BitmapMonitor.start();
        //开启，并提供页面获取接口
        BitmapMonitor.start(new BitmapMonitor.CurrentSceneProvider() {
            @Override
            public String getCurrentScene() {
                if (sCurrentActivity != null) {
                    return sCurrentActivity.getClass().getSimpleName();
                }
                return null;
            }
        });

        //4.停止
//        BitmapMonitor.stop();

        //5.主动 dump 数据
        //获取所有数据
        BitmapMonitorData bitmapAllData = BitmapMonitor.dumpBitmapInfo();
        Log.d("bitmapmonitor", "bitmapAllData: " + bitmapAllData);

        //仅获取数量和内存大小，不获取具体图片信息
        BitmapMonitorData bitmapCountData = BitmapMonitor.dumpBitmapCount();
        Log.d("bitmapmonitor", "bitmapCountData: " + bitmapCountData);

    }
}
