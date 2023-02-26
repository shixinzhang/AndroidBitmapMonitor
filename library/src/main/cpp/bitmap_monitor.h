//
// Created by zhangshixin
//

#ifndef MY_APPLICATION_BITMAP_MONITOR_H
#define MY_APPLICATION_BITMAP_MONITOR_H

#include <jni.h>
#include <string>
#include <android/log.h>
#include <vector>
#include <mutex>
#include <sys/system_properties.h>

typedef long long ptr_long;

struct BitmapRecord {

    ptr_long native_ptr;

    uint32_t    width;
    /** The bitmap height in pixels. */
    uint32_t    height;
    /** The number of byte per row. */
    uint32_t    stride;
    /** The bitmap pixel format. See {@link AndroidBitmapFormat} */
    int32_t     format;

    long long time;

    jstring large_bitmap_save_path;

    jobject java_bitmap_ref;
    jstring java_stack_jstring;
    jstring current_scene;

    bool restore_succeed;
};

struct BitmapMonitorContext {
    JavaVM  *java_vm;
    bool inited;
    bool open_hook;

    void* shadowhook_stub;

    jmethodID bitmap_recycled_method;
    jclass bitmap_monitor_jclass;
    jfieldID native_ptr_field;

    jmethodID dump_stack_method;
    jmethodID get_current_scene_method;

    jclass bitmap_info_jclass;
    jmethodID report_bitmap_data_method;

    jclass bitmap_record_class;
    jmethodID bitmap_record_constructor_method;

    std::vector<BitmapRecord> bitmap_records;

    int64_t create_bitmap_count;
    int64_t create_bitmap_size;

    std::mutex record_mutex;
};

//frameworks/base/libs/hwui/hwui/Bitmap.cpp

#define BITMAP_CREATE_SYMBOL_SO_RUNTIME_AFTER_10 "libhwui.so"
#define BITMAP_CREATE_SYMBOL_SO_RUNTIME "libandroid_runtime.so"

#define BITMAP_CREATE_SYMBOL_RUNTIME "_ZN7android6bitmap12createBitmapEP7_JNIEnvPNS_6BitmapEiP11_jbyteArrayP8_jobjecti"
#define BITMAP_CREATE_SYMBOL_BEFORE_8 "_ZN11GraphicsJNI12createBitmapEP7_JNIEnvPN7android6BitmapEiP11_jbyteArrayP8_jobjecti"

//默认 5 秒检查一次
#define BITMAP_RECORD_CHECK_THREAD_SLEEP_SECONDS 5
#define API_LEVEL_10_0 29
#define API_LEVEL_8_0 26

#define LOG_TAG "bitmap_monitor"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG, __VA_ARGS__)

#endif //MY_APPLICATION_BITMAP_MONITOR_H
