package top.shixinzhang.bitmapmonitor;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.Locale;

import androidx.annotation.Keep;

/**
 * 当前内存中的图片整体数据
 * @create by : zhangshixin
 */
@Keep
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

    public BitmapMonitorData(long createBitmapCount, long createBitmapMemorySize,
                             long remainBitmapCount, long remainBitmapMemorySize) {
        this.createBitmapCount = createBitmapCount;
        this.createBitmapMemorySize = createBitmapMemorySize;
        this.remainBitmapCount = remainBitmapCount;
        this.remainBitmapMemorySize = remainBitmapMemorySize;
    }

    public String getCreateBitmapMemorySizeWithFormat() {
        return getFormatSize(createBitmapMemorySize);
    }

    public String getRemainBitmapMemorySizeWithFormat() {
        return getFormatSize(remainBitmapMemorySize);
    }

    public static String getFormatSize(long size) {
        float memory = size * 1.0f;
        String unit = "b";
        if (memory > 1024 ) {
            memory = memory / 1024;
            unit = "Kb";
        }
        if (memory > 1024 ) {
            memory = memory / 1024;
            unit = "Mb";
        }
        if (memory > 1024 ) {
            memory = memory / 1024;
            unit = "Gb";
        }
        return String.format(Locale.getDefault(),"%.0f %s", memory, unit);
    }

    public enum AndroidBitmapFormat {
        /** No format. */
        ANDROID_BITMAP_FORMAT_NONE("None", 0),
        /**
         * Red: 8 bits, Green: 8 bits, Blue: 8 bits, Alpha: 8 bits.
         **/
        ANDROID_BITMAP_FORMAT_RGBA_8888("RGBA_8888", 1),
        /** Red: 5 bits, Green: 6 bits, Blue: 5 bits. **/
        ANDROID_BITMAP_FORMAT_RGB_565  ("RGB_565", 4),
        /** Deprecated in API level 13. Because of the poor quality of this configuration, it is advised to use ARGB_8888 instead. **/
        ANDROID_BITMAP_FORMAT_RGBA_4444 ("RGBA_4444", 7),
        /** Alpha: 8 bits. */
        ANDROID_BITMAP_FORMAT_A_8       ("ALPHA_8", 8),
        /** Each component is stored as a half float. **/
        ANDROID_BITMAP_FORMAT_RGBA_F16  ("RGBA_F16", 9);


        String name;
        int code;

        AndroidBitmapFormat(String name, int code) {
            this.name = name;
            this.code = code;
        }

        public static String getBitmapFormatName(int code) {
            switch (code) {
                case 1:
                    return ANDROID_BITMAP_FORMAT_RGBA_8888.name;
                case 4:
                    return ANDROID_BITMAP_FORMAT_RGB_565.name;
                case 7:
                    return ANDROID_BITMAP_FORMAT_RGBA_4444.name;
                case 8:
                    return ANDROID_BITMAP_FORMAT_A_8.name;
                case 9:
                    return ANDROID_BITMAP_FORMAT_RGBA_F16.name;
                case 0:
                default:
                    return ANDROID_BITMAP_FORMAT_NONE.name;

            }
        }
    };

    @Override
    public String toString() {
        return "BitmapMonitorData{" +
                "createBitmapCount=" + createBitmapCount +
                ", createBitmapMemorySize=" + createBitmapMemorySize +
                ", remainBitmapCount=" + remainBitmapCount +
                ", remainBitmapMemorySize=" + remainBitmapMemorySize +
                ", remainBitmapRecords=" + Arrays.toString(remainBitmapRecords) +
                '}';
    }
}
