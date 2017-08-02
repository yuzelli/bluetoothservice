package com.example.yuzelli.bluetoothservice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class ServerActivity extends Activity implements OnClickListener {
    private final static String TAG = "ServerActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private Context mContext;

    private Button mBtnBluetoothVisibility;
    private Button mBtnBluetoohDisconnect;
    private Button mBtnSendMessage;
    private Button btn_beginsendmessage;
    //private EditText mEdttMessage;
    private EditText et_jiaquan;
    private EditText et_ben;
    private EditText et_co2;
    private EditText et_co;
    private EditText et_so2;
    private EditText et_no;

    private boolean maySendMessage = false;
    private TextView mBtConnectState;
    private ProgressDialog mProgressDialog;
    private BluetoothChatUtil mBlthChatUtil;


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothChatUtil.STATE_CONNECTED:
                    String deviceName = msg.getData().getString(BluetoothChatUtil.DEVICE_NAME);
                    mBtConnectState.setText("已成功连接到设备" + deviceName);
                    maySendMessage = true;
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
                case BluetoothChatUtil.STATAE_CONNECT_FAILURE:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    maySendMessage = false;
                    Toast.makeText(getApplicationContext(), "连接失败", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothChatUtil.MESSAGE_DISCONNECTED:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    maySendMessage = false;
                    btn_beginsendmessage.setText("开始数据传输");
                    mBtConnectState.setText("与设备断开连接");
                    mBlthChatUtil.startListen();
                    break;
                case BluetoothChatUtil.MESSAGE_READ: {
                    byte[] buf = msg.getData().getByteArray(BluetoothChatUtil.READ_MSG);
                    String str = new String(buf, 0, buf.length);
                    Toast.makeText(getApplicationContext(), "读成功" + str, Toast.LENGTH_SHORT).show();
                    //mTvChat.setText(mTvChat.getText().toString()+"\n"+str);
                    break;
                }
                case BluetoothChatUtil.MESSAGE_WRITE: {
                    byte[] buf = (byte[]) msg.obj;
                    String str = new String(buf, 0, buf.length);
                    Toast.makeText(getApplicationContext(), "发送成功" + str, Toast.LENGTH_SHORT).show();
                    break;
                }
                case 3007: {
                    if (maySendMessage){
                        mHandler.sendEmptyMessageAtTime(3007,1000*30);
                        sendMessInfo();
                        btn_beginsendmessage.setText("正在传输数据");
                    }else {
                        btn_beginsendmessage.setText("开始数据传输");
                    }


                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server);
        mContext = this;
        initView();
        initBluetooth();
        mBlthChatUtil = BluetoothChatUtil.getInstance(mContext);
        mBlthChatUtil.registerHandler(mHandler);
    }

    private void initView() {

        mBtnBluetoothVisibility = (Button) findViewById(R.id.btn_blth_visiblity);
        mBtnBluetoohDisconnect = (Button) findViewById(R.id.btn_blth_disconnect);
        mBtnSendMessage = (Button) findViewById(R.id.btn_sendmessage);
        btn_beginsendmessage = (Button) findViewById(R.id.btn_beginsendmessage);

        mBtConnectState = (TextView) findViewById(R.id.tv_connect_state);


        et_jiaquan = findViewById(R.id.et_jiaquan);
        et_ben = findViewById(R.id.et_ben);
        et_co2 = findViewById(R.id.et_co2);
        et_co = findViewById(R.id.et_co);
        et_so2 = findViewById(R.id.et_so2);
        et_no = findViewById(R.id.et_no);

        mBtnBluetoothVisibility.setOnClickListener(this);
        btn_beginsendmessage.setOnClickListener(this);
        mBtnBluetoohDisconnect.setOnClickListener(this);
        mBtnSendMessage.setOnClickListener(this);
        mProgressDialog = new ProgressDialog(this);
    }

    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {//设备不支持蓝牙
            Toast.makeText(getApplicationContext(), "设备不支持蓝牙", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        //判断蓝牙是否开启
        if (!mBluetoothAdapter.isEnabled()) {//蓝牙未开启
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            //mBluetoothAdapter.enable();此方法直接开启蓝牙，不建议这样用。
        }
        //设置蓝牙可见性
        if (mBluetoothAdapter.isEnabled()) {
            if (mBluetoothAdapter.getScanMode() !=
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                Intent discoverableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(
                        BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                startActivity(discoverableIntent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult request=" + requestCode + " result=" + resultCode);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) return;
        if (mBlthChatUtil != null) {
            // 只有国家是state_none，我们知道，我们还没有开始
            if (mBlthChatUtil.getState() == BluetoothChatUtil.STATE_NONE) {
                // 启动蓝牙聊天服务
                mBlthChatUtil.startListen();
            } else if (mBlthChatUtil.getState() == BluetoothChatUtil.STATE_CONNECTED) {
                BluetoothDevice device = mBlthChatUtil.getConnectedDevice();
                if (null != device && null != device.getName()) {
                    mBtConnectState.setText("已成功连接到设备" + device.getName());
                } else {
                    mBtConnectState.setText("已成功连接到设备");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_blth_visiblity:
                if (mBluetoothAdapter.isEnabled()) {
                    if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                        Intent discoveryIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        discoveryIntent.putExtra(
                                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                        startActivity(discoveryIntent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.bluetooth_unopened), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_blth_disconnect:
                if (mBlthChatUtil.getState() != BluetoothChatUtil.STATE_CONNECTED) {
                    Toast.makeText(mContext, "蓝牙未连接", Toast.LENGTH_SHORT).show();
                } else {
                    mBlthChatUtil.disconnect();
                }
                break;
            case R.id.btn_sendmessage:
                sendMessInfo();

                break;
            case R.id.btn_beginsendmessage:
                if (maySendMessage){
                    mHandler.sendEmptyMessage(3007);
                    btn_beginsendmessage.setText("正在传输数据");
                }else {
                    Toast.makeText(ServerActivity.this,"当前不能开始传输数据",Toast.LENGTH_SHORT).show();
                    btn_beginsendmessage.setText("开始数据传输");
                }
                break;
            default:
                break;
        }
    }

    private void sendMessInfo() {
        String jiaquan = et_jiaquan.getText().toString().trim();
        String ben = et_ben.getText().toString().trim();
        String co2 = et_co2.getText().toString().trim();
        String co = et_co.getText().toString().trim();
        String so2 = et_so2.getText().toString().trim();
        String no = et_no.getText().toString().trim();
        if (jiaquan.equals("")) {
            Toast.makeText(ServerActivity.this, "请输入甲醛值", Toast.LENGTH_SHORT).show();
        }
        if (ben.equals("")) {
            Toast.makeText(ServerActivity.this, "请输入苯值", Toast.LENGTH_SHORT).show();
        }
        if (co2.equals("")) {
            Toast.makeText(ServerActivity.this, "请输入二氧化碳值", Toast.LENGTH_SHORT).show();
        }
        if (co.equals("")) {
            Toast.makeText(ServerActivity.this, "请输入一氧化碳值", Toast.LENGTH_SHORT).show();
        }
        if (so2.equals("")) {
            Toast.makeText(ServerActivity.this, "请输入二氧化硫值", Toast.LENGTH_SHORT).show();
        }
        if (no.equals("")) {
            Toast.makeText(ServerActivity.this, "请输入氮氧化合物值", Toast.LENGTH_SHORT).show();
        }
        JSONObject json = new JSONObject();
        try {
            json.put("jiaquan", Double.valueOf(jiaquan).doubleValue());
            json.put("ben", Double.valueOf(ben).doubleValue());
            json.put("co2", Double.valueOf(co2).doubleValue());
            json.put("co", Double.valueOf(co).doubleValue());
            json.put("so2", Double.valueOf(so2).doubleValue());
            json.put("no", Double.valueOf(no).doubleValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String messagesend = json.toString();
        if (null == messagesend || messagesend.length() == 0) {
            return;
        }
        mBlthChatUtil.write(messagesend.getBytes());
    }

}
