package com.li.videoapplication.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.videoapplication.R;
import com.li.videoapplication.entity.RecommendEntity;
import com.li.videoapplication.utils.JsonHelper;
import com.li.videoapplication.utils.ToastUtils;
import com.umeng.message.PushAgent;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by feimoliaochanghe on 2015/2/2.
 * 首页 抢福利 页面
 */
public class RecommendActivity extends Activity implements View.OnClickListener {

    private Context context;
    private LinearLayout linearLayout1 = null;
    private LinearLayout linearLayout2 = null;
    private ImageButton backBtn;
    private List<RecommendEntity> recommendEntities;
    private LayoutInflater inflater;
    //定义一个HashMap，保存软引用对象，防止OOM
    private Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
    /**
     * 记录所有界面上的图片，用以可以随时控制对图片的释放。
     */
    private List<ImageView> imageViewList = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        context = RecommendActivity.this;

        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();

        init();
    }

    private void init() {
        inflater = getLayoutInflater();
        linearLayout1 = (LinearLayout) findViewById(R.id.activity_recommend_linearlayout1);
        linearLayout2 = (LinearLayout) findViewById(R.id.activity_recommend_linearlayout2);
        backBtn = (ImageButton) findViewById(R.id.activity_recommend_back);
        backBtn.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetRecommendListTask("1").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetRecommendListTask("1").execute();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activity_recommend_back) {
            finish();
        } else {
            int i = (Integer) v.getTag();
            String type = recommendEntities.get(i).getType();
            if ("video".equals(type)) {
                Intent intent = new Intent(getApplication(), VideoPlayActivity.class);
                intent.putExtra("id", recommendEntities.get(i).getVideo_id());
                startActivity(intent);
            }
            if ("activity".equals(type)) {
                Intent intent = new Intent(getApplication(), ActivityDetailActivity.class);
                intent.putExtra("id", recommendEntities.get(i).getActivity_id());
                startActivity(intent);
            }
            if ("package".equals(type)) {
                Intent intent = new Intent(getApplication(), GiftAtuoDetailActivity.class);
                intent.putExtra("id", recommendEntities.get(i).getPackage_id());
                startActivity(intent);
            }
        }
    }

    private void addToAsynLoadImage(String imageUrl, int j, int i) {
        View view = inflater.inflate(R.layout.recommend_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.recommend_item_img);
        TextView textView = (TextView) view.findViewById(R.id.recommend_item_name);
        textView.setText(recommendEntities.get(i).getTitle());
//        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(0,0,0,10);
//        imageView.setLayoutParams(layoutParams);
        imageView.setTag(i);
        imageView.setOnClickListener(this);
        imageViewList.add(imageView);
        new ImageDownloadAsynTask(RecommendActivity.this, imageUrl, imageView).execute();
        if (j == 0) {
            linearLayout1.addView(view);
        } else if (j == 1) {
            linearLayout2.addView(view);
        }
    }

    public class ImageDownloadAsynTask extends AsyncTask<Void, Void, Bitmap> {

        private Context context;
        private String imageUrl;
        private ImageView imageView;
        private String sdPath = Environment.getExternalStorageDirectory() + File.separator + "videoapplication/netImages";

        public ImageDownloadAsynTask(Context context, String imageUrl, ImageView imageView) {
            this.context = context;
            this.imageUrl = imageUrl;
            this.imageView = imageView;
        }

        /* 后台执行，比较耗时的操作都可以放在这里。注意这里不能直接操作UI
         * 不需要传入什么参数，返回一个Drawable
         */
        @Override
        protected Bitmap doInBackground(Void... params) {
            String filename = sdPath + imageUrl.substring(imageUrl.lastIndexOf("/"));
            File file = new File(filename);
            if (file.exists() == true) {
                Bitmap bitmap = BitmapFactory.decodeFile(filename);
//                BitmapDrawable bitmapDrawable=new BitmapDrawable(bitmap);
                return bitmap;
            } else {
                try {
//                    URL url=new URL(imageUrl);
//                    URLConnection connection=url.openConnection();
//                    connection.setDoInput(true);// 使用 URL 连接进行输入
//                    connection.connect();
//                    InputStream is = connection.getInputStream();
//                    Bitmap b=compressImage(BitmapFactory.decodeStream(is));
                    System.out.println("++++++++++++++++++++++");
                    Bitmap b = convertToBitmap(imageUrl);
                    BitmapDrawable bd = new BitmapDrawable(b);
                    saveFile(bd, filename);
//				connection.getContent();
                    return b;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


        /**
         * 通过outPutStream、bitmap.compress(),flush()把图片保存到指定路径
         *
         * @param bd
         * @param filename
         */
        private void saveFile(BitmapDrawable bd, String filename) {
            File file = new File(sdPath);
            if (!file.exists()) {
                file.mkdir();
            }
            File f = new File(filename);
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
                Bitmap b = bd.getBitmap();
                b.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 相当于Handler 处理UI的方式，在这里面可以使用在doInBackground 得到的结果处理操作UI。
         * 此方法在主线程执行，任务执行的结果作为此方法的参数返回
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {//如果doInBackground()获取的结果不为空
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, 10);
                imageView.setLayoutParams(layoutParams);
                imageView.setAdjustViewBounds(true);
                imageView.setImageBitmap(result);//那么就在这一步更新UI
//                if (mark == 1) {
//                    imageView.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
//                }
            }
        }

        //路径转换Bitmap
        public Bitmap convertToBitmap(String path) {

            if (getBitmapByPath(path) != null) {
                return getBitmapByPath(path);
            } else {
                return addBitmapToCache(path);
            }
        }

        //保存Bitmap的软引用到HashMap
        public Bitmap addBitmapToCache(String path) {
            URL url = null;
            try {
                url = new URL(imageUrl);
                URLConnection connection = url.openConnection();
                connection.setDoInput(true);// 使用 URL 连接进行输入
                connection.connect();
                InputStream is = connection.getInputStream();
                Bitmap bitmap = compressImage(BitmapFactory.decodeStream(is));
                // 软引用的Bitmap对象
                SoftReference<Bitmap> softBitmap = new SoftReference<Bitmap>(bitmap);
                // 添加该对象到Map中使其缓存
                imageCache.put(path, softBitmap);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //通过SoftReference的get()方法得到Bitmap对象
        public Bitmap getBitmapByPath(String path) {
            // 从缓存中取软引用的Bitmap对象
            SoftReference<Bitmap> softBitmap = imageCache.get(path);
            // 判断是否存在软引用
            if (softBitmap == null) {
                return null;
            }
            // 取出Bitmap对象，如果由于内存不足Bitmap被回收，将取得空
            Bitmap bitmap = softBitmap.get();
            return bitmap;
        }
    }


    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 40) {  //循环判断如果压缩后图片是否大于40kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

//    private Bitmap comp(Bitmap image) {
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
//            baos.reset();//重置baos即清空baos
//            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
//        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
//        BitmapFactory.Options newOpts = new BitmapFactory.Options();
//        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
//        newOpts.inJustDecodeBounds = true;
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
//        newOpts.inJustDecodeBounds = false;
//        int w = newOpts.outWidth;
//        int h = newOpts.outHeight;
//        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//        float hh = 350f;//这里设置高度为800f
//        float ww = 700f;//这里设置宽度为480f
//        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//        int be = 1;//be=1表示不缩放
//        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
//            be = (int) (newOpts.outWidth / ww);
//        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
//            be = (int) (newOpts.outHeight / hh);
//        }
//        if (be <= 0)
//            be = 1;
//        newOpts.inSampleSize = be;//设置缩放比例
//        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
//        isBm = new ByteArrayInputStream(baos.toByteArray());
//        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
//        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
//    }


    private class GetRecommendListTask extends AsyncTask<Void, Void, String> {

        private String page;

        public GetRecommendListTask(String page) {
            this.page = page;
        }

        @Override
        protected String doInBackground(Void... params) {
            recommendEntities = JsonHelper.getRecommendIngList(page);
            if (recommendEntities != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("s")) {
                int j = 0;
                for (int i = 0; i < recommendEntities.size(); i++) {
                    j = j % 2;
                    addToAsynLoadImage(recommendEntities.get(i).getFlagPath(), j, i);
                    j++;
                }
            } else {
                ToastUtils.showToast(RecommendActivity.this, "获取推荐数据失败！");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
