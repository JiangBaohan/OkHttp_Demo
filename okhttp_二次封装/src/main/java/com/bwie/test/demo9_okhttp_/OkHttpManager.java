package com.bwie.test.demo9_okhttp_;

import android.os.Handler;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * data:2017/9/6
 * author:汉堡(Administrator)
 * function:终极封装,实现两个功能,从服务端下载数据;从客户端提交数据
 * 封装优秀的okhttp:okhttpUtils,OKGO(更深入的封装,研究OKGO)
 * 1.节约内存,使所有的网络请求都用一个okhttpclient和handler对象
 * 2.解决okhttp,网络请求成功,代码再子线程的问题,把请求成功后的逻辑代码放到主线程中执行
 * 3.简化代码
 * <p>
 * 这次封装用到哪些知识点
 * 1.单例模式 2.handler 3.接口  4. okhttp
 */

public class OkHttpManager {
    /****************************************
     * 定义成员变量,使用构造方法,完成初始化     *
     * 使用单例模式,通过获取的方式拿到对象      *
     * 定义接口,使handle,接口处理逻辑在主线程   *
     * 暴露提供给外界调用的方法                *
     *****************************************/
    private OkHttpClient mClient;
    private static Handler mhandler;
    //防止多个线程同时访问,volatile
    private volatile static OkHttpManager instance = null;

    //单例模式构造方法权限要私有,保证对象的唯一性(EventBus,如果看源码,
    // 他的构造方法是public,所以一方面可以通过单例方法拿到对象,
    // 一方面可以通过new的方式拿到)
    private OkHttpManager() {
        mClient = new OkHttpClient();
       /* okHttpClient.newBuilder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);*/

        mhandler = new Handler();
    }

    public static OkHttpManager getInstance() {
        if (instance == null) {
            synchronized (OkHttpManager.class) {
                if (instance == null) {
                    instance = new OkHttpManager();
                }
            }
        }
        return instance;
    }

    interface Func1 {
        void onResponse(String result);
    }

    interface Func2 {
        void onResponse(byte[] result);
    }

    interface Func3 {
        void onResponse(JSONObject jsonObject);
    }

    //处理请求网络成功的方法,返回的结果是JSON字符串
    private static void onSuccessJsonStringMethod(final String jsonValue, final Func1 callBack) {
        //用的是mhandler.post方法,把数据放到主线程中,也可以用EventBus或RxJava的线程调度器去完成
        mhandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    //ctrl+alt+t=======try,cath
                    try {
                        callBack.onResponse(jsonValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //处理请求网络成功的方法,返回的结果是byte[]字节
    private void onSuccessJsonStringMethodImage(final byte[] bytes, final Func2 callBack) {
        //用的是mhandler.post方法,把数据放到主线程中,也可以用EventBus或RxJava的线程调度器去完成
        mhandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    //ctrl+alt+t=======try,cath
                    try {
                        callBack.onResponse(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 根据请求的UTL返回的结果是JSON字符串
     */
    public void asyncJsonStringByURL(String url, final Func1 callBack) {
        Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //判断response是否有对象,成功
                if (response != null && response.isSuccessful()) {
                    onSuccessJsonStringMethod(response.body().string(), callBack);
                }
            }
        });
    }

    /**
     * 提交表单
     */
    public void sendComplexForm(String url, Map<String, String> params, final Func1 callBack) {
        //表单对象
        FormBody.Builder form_builder = new FormBody.Builder();
        //键值非空判断
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                form_builder.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody request_body = form_builder.build();
        Request request = new Request.Builder().url(url).post(request_body).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //判断response是否有对象,成功
                if (response != null && response.isSuccessful()) {
                    onSuccessJsonStringMethod(response.body().string(), callBack);
                }
            }
        });
    }

    public void DownImageByeByURL(String url, final Func2 callBack) {
        Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //判断response是否有对象,成功
                if (response != null && response.isSuccessful()) {
                    onSuccessJsonStringMethodImage(response.body().bytes(), callBack);
                }
            }
        });
    }


}
