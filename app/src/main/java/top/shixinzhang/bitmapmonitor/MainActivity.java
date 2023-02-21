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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.net.URL;

import top.shixinzhang.bitmapmonitor.fragment.LargeBitmapFragment;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    final Handler H = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        SamplePagerAdapter adapter = new SamplePagerAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(adapter);

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
    }

    private void testCreateBitmap() throws Exception{
        //1.通过 BitmapFactory 解码本地、网络图片
        File diskImage = new File(getExternalCacheDir(), "shixin.png");
        Bitmap bitmap = BitmapFactory.decodeFile(diskImage.getAbsolutePath());

        URL url = new URL("www.imagesource.com");
        bitmap = BitmapFactory.decodeStream(url.openStream());

        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo_pixelated_1);

        //2.给 ImageView 设置图片
        ImageView imageView = findViewById(R.id.iv_logo_pixelated_0);

        imageView.setImageDrawable(new BitmapDrawable(bitmap));
        imageView.setImageResource(R.mipmap.logo_pixelated_0);

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
        BitmapRegionDecoder bitmapRegionDecoder = BitmapRegionDecoder.newInstance(diskImage.getAbsolutePath(), false);
        Rect rect = new Rect();     //这里指定要解码的区域
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap decodeRegionBitmap = bitmapRegionDecoder.decodeRegion(rect, options);

        int averageColor = getAverageColor(pixels);
        TextView textView = findViewById(R.id.tv_title);
        textView.setTextColor(averageColor);

        ImageView imageView3 = findViewById(R.id.iv_logo);
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
        Bitmap blockBitmap = getBlockBitmap(logoBitmap, logoBitmap.getWidth() / 20);
        imageView3.setImageBitmap(blockBitmap);


        H.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    testCreateBitmap();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 3_000);
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

    private static class SamplePagerAdapter extends FragmentPagerAdapter {

        public SamplePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0 || position == 1) {
                return new LargeBitmapFragment();
            }
            return null;
        }
    }
}