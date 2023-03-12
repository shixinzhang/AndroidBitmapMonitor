package top.shixinzhang.bitmapmonitor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final List<String> GLIDE_IMAGE_URL_LIST = Arrays.asList(
            "https://img2.baidu.com/it/u=867579726,2670217964&fm=253&fmt=auto&app=120&f=JPEG?w=1280&h=800",
            "https://img1.baidu.com/it/u=2825489197,612817393&fm=253&fmt=auto&app=120&f=JPEG?w=1280&h=800",
            "https://t7.baidu.com/it/u=1732966997,2981886582&fm=193&f=GIF"
    );

    final List<String> PICASSO_IMAGE_URL_LIST = Arrays.asList(
            "https://img2.baidu.com/it/u=449329914,897680117&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500",
            "https://img0.baidu.com/it/u=4159207327,1114356188&fm=253&fmt=auto&app=138&f=JPEG?w=600&h=361",
            "https://img0.baidu.com/it/u=2427603358,581212902&fm=253&fmt=auto&app=120&f=JPEG?w=653&h=490"
    );

    final Handler H = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        H.postDelayed(new Runnable() {
            @Override
            public void run() {
//                BitmapMonitor.stop();
            }
        }, 3_000);


        try {
            testCreateBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadImageByGlide();
        loadImageByPicasso();

    }

    private void loadImageByGlide() {
        LinearLayout horizontalScrollParentView = findViewById(R.id.glide_horizontal_scroll_parent_view);

        int width = getResources().getDisplayMetrics().widthPixels * 3 / 4;

        for (String url : GLIDE_IMAGE_URL_LIST) {
            ImageView imageView = new ImageView(MainActivity.this);
            Glide.with(this).load(url).into(imageView);

            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.rightMargin = 40;
            horizontalScrollParentView.addView(imageView, layoutParams);
        }
    }

    private void loadImageByPicasso() {
        LinearLayout horizontalScrollParentView = findViewById(R.id.picasso_horizontal_scroll_parent_view);


        int width = getResources().getDisplayMetrics().widthPixels * 3 / 4;

        for (String url : PICASSO_IMAGE_URL_LIST) {
            ImageView imageView = new ImageView(MainActivity.this);
            Picasso.get().load(url).into(imageView);

            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.rightMargin = 40;
            horizontalScrollParentView.addView(imageView, layoutParams);
        }
    }

    private void testCreateBitmap() throws Exception{

        //1.给 ImageView 设置图片
        ImageView imageView = findViewById(R.id.iv_logo_pixelated_0);
        imageView.setImageResource(R.mipmap.logo_pixelated_0);

        //2.通过 BitmapFactory 解码本地、网络图片
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo_pixelated_1);
        ImageView imageView2 = findViewById(R.id.iv_logo_pixelated_1);
        imageView2.setImageBitmap(bitmap);


        //3.根据其他图片创建新图片
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        int numPixels = bitmapWidth * bitmapHeight;
        int[] pixels = new int[numPixels];
        bitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        Bitmap copyBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        copyBitmap.setPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        //4.通过资源 id 获取 drawable
        Drawable drawable = getDrawable(R.mipmap.logo_pixelated_2);
        ImageView imageView1 = findViewById(R.id.iv_logo_pixelated_2);

        imageView1.setImageDrawable(drawable);
//        imageView1.setImageBitmap(copyBitmap);

        //5.加载大图
//        BitmapRegionDecoder bitmapRegionDecoder = BitmapRegionDecoder.newInstance(diskImage.getAbsolutePath(), false);
//        Rect rect = new Rect();     //这里指定要解码的区域
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Bitmap decodeRegionBitmap = bitmapRegionDecoder.decodeRegion(rect, options);

//        int averageColor = getAverageColor(pixels);
//        TextView textView = findViewById(R.id.tv_title);
//        textView.setTextColor(averageColor);

//        ImageView imageView3 = findViewById(R.id.iv_logo);
//        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
//        Bitmap blockBitmap = getBlockBitmap(logoBitmap, logoBitmap.getWidth() / 20);
//        imageView3.setImageBitmap(blockBitmap);
    }

    /**
     * 转成像素图片
     *
     * @param bitmap    原图片
     * @param blockSize 块大小
     * @return
     */
    public static Bitmap getBlockBitmap(Bitmap bitmap, int blockSize) {
        if (bitmap == null)
            throw new IllegalArgumentException("Bitmap cannot be null.");
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();
        Bitmap back = Bitmap.createBitmap((bitmap.getWidth() % blockSize == 0) ? bitmap.getWidth() : ((bitmap.getWidth() / blockSize) * blockSize)
                , (bitmap.getHeight() % blockSize == 0) ? bitmap.getHeight() : ((bitmap.getHeight() / blockSize) * blockSize)
                , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(back);
        canvas.drawColor(0xfff);
        for (int y = 0; y < picHeight; y += blockSize) {
            for (int x = 0; x < picWidth; x += blockSize) {
                int[] colors = getPixels(bitmap, x, y, blockSize, blockSize);

                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setColor(getAverageColor(colors));
                paint.setStyle(Paint.Style.FILL);
                int left = x;
                int top = y;
                int right = x + blockSize;
                int bottom = y + blockSize;

                canvas.drawRect(left, top, right, bottom, paint);

            }
        }

        return back;
    }

    private void testStacktraceGetCost(boolean exceptionStackTrace) {
        long start = System.currentTimeMillis();
        for(long i=0; i<100000; i++) {

            StackTraceElement [] stackTrace;

            if (exceptionStackTrace)
                stackTrace = new Throwable().getStackTrace();
            else
                stackTrace = Thread.currentThread().getStackTrace();
        }

        String desc = exceptionStackTrace ? "exceptionStackTrace" : "currentThread.getStackTrace";
        Log.d("z_test", "testStacktraceGetCost: " + desc + ", cost: " + (System.currentTimeMillis() - start));
    }

    /**
     * 获取某一块的所有像素的颜色
     *
     * @param bitmap
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    private static int[] getPixels(Bitmap bitmap, int x, int y, int w, int h) {
        int[] colors = new int[w * h];
        int idx = 0;
        for (int i = y; (i < h + y) && (i < bitmap.getHeight()); i++) {
            for (int j = x; (j < w + x) && (j < bitmap.getWidth()); j++) {
                int color = bitmap.getPixel(j, i);
                colors[idx++] = color;
            }
        }
        return colors;
    }

    /**
     * 求取多个颜色的平均值
     *
     * @param pixels
     * @return
     */
    private static int getAverageColor(int[] pixels) {
        int red = 0;
        int green = 0;
        int blue = 0;
        for (int color : pixels) {
            red += ((color & 0xff0000) >> 16);
            green += ((color & 0xff00) >> 8);
            blue += (color & 0x0000ff);
        }
        float len = pixels.length;

        red = Math.round(red / len);
        green = Math.round(green / len);
        blue = Math.round(blue / len);

        return Color.argb(0xff, red, green, blue);
    }
}