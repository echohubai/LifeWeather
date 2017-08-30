package example.hubai.lifeweather2.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import example.hubai.lifeweather2.R;

/**
 * Created by hubai on 2017/3/8.
 */

public class BLEActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    public static String district;
    public static double lat;
    public static double lon;
    private BluetoothAdapter bluetoothAdapter;
    private List<String> Devices = new ArrayList<String>();
    private ListView IDevices;
    private ArrayAdapter<String> adapter;
    private final UUID MY_UUID = UUID.fromString("db764ac8-4b08-7f25-aafe-59d03c27bae3");
    private final String NAME = "Bluetooth_Socket";
    private BluetoothDevice selectDevice;
    private BluetoothSocket clientSocket;
    private OutputStream os;
    private String info="hhhhhh";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        Intent intent = getIntent();
        district = intent.getStringExtra("district");
        lat = intent.getDoubleExtra("lat",(double) 0);
        lon = intent.getDoubleExtra("lon",(double) 0);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IDevices = (ListView) findViewById(R.id.dev);
       //info = getIntent().getStringExtra("info");
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, Devices);
        IDevices.setAdapter(adapter);
        IDevices.setOnItemClickListener(this);
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : devices) {
                Devices.add(bluetoothDevice.getName() + ":" + bluetoothDevice.getAddress() + "\n");
            }
        }
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_NAME);
                if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Devices.add(bluetoothDevice.getName() + ":" + bluetoothDevice.getAddress() + "\n");
                    adapter.notifyDataSetChanged();
                } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    setTitle("搜索完成");
                }
            }
        }
    };
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String s = adapter.getItem(position);
        String address = s.substring(s.indexOf(":") + 1).trim();
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        Intent intent = new Intent(this,ParamActivity.class);
        intent.putExtra("bleaddress",address);
        intent.putExtra("district",district);
        intent.putExtra("lon",lon);
        intent.putExtra("lat",lat);
        startActivity(intent);
    }
}
