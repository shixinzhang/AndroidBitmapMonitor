package top.shixinzhang.bitmapmonitor.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import top.shixinzhang.bitmapmonitor.BitmapMonitor;

public class FloatWindow {
    public static void show(Context context) {
        if (context == null) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        if (Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(context, FloatWindowService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent);
            return;
        }

        hide(context);

        //申请悬浮窗权限
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static void hide(Context context) {
        if (context == null) {
            return;
        }

        context.stopService(new Intent(context, FloatWindowService.class));
    }
}
