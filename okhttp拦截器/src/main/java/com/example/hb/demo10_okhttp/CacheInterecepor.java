package com.example.hb.demo10_okhttp;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * data:2017/9/7
 * author:汉堡(Administrator)
 * function:自定义的缓存拦截器:如果服务器没有给文件再响应头中定义缓存标签,那么我们再拦截器中手动的给响应头加上标签
 * 1.自定义一个类,实现Interecepor
 * 2.再intercept方法中写自己的逻辑
 */

public class CacheInterecepor implements Interceptor{
    /**
     * 数据返回过来有响应头,封装网络工具类,他是拿到响应头中的缓存标签,来决定,
     * 这一次请求的数据是否要进行缓存,如果没有,那么就不做缓存
     * 服务器那边有问题,本来这个数据是要做的缓存,他没有再响应头中添加标签,那么此时
     * 这一个数据判断是没有缓存标签,就不会做缓存,我要为这条数据重新写网络请求,很麻烦
     * 这时可以手动去响应头中添加标签
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        //得到Response对象
        Response response = chain.proceed(chain.request());
        Response build = response.newBuilder()
                //设置缓存标签,60秒的时长
                .header("Cache-Control", "max-age=60")
                .build();

        return build;
    }
}
