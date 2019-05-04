package com.example.screenprotect;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ScreenSaverActivity extends Activity {
    protected static final String TAG = "ScreenSaverActivity";
    private static PowerManager.WakeLock mWakeLock;
    private static final int MSGKEY = 0x10001;
    //	private PlayControl mPlayControl;
    //屏保右上角的系统时间
    private TextView mTime;
    //图片集合
    private List<String> images;
    //标题集合
    private List<String> imageTitle;
    private List<String> imageChTitle;
    private Banner mBanner;
    // 退出时间
    private long exitTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_screen);
        // 设置时间
        mTime = (TextView)findViewById(R.id.mytime);
        //启动线程刷新屏保界面右上角的时间
        new TimeThread().start();
        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.SCREEN_DIM_WAKE_LOCK |
                PowerManager.ON_AFTER_RELEASE, "SimpleTimer");
        // 设置轮播图
        //初始化数据
        initData();
        //初始化view
        initView();
    }
    public class TimeThread extends Thread
    {
        @Override
        public void run()
        {
            do
            {
                try
                {
                    //更新时间
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = MSGKEY;
                    mHandler.sendMessage(msg);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    // 新线程
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case MSGKEY:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("hh:mm", sysTime);
                    mTime.setText(sysTimeStr);
                    break;

            }
        }
    };

    private void initView() {
        mBanner = findViewById(R.id.banner);
        //设置样式,默认为:Banner.NOT_INDICATOR(不显示指示器和标题)
        //可选样式如下:
        //1. Banner.CIRCLE_INDICATOR    显示圆形指示器
        //2. Banner.NUM_INDICATOR   显示数字指示器
        //3. Banner.NUM_INDICATOR_TITLE 显示数字指示器和标题
        //4. Banner.CIRCLE_INDICATOR_TITLE  显示圆形指示器和标题
        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());
        //设置标题集合（当banner样式有显示title时）
        mBanner.setBannerTitles(imageTitle);
        //设置轮播样式（没有标题默认为右边,有标题时默认左边）
        //可选样式:
        //Banner.LEFT   指示器居左
        //Banner.CENTER 指示器居中
        //Banner.RIGHT  指示器居右
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        //设置是否允许手动滑动轮播图
        mBanner.setViewPagerIsScroll(true);
        //设置是否自动轮播（不设置则默认自动）
        mBanner.isAutoPlay(true);
        //设置轮播图片间隔时间（不设置默认为2000）
        mBanner.setDelayTime(2000);
        //设置图片资源:可选图片网址/资源文件，默认用Glide加载,也可自定义图片的加载框架
        //所有设置参数方法都放在此方法之前执行
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.setImages(images)
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
//                        Toast.makeText(ScreenSaverActivity.this, "这是第" + (position + 1) + "张图片", Toast.LENGTH_SHORT).show();
                        Toast.makeText(ScreenSaverActivity.this, imageChTitle.get(position), Toast.LENGTH_SHORT).show();
                    }
                })
                .start();

    }
    private void initData() {
        //设置图片资源:url或本地资源
        images = new ArrayList<>();
        images.add("https://www.fzlu.net/uploads/allimg/150827/1-150RG9425H39.jpeg");
        images.add("http://old.bz55.com/uploads/allimg/140627/138-14062G43954.jpg");
        images.add("http://d.5857.com/ywjz_170517/008.jpg");
        images.add("http://www.5857.com/uploadfile/2017/0523/20170523021219677.jpg");
        images.add("http://pic.qqtn.com/file/2013/2015-3/2015032409365956698.jpg");
        images.add("http://pic.qqtn.com/file/2013/2015-3/2015032409365990876.jpg");

        //设置图片标题:自动对应
        imageTitle = new ArrayList<>();
        imageTitle.add("look at the stars,look how they shine for you");
        imageTitle.add("baby you are my destiny");
        imageTitle.add("be yourself and you can be anything");
        imageTitle.add("never dream for success, but work for it");
        imageTitle.add("I'm not prefect, but I'm always myself");
        imageTitle.add("keep going and you are getting there");

        // 设置中文翻译
        imageChTitle = new ArrayList<>();
        imageChTitle.add("看看星星，看它们怎样为你闪耀");
        imageChTitle.add("你是我的宿命");
        imageChTitle.add("做你自己，你可以做任何事");
        imageChTitle.add("从不梦想成功，但为之努力");
        imageChTitle.add("虽不完美，但我就是我");
        imageChTitle.add("坚持下去，终会到达");

    }

    /**
     * 网络加载图片
     * 使用了Glide图片加载框架
     */
    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context)
                    .load((String) path)
                    .into(imageView);
        }

    }



    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        //mHandler01.postDelayed(mTasks01, intervalKeypadeSaver);
        mWakeLock.acquire();
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        mWakeLock.release();
        super.onPause();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit()
    {
        if ((System.currentTimeMillis() - exitTime) > 2000)
        {
            Toast.makeText(getApplicationContext(), "再按一次即可退出应用", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        }
        else
        {
            finish();
        }
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // TODO Auto-generated method stub
//        finish();
//        return super.onTouchEvent(event);
//    }
    //搜索键
    @Override
    public boolean onSearchRequested() {
        // TODO Auto-generated method stub
        finish();
        return super.onSearchRequested();
    }

}