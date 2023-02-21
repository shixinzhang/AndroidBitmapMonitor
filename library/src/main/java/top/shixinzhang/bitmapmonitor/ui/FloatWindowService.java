package top.shixinzhang.bitmapmonitor.ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Locale;

import top.shixinzhang.bitmapmonitor.BitmapMonitor;
import top.shixinzhang.bitmapmonitor.BitmapMonitorData;
import top.shixinzhang.bitmapmonitor.R;

public class FloatWindowService extends Service implements BitmapMonitor.BitmapInfoListener {

    private float lastMoveX;
    private float lastMoveY;

    private View floatView;
    private TextView bitmapCountTextView;
    private TextView memoryUsageTextView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private final Handler H = new Handler(Looper.getMainLooper());

    private final Runnable openActivityRunnable = new Runnable() {
        @Override
        public void run() {

            Intent intent = new Intent(FloatWindowService.this, BitmapRecordsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
            addFloatView();

            BitmapMonitor.addListener(this);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void addFloatView() {
        if (windowManager == null) {
            return;
        }

        removeView();

        floatView = LayoutInflater.from(this).inflate(R.layout.layout_float_window, null);

        bitmapCountTextView = floatView.findViewById(R.id.tv_bitmap_count);
        memoryUsageTextView = floatView.findViewById(R.id.tv_bitmap_memory_usage);


        layoutParams = (WindowManager.LayoutParams) floatView.getLayoutParams();
        if (layoutParams == null) {
            int type =  WindowManager.LayoutParams.TYPE_PHONE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }
            int layoutParamFlags  = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams = new WindowManager.LayoutParams(200, 200, 300, 50, type, layoutParamFlags, PixelFormat.RGBA_8888);
        }

        windowManager.addView(floatView, layoutParams);

        setListener();

        BitmapMonitorData bitmapMonitorData = BitmapMonitor.dumpBitmapCount();
        updateFloatViewUI(bitmapMonitorData);
    }

    private void setListener() {
        if (floatView == null) {
            return;
        }

        floatView.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView();
            }
        });

        floatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event == null) {
                    return true;
                }
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        lastMoveX = event.getRawX();
                        lastMoveY = event.getRawY();

                        H.removeCallbacks(openActivityRunnable);
                        H.postDelayed(openActivityRunnable, 300);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float currentMoveX = event.getRawX();
                        float currentMoveY = event.getRawY();

                        float offsetX = currentMoveX - lastMoveX;
                        float offsetY = currentMoveY - lastMoveY;

                        Log.d("bitmap_monitor", "ACTION_MOVE >>> " + offsetX + ", " + offsetY);

                        if (offsetX != 0 && offsetY != 0) {
                            H.removeCallbacks(openActivityRunnable);
                        }

                        lastMoveX = currentMoveX;
                        lastMoveY = currentMoveY;

                        layoutParams.x = (int) (layoutParams.x + offsetX);
                        layoutParams.y = (int) (layoutParams.y + offsetY);

                        windowManager.updateViewLayout(floatView, layoutParams);
                        break;

                    case MotionEvent.ACTION_UP:

                        float currentMoveX1 = event.getRawX();
                        float currentMoveY1 = event.getRawY();

                        float offsetX1 = currentMoveX1 - lastMoveX;
                        float offsetY1 = currentMoveY1 - lastMoveY;

                        Log.d("bitmap_monitor", "ACTION_DOWN >>> " + offsetX1 + ", " + offsetY1);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeView();
        BitmapMonitor.removeListener(this);
    }

    private void removeView() {
        if (windowManager == null || floatView == null) {
            return;
        }

        windowManager.removeView(floatView);
        floatView = null;
    }

    @Override
    public void onBitmapInfoChanged(BitmapMonitorData data) {

        H.post(new Runnable() {
            @Override
            public void run() {
                updateFloatViewUI(data);
            }
        });
    }

    private void updateFloatViewUI(BitmapMonitorData data) {
        if (data == null) {
            return;
        }
        Log.d("BitmapMonitor", "updateFloatViewUI: " + data);
//        bitmapCountTextView.setText(String.format(Locale.getDefault(),"%d/%d", data.remainBitmapCount, data.createBitmapCount));
        bitmapCountTextView.setText(String.format(Locale.getDefault(),"%d 张", data.remainBitmapCount));

        String remainBitmapMemorySize = data.getRemainBitmapMemorySizeWithFormat();
        memoryUsageTextView.setText(remainBitmapMemorySize);
    }
}
