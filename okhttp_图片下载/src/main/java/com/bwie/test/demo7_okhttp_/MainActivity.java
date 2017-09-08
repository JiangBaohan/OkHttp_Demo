package com.bwie.test.demo7_okhttp_;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**okhttp网络二次封装工具类,单例模式,handler,定义接口
 *
 * xutils,httpclient,okhttp,retrofit,volley(不能做大文件下载) ,nohttp.....
 * 1.每一个网络框架,都有自己的特点,所以再封装各不相同,因此再封装网络框架之前,要先充分了解所
 * 封装的网络框架
 * 2.代码冗余的共性,可以对他去安装自己的需求解决
 * 3.封装完,网络框架性能要更优化,节约内存,逻辑处理变的更简单
 * OKHTTP封装好处:
 * 1.代码简化
 * 2.不用开子线程,就可以在主线程中使用工具类完成需求,不用做线程间交互,逻辑会更简单
 * 3.节约内存,是所有网络请求都公用一个okhttpclient对象和handler对象
 *
 */
public class MainActivity extends AppCompatActivity {
   // private String path = "https://link.zhihu.com/?target=https%3A//unsplash.it/400/800/%3Frandom";
   private String path="https://10.url.cn/eth/ajNVdqHZLLAxibwnrOxXSzIxA76ichutwMCcOpA45xjiapneMZsib7eY4wUxF6XDmL2FmZEVYsf86iaw/";
    private ImageView imageView_okhttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView_okhttp = (ImageView) findViewById(R.id.imageView_okhttp);
    }

    public void Picture_okhttp_bt(View view) {
       new Thread(){
                   @Override
                   public void run() {
                       super.run();
                       int chcheSize = 10 * 1024 * 1024;//10M
                       Cache cache = new Cache(getCacheDir(), chcheSize);
                       OkHttpClient okHttpClient = new OkHttpClient.Builder().cache(cache).build();
                       Request.Builder builder = new Request.Builder();
                       Request.Builder url = builder.url(path);
                       Request request = url.build();
                       okhttp3.Call call = okHttpClient.newCall(request);


                       call.enqueue(new Callback() {
                           @Override
                           public void onFailure(okhttp3.Call call, IOException e) {

                           }

                           @Override
                           public void onResponse(okhttp3.Call call, Response response) throws IOException {
                               byte[] bytes = response.body().bytes();
                               final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       imageView_okhttp.setImageBitmap(bitmap);
                                   }
                               });
                           }
                       });
                   }
               }.start();
        }

    /**
     * 当按钮点击时,之行使用okhttp上传图片到服务器的
     注意:有时候上传图片事变,是服务器规定还要上传一个key,如果开发中关于网络这一块处理问题,
     就多和WEB人员交流

     * @param view
     */
    public void uploading(View view){
        //上传接口
        String url="";
        //上传文件对象
        File file = new File(Environment.getExternalStorageDirectory(), "big,jpg");
        //创建RequestBody封装参数
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image","big.jpg",requestBody)
                .build();
        //创建okHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        //创建request对象
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        //上传完图片,得到服务器反馈数据
okHttpClient.newCall(request).enqueue(new Callback() {
    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String string = response.body().string();
        Log.d("hb",string);
    }
});
    }
    }
