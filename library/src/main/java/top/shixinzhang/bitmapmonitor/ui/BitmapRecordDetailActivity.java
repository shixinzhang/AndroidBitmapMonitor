package top.shixinzhang.bitmapmonitor.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import top.shixinzhang.bitmapmonitor.BitmapMonitor;
import top.shixinzhang.bitmapmonitor.BitmapRecord;
import top.shixinzhang.bitmapmonitor.R;

@Keep
public class BitmapRecordDetailActivity extends Activity {
    public static final String IMAGE_RESTORE_DEFAULT = "图片大小没有达到阈值，因此没有还原为图片";
    public static final String STACK_TRACE_DEFAULT = "图片大小没有达到阈值，因此没有获取堆栈";

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap_record_detail);

        View recordSummaryView = findViewById(R.id.containr_bitmap_record);
        ImageView imageView = findViewById(R.id.iv_image);
        TextView stacktraceTextView = findViewById(R.id.tv_stacktrace);
        TextView imageRestorePathTextView = findViewById(R.id.tv_restore_path);

        Intent intent = getIntent();
        if (intent != null && intent.getSerializableExtra("data") != null) {
            BitmapRecord record = (BitmapRecord) intent.getSerializableExtra("data");

            BitmapRecordsActivity.BitmapRecordViewHolder holder = new BitmapRecordsActivity.BitmapRecordViewHolder(recordSummaryView);
            setData(holder, record);
            showImage(imageView, record);

            String stacktrace = TextUtils.isEmpty(record.createStack) ? getStackTraceDefaultTip() : record.createStack;
            setTextSafe(stacktraceTextView, stacktrace);


            String path = TextUtils.isEmpty(record.pictureExplorePath) ? IMAGE_RESTORE_DEFAULT : record.pictureExplorePath;
            setTextSafe(imageRestorePathTextView, path);
        }
    }

    private String getStackTraceDefaultTip() {
        return STACK_TRACE_DEFAULT + "\n" + BitmapMonitor.getConfig();
    }

    public static void setTextSafe(TextView textView, String data) {
        if (textView == null || TextUtils.isEmpty(data)) {
            return;
        }

        textView.setText(data);

        if (textView.getVisibility() != View.VISIBLE) {
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void setData(BitmapRecordsActivity.BitmapRecordViewHolder holder, BitmapRecord record) {
        String sizeInfo = String.format(Locale.getDefault(),
                "%s   %dx%dx%d", record.getFormatSize(), record.width, record.height, record.bitsPerPixel);

        setTextSafe(holder.sizeTextView, sizeInfo);

        setTextSafe(holder.timeTextView, dateFormat.format(record.time));

        setTextSafe(holder.sceneTextView, record.currentScene);
    }

    private void showImage(ImageView imageView, BitmapRecord record) {
        if (record == null || record.pictureExplorePath == null) {
            imageView.setVisibility(View.GONE);
            return;
        }

        if (new File(record.pictureExplorePath).exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(record.pictureExplorePath);
            imageView.setImageBitmap(bitmap);
        }
    }
}
