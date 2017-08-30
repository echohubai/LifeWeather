package example.hubai.lifeweather2.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;


import java.io.IOException;

import example.hubai.lifeweather2.gson.Weather;
import example.hubai.lifeweather2.util.HttpUtil;
import example.hubai.lifeweather2.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
/**
 * Created by hubai on 2017/7/27.
 */

public class WebService {
    final String TAG = "WebTest";
    /**
     * post请求
     * @param view
     */
    public void httpPost(View view){
        //换成自己的ip就行
        String url = "http://10.104.4.1:8080/okhttp/LoginServlet";
        OkHttpClient client = new OkHttpClient();//创建okhttp实例
        FormBody body=new FormBody.Builder()
                .add("name","张三")
                .add("age","23")
                .build();
        Request request=new Request.Builder().post(body).url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            //请求失败时调用
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: " + e);
            }
            //请求成功时调用
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: " + response.body().string());
                }
            }
        });
    }
}

