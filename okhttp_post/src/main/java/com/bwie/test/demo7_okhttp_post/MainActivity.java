package com.bwie.test.demo7_okhttp_post;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText et_qq;
    private EditText et_pwd;
    private TextView tv_status;
    String path = "http://169.254.53.96:8080/web/LoginServlet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        et_qq = (EditText) findViewById(R.id.et_qq);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        tv_status = (TextView) findViewById(R.id.tv_status);
    }

    /**
     * 使用POST进行表单上传,完成登入
     */
    public void login(View view) {
        final String qq = et_qq.getText().toString().trim();
        final String pwd = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(qq) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(MainActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                //创建okhttpclient对象
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)//连接超时
                        .readTimeout(10, TimeUnit.SECONDS)//读取超时
                        .writeTimeout(10, TimeUnit.SECONDS)//写入超时
                        .build();
                FormBody formBody = new FormBody.Builder()
                        .add("qq", qq)
                        .add("pwd", pwd)
                        .build();
                Request build = new Request.Builder()
                        .post(formBody)
                        .url(path)
                        .build();
                Call call = okHttpClient.newCall(build);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String s = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_status.setText(s);
                            }
                        });
                    }
                });
            }
        }.start();
    }
}
