package example.hubai.lifeweather2.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import example.hubai.lifeweather2.R;
import example.hubai.lifeweather2.util.HttpUtil;
import example.hubai.lifeweather2.view.BesselCurveView;

public class ParamActivity extends AppCompatActivity {
    private BesselCurveView mBesselCurveView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Integer> listStop = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();
    private BluetoothAdapter bluetoothAdapter;
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String NAME = "Bluetooth_Socket";
    private BluetoothDevice selectDevice;
    private BluetoothSocket clientSocket;
    private InputStream is;
    private OutputStream os;
    private String address;
    static int infovalue = 112;
    // 以下是 weather_aqi 内容

    private TextView tmText;

    private TextView rmText;

    private TextView coText;

    private TextView o3Text;

    private TextView pm10Text;

    private TextView so2Text;
    public String district;
    public double lat;
    public double lon;
    public String tm="11";
    public String rh="22";
    public String param1="33";
    public String param2="44";
    Thread thread;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            super.handleMessage(msg);
            String rawvalue = (String) msg.obj;
            if (rawvalue != null) {
                Log.d("recv",rawvalue);
                Log.d("recvlen",String.valueOf(rawvalue.length()));
               if(rawvalue.length()==10 ){
                    tm = rawvalue.substring(rawvalue.indexOf("t")+1,rawvalue.indexOf("r"));
                    rh = rawvalue.substring(rawvalue.indexOf("r")+1);
                   tmText.setText(tm+"C");
                   rmText.setText(rh+"%");
               }
                else if(rawvalue.length()>1){
                   param1 = rawvalue.substring(rawvalue.indexOf("b")+1);
                   param2 = rawvalue.substring(0,rawvalue.indexOf("b"));
                   o3Text.setText(param1);
                   coText.setText(param2);
                   HttpUtil.httpPost(district,lat,lon,tm,rh,param1,param2);
                   Toast.makeText(ParamActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
               }
                // mBesselCurveView.postInvalidate();

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        district = intent.getStringExtra("district");
        lat = intent.getDoubleExtra("lat",(double) 0);
        lon = intent.getDoubleExtra("lon",(double) 0);
        address = intent.getStringExtra("bleaddress");
        setContentView(R.layout.activity_param);
        rmText = (TextView)findViewById(R.id.rm_text);
        tmText = (TextView)findViewById(R.id.tm_text);
        coText = (TextView)findViewById(R.id.co_text);
        o3Text = (TextView)findViewById(R.id.o3_text);
        pm10Text = (TextView)findViewById(R.id.pm10_text);
        so2Text = (TextView)findViewById(R.id.so2_text);
      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);*/
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh) ;
       /* setSupportActionBar(toolbar);*/
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String mhour = String.valueOf(hour);
        String mminute = String.valueOf(minute);
        String time = mhour + ":" + mminute;
        mBesselCurveView = (BesselCurveView) findViewById(R.id.besselCurveView);
        listStop.add(1254);
        listStop.add(8551);
        listStop.add(6352);
        listStop.add(4000);
        listStop.add(5210);
        listStop.add(2390);
        listStop.add(3094);
        mBesselCurveView.setListStep(listStop);
        mBesselCurveView.setFriendAverageStep(999999);
        mBesselCurveView.setAverageStep(2603);
        mBesselCurveView.setChampion("JACK");
        mBesselCurveView.setAllStop(infovalue);
        mBesselCurveView.setTime(time);
        mBesselCurveView.setRanking("");
        mBesselCurveView.setChampion_icon(BitmapFactory.
                decodeResource(getResources(), R.drawable.icon));
        bluetoothInit();
        sendMsg("i");
       // initEvent();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               /* HttpUtil.httpPost(district,lat,lon);
                Toast.makeText(ParamActivity.this, "success", Toast.LENGTH_SHORT).show();*/
                HttpUtil.httpPost(district,lat,lon,tm,rh,param1,param2);
                sendMsg("o");
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ChartActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
   private void bluetoothInit(){
       if (selectDevice == null) {
           selectDevice = bluetoothAdapter.getRemoteDevice(address);
           Log.d("device",selectDevice.getName());
       }
       try {
           if (clientSocket == null) {
               clientSocket = selectDevice.createRfcommSocketToServiceRecord(MY_UUID);
               clientSocket.connect();
               if(clientSocket.isConnected()){
                   Log.d("device","连接成功");
                   Toast.makeText(ParamActivity.this, "蓝牙连接成功", Toast.LENGTH_SHORT).show();
               } else {
                   Toast.makeText(ParamActivity.this, "蓝牙连接失败", Toast.LENGTH_SHORT).show();
               }
               os = clientSocket.getOutputStream();
           }
          /* if (os != null) {
               Log.d("sendinfo",text);
               os.write(text.getBytes("UTF-8"));
               Toast.makeText(ParamActivity.this, "蓝牙发送成功！", Toast.LENGTH_SHORT).show();
               thread = new AcceptThread();
               thread.start();
               swipeRefreshLayout.setRefreshing(false);
           }*/

       } catch (IOException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
           // 如果发生异常则告诉用户发送失败
           Toast.makeText(ParamActivity.this, "蓝牙发送失败！", Toast.LENGTH_SHORT).show();
           swipeRefreshLayout.setRefreshing(false);

       }
   }
   private void sendMsg(String text) {

       if (os != null) {
           try {
               Log.d("sendinfo", text);
               os.write(text.getBytes("UTF-8"));
               Toast.makeText(ParamActivity.this, "蓝牙发送成功！", Toast.LENGTH_SHORT).show();
               thread = new AcceptThread();
               thread.start();
               swipeRefreshLayout.setRefreshing(false);
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
   }

    private class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;
        private BluetoothSocket socket;
        private InputStream is;
        private OutputStream os;

       /* public AcceptThread() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (Exception e) {

            }}*/

        public void run() {
            try {
                //socket = serverSocket.accept();
                is = clientSocket.getInputStream();
                while (true) {
                    Thread.sleep(1000);
                    byte[] buffer = new byte[1024];
                    int count = is.read(buffer);
                    Message msg = new Message();
                    msg.obj = new String(buffer, 0, count, "utf-8");
                    handler.sendMessage(msg);

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       /* if (selectDevice == null) {
            selectDevice = bluetoothAdapter.getRemoteDevice(address);
            Log.d("device",selectDevice.getName());
        }
        try {
            if (clientSocket == null) {
                clientSocket = selectDevice.createRfcommSocketToServiceRecord(MY_UUID);
                clientSocket.connect();
                if(clientSocket.isConnected()){
                    Log.d("device","连接成功");
                }
                os = clientSocket.getOutputStream();
            }
            if (os != null) {
                String text = "1234";
                Log.d("sendinfo",text);
                os.write(text.getBytes("UTF-8"));
            }
            Toast.makeText(this, "发送信息成功，请查收", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // 如果发生异常则告诉用户发送失败
            Toast.makeText(this, "发送信息失败", Toast.LENGTH_SHORT).show();

        }*/
        return super.onTouchEvent(event);
    }

    /*private void initEvent() {
        //mSeekBar.setOnSeekBarChangeListener(this);
        //mEtMax.addTextChangedListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        mBesselCurveView.setAllStop(5000);

                    }
                });
            }
        }).start();
    }*/
}
