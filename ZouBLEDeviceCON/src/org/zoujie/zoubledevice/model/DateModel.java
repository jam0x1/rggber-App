package org.zoujie.zoubledevice.model;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.rfstar.kevin.app.App;
import com.rfstar.kevin.params.BLEDevice;
import com.rfstar.kevin.service.RFStarBLEService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;

/**
 * Created by benxer on 16/9/29.
 */
public class DateModel {

    private final String TAG = DateModel.class.getSimpleName();

    private boolean isConnected;
    private BluetoothDevice currBle;
    private HashMap<String, BluetoothDevice> bleSet = new HashMap<String, BluetoothDevice>();

    private List<RFStarBLEListener> listeners = new ArrayList<RFStarBLEListener>();
    private OnBLEConnectedListener connectedListener;
    private OnBLEReceiveListener receiveListener;

    private App app;

    private static DateModel instance;

    private BLEDevice.RFStarBLEBroadcastReceiver delegate = new BLEDevice.RFStarBLEBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent, String macData, String uuid) {
            String action = intent.getAction();
            if (RFStarBLEService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG, "Connect BLE Device " + macData);
                if (null != connectedListener) {
                    connectedListener.onConnected(macData);
                }
                dealBleDevice(true, macData);
            } else if (RFStarBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG, "Disconnect BLE Device " + macData);
                dealBleDevice(false, macData);
            } else if (RFStarBLEService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                Log.d(TAG, "Service discovered " + macData);
            } else if (RFStarBLEService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d(TAG, "ACTION_DATA_AVAILABLE uuid: " + uuid);
                if (uuid.contains("ffe4")) {
                    byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
                    for (int i = 0; i < data.length; i++) {
                        Log.d(TAG, "data[" + i + "]:" + data[i]);
                    }
                    try {
                        String revData = new String(data, "GB2312");
                        Log.d(TAG, "rev data: " + revData);
                        if (null != receiveListener) {
                            receiveListener.onReceive(revData);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private DateModel() {

    }

    public static DateModel getDefault() {
        if (null == instance) {
            synchronized (DateModel.class) {
                if (null == instance) {
                    instance = new DateModel();
                }
            }
        }
        return instance;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public void setConnectedListener(OnBLEConnectedListener connectedListener) {
        this.connectedListener = connectedListener;
    }

    public void setReceiveListener(OnBLEReceiveListener receiveListener) {
        this.receiveListener = receiveListener;
    }

    public void connectBleDevice(BluetoothDevice bleDevice, String address) {
        currBle = bleDevice;
        bleSet.put(address, bleDevice);
        Log.d(TAG, "add " + address + " ble device");
    }

    public BluetoothDevice getCurrBleDevice() {
        return currBle;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getDeviceName() {
        if (null == getCurrBleDevice()) {
            return "";
        }
        return TextUtils.isEmpty(getCurrBleDevice().getName()) ?
                getCurrBleDevice().getAddress() : getCurrBleDevice().getName();
    }

    public BLEDevice.RFStarBLEBroadcastReceiver getDelegate() {
        return delegate;
    }

    public void addBLEListener(RFStarBLEListener listener) {
        listeners.add(listener);
    }

    public void removeBLEListener(RFStarBLEListener listener) {
        listeners.remove(listener);
    }

    public void clearBLEListener() {
        listeners.clear();
    }

    private void dealBleDevice(boolean connect, String address) {
        if (connect) {
            isConnected = true;
            for (RFStarBLEListener listener : listeners) {
                listener.onGattConnected(getDeviceName());
            }
            return;
        }
        bleSet.remove(address);
        if (bleSet.size() == 0) {
            isConnected = false;
            for (RFStarBLEListener listener : listeners) {
                listener.onGattDisconnected();
            }
        }
    }

    public void setReceive(boolean flag) {
        app.manager.cubicBLEDevice.setNotification("ffe0", "ffe4", flag);
    }

    public interface RFStarBLEListener {

        public void onGattConnected(String name);

        public void onGattDisconnected();

    }

    public interface OnBLEConnectedListener {

        public void onConnected(String address);

    }

    public interface OnBLEReceiveListener {

        public void onReceive(String revData);

    }
}
