package top.shixinzhang.bitmapmonitor;

import androidx.annotation.Keep;

import java.io.Serializable;

/**
 * 某张图片的详细信息
 * @create by : zhangshixin
 */
@Keep
public class BitmapRecord implements Serializable {
    public int width;
    public int height;
    public int bitsPerPixel;
    public int format;

    public long time;

    public long nativePtr;

    public String pictureExplorePath;
    public String createStack;
    public String currentScene;

    public BitmapRecord(long nativePtr, int width, int height, int bitsPerPixel,
                        int format, long time, String pictureExplorePath,
                        String createStack, String currentScene) {
        this.nativePtr = nativePtr;
        this.width = width;
        this.height = height;
        this.bitsPerPixel = bitsPerPixel;
        this.format = format;
        this.time = time;
        this.pictureExplorePath = pictureExplorePath;
        this.createStack = createStack;
        this.currentScene = currentScene;
    }

    @Override
    public String toString() {
        return "BitmapRecord{" +
                "nativePtr=" + nativePtr +
                ", width=" + width +
                ", height=" + height +
                ", bitsPerPixel=" + bitsPerPixel +
                ", format=" + format +
                ", pictureExplorePath='" + pictureExplorePath + '\'' +
                ", createStack='" + createStack + '\'' +
                ", currentScene='" + currentScene + '\'' +
                '}';
    }

    public String getFormatSize() {
        int size = width * height * bitsPerPixel;
        return BitmapMonitorData.getFormatSize(size);
    }

    public int getSize() {
        return width * height * bitsPerPixel;
    }
}
