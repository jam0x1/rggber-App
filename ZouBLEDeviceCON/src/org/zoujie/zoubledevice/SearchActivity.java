package org.zoujie.zoubledevice;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.rfstar.kevin.app.App;
import com.rfstar.kevin.app.AppManager;
import com.rfstar.kevin.params.BLEDevice;
import com.rfstar.kevin.params.CubicBLEDevice;
import com.rfstar.kevin.service.RFStarBLEService;
import org.w3c.dom.Text;
import org.zoujie.zoubledevice.model.DateModel;

import java.util.ArrayList;

/**
 * Created by benxer on 16/9/28.
 */
public class SearchActivity extends Activity implements DateModel.RFStarBLEListener {

    private final String TAG = SearchActivity.class.getSimpleName();

    private App app;
    private ArrayList<BluetoothDevice> arraySource;

    private boolean isConnecting;
    private boolean isScaning;
    private int selectIndex;

    private LinearLayout resultList;
    private ProgressBar progressBar;
    private TextView result;
    private RelativeLayout[] bleItems;

    private AppManager.RFStarManageListener starMgrListener = new AppManager.RFStarManageListener() {

        @Override
        public void RFstarBLEManageStopScan() {
            Log.d(TAG, "stop scan");
            searchProgress(false);
            openResult();
        }

        @Override
        public void RFstarBLEManageStartScan() {
            Log.d(TAG, "start scan");
            arraySource.clear();
            searchProgress(true);
        }

        @Override
        public void RFstarBLEManageListener(BluetoothDevice device, int rssi,
                                            byte[] scanRecord) {
            Log.d(TAG, "scanrecord : " + device.getAddress() + ", " + device.getName());
            arraySource.add(device);
        }
    };

    private DateModel.OnBLEConnectedListener connectedListener = new DateModel.OnBLEConnectedListener() {
        @Override
        public void onConnected(String address) {
            DateModel.getDefault().connectBleDevice(app.manager.bluetoothDevice, address);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        app = (App)getApplication();
        app.manager.setRFstarBLEManagerListener(starMgrListener);
        arraySource = new ArrayList<BluetoothDevice>();
        DateModel.getDefault().setConnectedListener(connectedListener);

        resultList = (LinearLayout) findViewById(R.id.ble_list);
        progressBar = (ProgressBar) findViewById(R.id.search_progressbar);
        result = (TextView) findViewById(R.id.search_result);
        TextView refresh = (TextView) findViewById(R.id.search_refresh);
        refresh.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG); //下划线
        refresh.getPaint().setAntiAlias(true);//抗锯齿
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isScaning && !isConnecting) {
                    findViewById(R.id.search_connectname).setVisibility(View.GONE);
                    app.manager.startScanBluetoothDevice();
                }
            }
        });

        app.manager.isEdnabled(this);
        app.manager.startScanBluetoothDevice();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DateModel.getDefault().addBLEListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DateModel.getDefault().removeBLEListener(this);
        if (isScaning) {
            app.manager.stopScanBluetoothDevice();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        super.onActivityResult(requestCode, resultCode, data);
        app.manager.onRequestResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isScaning) {
                Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (isConnecting) {
                Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void searchProgress(boolean search) {
        isScaning = search;
        resultList.setVisibility(search ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(search ? View.VISIBLE : View.GONE);
        result.setVisibility(search ? View.GONE : View.VISIBLE);
    }

    private void openResult() {
        selectIndex = -1;
        resultList.removeAllViews();
        if (arraySource.size() == 0) {
            TextView noDeviceInfo = new TextView(this);
            noDeviceInfo.setText("Can't find BLE device");
            noDeviceInfo.setTextSize(16);
            noDeviceInfo.setTextColor(Color.WHITE);
            noDeviceInfo.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
            resultList.addView(noDeviceInfo, params);
            return;
        }
        bleItems = new RelativeLayout[arraySource.size()];
        for (int i = 0; i < arraySource.size(); i++) {
            RelativeLayout bleItem = (RelativeLayout) View.inflate(this, R.layout.search_item, null);
            bleItem.setId(i);
            TextView bleName = (TextView) bleItem.findViewById(R.id.item_name);
            if (TextUtils.isEmpty(arraySource.get(i).getName())) {
                bleName.setText(arraySource.get(i).getAddress());
            } else {
                bleName.setText(arraySource.get(i).getName());
            }
            bleItems[i] = bleItem;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 1;
            resultList.addView(bleItem, params);
            bleItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConnecting) {
                        return;
                    }
                    if (isConnectedDevice(view.getId())) {
                        return;
                    }
                    selectIndex = view.getId();
                    app.manager.bluetoothDevice = arraySource.get(selectIndex);
                    app.manager.cubicBLEDevice = new CubicBLEDevice(getApplicationContext(), app.manager.bluetoothDevice);
                    app.manager.cubicBLEDevice.setBLEBroadcastDelegate(DateModel.getDefault().getDelegate());
                    showItemProgressbar(true);
                }
            });
        }
        if (DateModel.getDefault().isConnected()) {
            connect(true, DateModel.getDefault().getDeviceName());
        }
    }

    private void connect(boolean connected, String deviceName) {
        TextView name = (TextView) findViewById(R.id.search_connectname);
        if (connected) {
            name.setVisibility(View.VISIBLE);
            name.setText("connected " + deviceName);
        } else {
            name.setVisibility(View.GONE);
            name.setText(null);
        }
        for (int i = 0; i < arraySource.size(); i++) {
            RelativeLayout item = bleItems[i];
            if (isConnectedDevice(i)) {
                item.setBackgroundResource(R.drawable.search_item_graybg);
            } else {
                item.setBackgroundResource(R.drawable.search_active_item);
            }
        }
    }

    private void showItemProgressbar(boolean visible) {
        if (selectIndex < 0) {
            return;
        }
        isConnecting = visible;
        ProgressBar itemProgressbar = (ProgressBar)bleItems[selectIndex].findViewById(R.id.item_progressbar);
        itemProgressbar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private boolean isConnectProgressbar() {
        if (selectIndex < 0) {
            return false;
        }
        ProgressBar itemProgressbar = (ProgressBar)bleItems[selectIndex].findViewById(R.id.item_progressbar);
        return itemProgressbar.getVisibility() == View.VISIBLE;
    }

    private boolean isConnectedDevice(int index) {
        if (!DateModel.getDefault().isConnected()) {
            return false;
        }
        BluetoothDevice bleDevice = arraySource.get(index);
        if (DateModel.getDefault().getDeviceName().equals(bleDevice.getName()) ||
                DateModel.getDefault().getDeviceName().equals(bleDevice.getAddress())) {
            return true;
        }
        return false;
    }

    @Override
    public void onGattConnected(String name) {
        if (isScaning) {
            return;
        }
        showItemProgressbar(false);
        connect(true, name);
        finish();
    }

    @Override
    public void onGattDisconnected() {
        if (isScaning) {
            return;
        }
        showItemProgressbar(false);
        connect(false, null);
    }

}
