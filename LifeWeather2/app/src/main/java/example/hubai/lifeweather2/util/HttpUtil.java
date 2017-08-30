package example.hubai.lifeweather2.util;

import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by len_titude on 2017/4/26.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
    final static String TAG = "WebTest";

    public static void httpPost(String district,double lat,double lon,String tm,String rh,String param1,String param2){
        String latdata = String.valueOf(lat);
        String londata = String.valueOf(lon);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(new Date());
        //换成自己的ip就行
        String url = "http://192.168.0.103:8082/test";
        OkHttpClient client = new OkHttpClient();//创建okhttp实例
        FormBody body=new FormBody.Builder()
                .add("name","device 1")
                .add("age","23")
                .add("district",district)
                .add("lat",latdata)
                .add("lon",londata)
                .add("tm",tm)
                .add("rh",rh)
                .add("param1",param1)
                .add("param2",param2)
                .add("time",str)
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
