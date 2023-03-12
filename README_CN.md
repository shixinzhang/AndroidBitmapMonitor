![](images/logo.jpg)

![](https://img.shields.io/badge/Android-4.4%20--%2013-blue.svg?style=flat)
![](https://img.shields.io/badge/arch-armeabi--v7a%20%7C%20arm64--v8a-blue.svg?style=flat)
![](https://img.shields.io/badge/release-1.1.0-red.svg?style=flat)

**Android Bitmap Monitor** 是一个 Android 图片内存分析工具，可以帮助开发者快速发现应用的图片使用是否合理，支持在线下和线上使用。

---

**在 Android 应用的内存使用中，图片总是占据不少比例**。

拿小米 12（3200 x 1440 的分辨率） 来说，一张全屏的图片至少要占用 17MB（3200 x 1440 x 4 ）。如果缓存里多几张，基本就要达到上百 MB，稍有不当，就可能导致应用的内存溢出崩溃大大增加。

因此，我们需要这样的工具：**可以快速发现应用内加载的图片是否合理**，比如大小是否合适、是否存在泄漏、缓存是否及时清理、是否加载了当前并不需要的图片等等。

[AndroidBitmapMonitor](https://github.com/shixinzhang/AndroidBitmapMonitor) 正是为此而生！

## 更新日志

|版本|变更| 备注 |
|---|---| --- |
|1.1.0|支持清除本地保存的图片数据，避免占用存储过多|推荐使用|
|1.0.9|优化性能，减少主线程耗时||
|1.0.8|修复使用 Glide 加载的图片，还原时可能为纯黑的问题；支持 no-op 依赖(感谢 [yibaoshan](https://github.com/yibaoshan))||
|1.0.7|完善悬浮窗和图片列表功能，修复悬浮窗可能出现多个的问题||

## 功能介绍

* 支持 Android 4.4 - 13 (API level 19 - 33)
* 支持 armeabi-v7a 和 arm64-v8a
* 支持线下实时查看图片内存情况 和 线上数据统计

可以提供的功能：

1. 获取内存中的图片数量及占用内存
2. 获取 Bitmap 创建堆栈及线程
3. 全版本 Bitmap Preview，在堆栈无法看出问题时，可以用来定位图片所属业务

动图：
<div align=center><img width="300" src="images/capture.gif"/></div>

核心功能截图：

<div align=center><img width="300" src="images/capture_1.jpg"/></div>
<div align=center><p>悬浮窗中可以实时查看到图片内存</p></div>

<div align=center><img width="300" src="images/capture_2.jpg"/></div>
<div align=center><p>内存中的图片信息</p></div>

<div align=center><img width="300" src="images/capture_3.jpg"/></div>
<div align=center><p>某张图片的具体信息</p></div>

## 使用文档


您可以参考 [app module](app) 中的示例 app 添加依赖，这里介绍具体添加方式，主要有四步：

1. 添加 gradle 依赖
2. 初始化配置
3. 在需要的时候调用 start 和 stop
4. 获取数据

### 1. 在 build.gradle 中增加依赖

Android Bitmap Monitor 发布在 mavenCentral 上，因此首先需要确保您的项目有使用 mavenCentral 作为仓库。

您可以在根目录的 build.gradle 或者 setting.gradle 中添加以下代码：

```
allprojects {
    repositories {
        //...
        //添加 mavenCentral 依赖
        mavenCentral()
    }
}
```

接着在具体业务的 build.gradle 文件中添加依赖：

```
android {
    packagingOptions {
        pickFirst 'lib/*/libshadowhook.so'
    }
}

dependencies {
    //依赖方式 1，如果线上线下都要使用，可以通过以下方式依赖
    implementation 'io.github.shixinzhang:android-bitmap-monitor:1.1.0'
    
    //依赖方式 2，如果不希望正式包中有代码运行，可以通过以下方式依赖
    releaseImplementation 'io.github.shixinzhang:android-bitmap-monitor-no-op:1.1.0'
    debugImplementation 'io.github.shixinzhang:android-bitmap-monitor:1.1.0'
}
```

依赖方式 1 和 2 选择其一即可。

> 请注意：为了避免和其他库冲突，上面的 packagingOptions 中 ``pickFirst 'lib/*/libshadowhook.so'`` 是必要的。

添加完依赖并执行 gradle sync 后，下一步就是在代码里进行初始化和启动。

### 2. 初始化

初始化需要调用的 API 是 ``BitmapMonitor.init``：

```
        long checkInterval = 10;
        long threshold = 100 * 1024;
        long restoreImageThreshold = 100 * 1024;;
        String dir = this.getExternalFilesDir("bitmap_monitor").getAbsolutePath();

        BitmapMonitor.Config config = new BitmapMonitor.Config.Builder()
                .checkRecycleInterval(checkInterval)    //检查图片是否被回收的间隔，单位：秒 （建议不要太频繁，默认 5秒）
                .getStackThreshold(threshold)           //获取堆栈的阈值，当一张图片占据的内存超过这个数值后就会去抓栈
                .restoreImageThreshold(restoreImageThreshold)   //还原图片的阈值，当一张图占据的内存超过这个数值后，就会还原出一张原始图片
                .restoreImageDirectory(dir)             //保存还原后图片的目录
                .showFloatWindow(true)                  //是否展示悬浮窗，可实时查看内存大小（建议只在 debug 环境打开）
                .clearAllFileWhenRestartApp(true)      //重启后清除本地所有文件（目前不支持展示历史数据，所以默认清除本地所有 2023.03.12）
                .clearFileWhenOutOfThreshold(false)     //运行时超出阈值就清理，阈值为 diskCacheLimitBytes
                .diskCacheLimitBytes(100 * 1024 * 1024)  //本地图片缓存写入上限，单位为 byte，默认大小为 512MB，超出后会立刻删除
                .isDebug(true)
                .context(this)
                .build();
        BitmapMonitor.init(config);
```

> 当 showFloatWindow 为 true 时，首次启动 app 需要授予悬浮窗权限。

### 3. 开启和停止监控

初始化完成后，可以在任意时刻调用 start/stop 开启和停止监控:

```
        //开启监控，方式1
        BitmapMonitor.start();
        
        //开启方式2，提供页面获取接口，建议使用
        BitmapMonitor.start(new BitmapMonitor.CurrentSceneProvider() {
            @Override
            public String getCurrentScene() {
                //返回当前顶部页面名称
                if (sCurrentActivity != null) {
                    return sCurrentActivity.getClass().getSimpleName();
                }
                return null;
            }
        });
        
        //停止监控
        BitmapMonitor.stop();
```

上面的代码中，开启方式 2 的参数用来获取图片创建时的页面名称，这个接口可以帮助知道大图是在哪个页面创建的。如果不想提供这个接口可以使用开启方式 1。

那我们该在什么使用开启监控呢？

一般有「全局开启」和「分业务开启」两种使用方式：

1. 全局开启：一启动就 start，用于了解整个 APP 使用过程中的图片内存数据
2. 分业务开启：在进入某个业务前 start，退出后 stop，用于了解特定业务的图片内存数据

### 4. 获取数据

在初始化完成并开启监控后，我们就可以拦截到每张图片的创建过程。

**Android Bitmap Monitor** 提供了两种获取内存中图片数据的 API：

1. 定时回调 addListener
2. 主动获取数据 dumpBitmapInfo

**定时回调** 是指注册一个 listener，这个接口的回调会按照一定时间间隔被调用，**可以用来做实时监控**：

```
        BitmapMonitor.addListener(new BitmapMonitor.BitmapInfoListener() {
            @Override
            public void onBitmapInfoChanged(final BitmapMonitorData data) {
                Log.d("bitmapmonitor", "onBitmapInfoChanged: " + data);
            }
        });
```

间隔时间是初始化时传递的参数 checkRecycleInterval，返回的数据结构如下所示：

```
public class BitmapMonitorData {
    //历史创建的总图片数
    public long createBitmapCount;
    //历史创建的总图片内存大小，单位 byte
    public long createBitmapMemorySize;

    //当前内存中还未回收的图片数
    public long remainBitmapCount;
    //当前内存中还未回收的图片内存大小，单位 byte
    public long remainBitmapMemorySize;

    //泄漏（未释放）的 bitmap 数据
    public BitmapRecord[] remainBitmapRecords;
    
    //...
}
```

**主动获取数据** 是指主动调用 ``BitmapMonitor.dumpBitmapInfo()`` 获取内存中的所有数据，**可以用在内存升高时上报数据**：

```
        //获取所有数据
        BitmapMonitorData bitmapAllData = BitmapMonitor.dumpBitmapInfo();
        Log.d("bitmapmonitor", "bitmapAllData: " + bitmapAllData);
        
        //仅获取数量和内存大小，不获取具体图片信息
        BitmapMonitorData bitmapCountData = BitmapMonitor.dumpBitmapCount();
        Log.d("bitmapmonitor", "bitmapCountData: " + bitmapCountData);
```

``dumpBitmapInfo`` 会返回内存中所有图片的信息，如果只想获取到图片的总数和内存总量，可以调用 ``dumpBitmapCount``，速度更快更轻量。

到这里我们就了解了 **Android Bitmap Monitor** 的核心 API，通过这个库我们可以对 APP 的图片使用情况有更深的了解，也可以让知识面更广一点！快来使用吧！

## 贡献者

1. [shixinzhang](https://about.me/shixinzhang)
2. [yibaoshan](https://github.com/yibaoshan)

## 致谢

1. 函数 hook 通过强力的 [android-inline-hook](https://github.com/bytedance/android-inline-hook) 实现，感谢
2. 图片导出基于 [Nian Sun](https://www.linkedin.cn/incareer/in/nian-sun-531b3745) 的实现开发而成，感谢

# 联系我

<div style="display:flex; flex-direction:row">
    <img width="300" src="images/wechat_channel.jpg"/>
    <img width="280" src="images/wechat.jpg"/>
</div>


## 许可证

**Android Bitmap Monitor**  使用 [Apache 2.0](LICENSE) 授权。
