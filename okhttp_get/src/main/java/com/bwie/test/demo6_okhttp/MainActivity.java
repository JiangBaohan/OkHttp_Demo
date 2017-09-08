package com.bwie.test.demo6_okhttp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final int SUCCESS = 4645;
    private String path = "http://v.juhe.cn/toutiao/index?type=beijing&key=e76b62dbe5ce78645516fe866dc7058b";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    String obj = (String) msg.obj;

                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv=(TextView) findViewById(R.id.ppp);
    }

    public void okhttp_ok(View view) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                //创建client
                OkHttpClient client = new OkHttpClient.Builder().build();
                //创建请求对象
                Request request = new Request.Builder().url(path).build();


                try {
                    //同步
           /* Response response = client.newCall(request).execute();
            String string = response.body().string();
            Log.d("hb",string);*/
                    //异步
                    client.newCall(request).enqueue(new Callback() {
                        @Override//失败的回调
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override//成功的回调
                        public void onResponse(Call call, Response response) throws IOException {
                            String string = response.body().string();
                            Log.d("hb", string);
                            String name = Thread.currentThread().getName();
                            System.out.println("okhttp成功回调方法运行再什么线程" + name);
                            //Message message = new Message();
                            Message obtain = Message.obtain();
                            obtain.obj = string;
                            obtain.what = SUCCESS;
                            handler.sendMessage(obtain);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 通过点击事件,异步网络请求,拿到返回String数据,并进行本地缓存
     */
    public void okhttp_cache(View view) {
        new Thread() {
            @Override
            public void run() {
                int chcheSize = 10 * 1024 * 1024;//10M
                Cache cache = new Cache(getCacheDir(), chcheSize);
                OkHttpClient okHttpClient = new OkHttpClient.Builder().cache(cache).build();
                Request.Builder builder = new Request.Builder();
                Request.Builder url = builder.url(path);
                Request request = url.build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String string = response.body().string();
                        Log.d("hb", string);
                    }
                });
            }
        }.start();

    }
}
