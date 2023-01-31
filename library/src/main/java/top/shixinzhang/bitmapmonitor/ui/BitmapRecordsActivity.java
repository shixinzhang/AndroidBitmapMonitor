package top.shixinzhang.bitmapmonitor.ui;

import static top.shixinzhang.bitmapmonitor.ui.BitmapRecordDetailActivity.setTextSafe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import top.shixinzhang.bitmapmonitor.BitmapMonitor;
import top.shixinzhang.bitmapmonitor.BitmapMonitorData;
import top.shixinzhang.bitmapmonitor.BitmapRecord;
import top.shixinzhang.bitmapmonitor.R;

@Keep
public class BitmapRecordsActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bitmap_records);

        TextView summaryTv = findViewById(R.id.tv_summary);
        recyclerView = findViewById(R.id.recycler_view);

        BitmapMonitorData data = BitmapMonitor.dumpBitmapInfo();
        if (data == null) {
            return;
        }

        summaryTv.setText(String.format(Locale.getDefault(),
                "创建了 %d 张图片，占用内存 %s ；\n尚未回收的图片 %d 张，占用内存 %s。",
                data.createBitmapCount, data.getCreateBitmapMemorySizeWithFormat(),
                data.remainBitmapCount, data.getRemainBitmapMemorySizeWithFormat()));

        BitmapRecord[] remainBitmapRecords = data.remainBitmapRecords;

        if (remainBitmapRecords != null && remainBitmapRecords.length > 0) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                    BitmapRecordsActivity.this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);

            BitmapRecordAdapter bitmapRecordAdapter = new BitmapRecordAdapter(remainBitmapRecords, BitmapRecordsActivity.this);
            recyclerView.setAdapter(bitmapRecordAdapter);
        }

    }

    static class BitmapRecordAdapter extends RecyclerView.Adapter<BitmapRecordViewHolder> {

        BitmapRecord[] data;
        Context context;
        LayoutInflater layoutInflater;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        public BitmapRecordAdapter(BitmapRecord[] data, Context context) {
            this.data = data;
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public BitmapRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.item_bitmap_record, parent, false);
            return new BitmapRecordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BitmapRecordViewHolder holder, int position) {
            if (position >= data.length) {
                return;
            }

            setTextSafe(holder.indexTextView, String.valueOf(position + 1));

            BitmapRecord record = data[position];

            String sizeInfo = String.format(Locale.getDefault(),
                    "%s     %dx%d", record.getFormatSize(), record.width, record.height);

            setTextSafe(holder.sizeTextView, sizeInfo);

            setTextSafe(holder.timeTextView, dateFormat.format(record.time));

            setTextSafe(holder.sceneTextView, record.currentScene);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context != null) {
                        Intent intent = new Intent(context, BitmapRecordDetailActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("data", record);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data != null ? data.length : 0;
        }
    }

    static class BitmapRecordViewHolder extends RecyclerView.ViewHolder {

        public TextView indexTextView;
        public TextView sizeTextView;
        public TextView timeTextView;
        public TextView sceneTextView;

        public BitmapRecordViewHolder(@NonNull View itemView) {
            super(itemView);

            indexTextView = itemView.findViewById(R.id.tv_index);
            sizeTextView = itemView.findViewById(R.id.tv_size);
            timeTextView = itemView.findViewById(R.id.tv_time);
            sceneTextView = itemView.findViewById(R.id.tv_scene);
        }
    }
}
