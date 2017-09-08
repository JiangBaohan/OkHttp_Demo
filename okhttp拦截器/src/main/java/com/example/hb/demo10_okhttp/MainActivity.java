package com.example.hb.demo10_okhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

/**
 * okhttp底层网络请求用的是Socket,长连接
 */
public class MainActivity extends AppCompatActivity {
    private String Path = "http://publicobject.com/helloworld.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 应用拦截器
     * @param view
     */
    public void interceptor(View view){
         new Thread(){
                     @Override
                     public void run() {
                         try {
                             //建立okhttp对象时,传入拦截器对象
                             //注意:
                             OkHttpClient builder = new OkHttpClient.Builder().addInterceptor(new LoggingInterceptor()).build();
                            // OkHttpClient builder = new OkHttpClient.Builder().addInterceptor(new CacheInterecepor()).build();
                             Request request = new Request.Builder().url(Path).build();
                             Response response = builder.newCall(request).execute();
                             String string = response.body().string();
                             Log.d("hb",string);
                         } catch (Exception e) {


                         }
                     }
                 }.start();

    }

    /**
     * 根据点击事件使用okhttp,网络拦截器
     * @param view
     */
    public void interceptorNetWork(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    //建立okhttp对象时,传入拦截器对象addNetworkInterceptor
                    //缓存拦截器:注意有两个,我们导包时用自己的,不要弄错了
                   OkHttpClient builder = new OkHttpClient.Builder().addNetworkInterceptor(new LoggingInterceptor()).build();
                   // OkHttpClient builder = new OkHttpClient.Builder().addInterceptor(new CacheInterecepor()).build();
                    Request request = new Request.Builder().url(Path).build();
                    Response response = builder.newCall(request).execute();
                    String string = response.body().string();
                    Log.d("hb",string);
                } catch (Exception e) {


                }
            }
        }.start();

    }








   /*日志拦截器:请求来了,先在这里处理,可以计算发送请求和得到请求所消耗的时间
   作用:可以排查网络请求速度慢的根本原因:
   1.可能网络不给力
   2.可能是服务端,有问题(硬件,逻辑代码)
   3.可能是客户端的问题(逻辑代码)
   * */
    class LoggingInterceptor implements Interceptor {
        @Override public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();


            long t1 = System.nanoTime();

            System.out.println(" request  = " + String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));
            Response response = chain.proceed(request);

            long t2 = System.nanoTime();

            //得出请求网络,到得到结果,中间消耗了多长时间
            System.out.println("response  " + String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            return response;
        }
    }

    /**
     * 压缩拦截器:压缩请求的内容,服务器的支持
     * 提示:http1.1默认进行压缩
     */
    class GzipRequestInterceptor implements Interceptor {
        @Override public Response intercept(Interceptor.Chain chain) throws IOException {
            Request originalRequest = chain.request();
            if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
                return chain.proceed(originalRequest);
            }

            Request compressedRequest = originalRequest.newBuilder()
                    .header("Content-Encoding", "gzip")
                    .method(originalRequest.method(), gzip(originalRequest.body()))
                    .build();
            return chain.proceed(compressedRequest);
        }

        private RequestBody gzip(final RequestBody body) {
            return new RequestBody() {
                @Override public MediaType contentType() {
                    return body.contentType();
                }

                @Override public long contentLength() {
                    return -1; // We don't know the compressed length in advance!
                }

                @Override public void writeTo(BufferedSink sink) throws IOException {
                    BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                    body.writeTo(gzipSink);
                    gzipSink.close();
                }
            };
        }
    }


}
