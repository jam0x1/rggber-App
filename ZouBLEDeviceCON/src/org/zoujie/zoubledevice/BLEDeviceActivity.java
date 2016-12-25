package org.zoujie.zoubledevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rfstar.kevin.app.App;
import com.rfstar.kevin.app.AppManager.RFStarManageListener;
import com.rfstar.kevin.params.BLEDevice.RFStarBLEBroadcastReceiver;
import com.rfstar.kevin.params.CubicBLEDevice;
import com.rfstar.kevin.service.RFStarBLEService;

public class BLEDeviceActivity extends Activity implements OnClickListener {

	private final String TAG = BLEDeviceActivity.class.getSimpleName();
	private View statView;
	private TextView respText;
	
	private App app;
	private ProgressDialog dialog;
	private ArrayList<BluetoothDevice> arraySource;
	
	private boolean isConnected;
	private boolean isScaning;
	
	private RFStarManageListener starMgrListener = new RFStarManageListener() {
		
		@Override
		public void RFstarBLEManageStopScan() {
			Log.d(TAG, "stop scan");
			isScaning = false;
			dialog.hide();
			if (arraySource.size() == 0) {
				Toast.makeText(BLEDeviceActivity.this, "Can't find BLE device.", Toast.LENGTH_LONG).show();
				return;
			}
			openBLEDeviceListDialog();
		}
		
		@Override
		public void RFstarBLEManageStartScan() {
			Log.d(TAG, "start scan");
        	arraySource.clear();
        	isScaning = true;
			dialog.setMessage("Searching...");
        	dialog.show();
		}
		
		@Override
		public void RFstarBLEManageListener(BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			Log.d(TAG, "scanrecord : " + device.getAddress() + ", " + device.getName());
			arraySource.add(device);
		}
	};
	
	private RFStarBLEBroadcastReceiver delegate = new RFStarBLEBroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent, String macData,
				String uuid) {
			String action = intent.getAction();
			if (RFStarBLEService.ACTION_GATT_CONNECTED.equals(action)) {
				Log.d(TAG, "Connect BLE Device");
				dialog.hide();
				connectDevice(true);
			} else if (RFStarBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Log.d(TAG, "Disconnect BLE Device");
				dialog.hide();
				connectDevice(false);
			} else if (RFStarBLEService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				Log.d(TAG, "Service discovered");
//				dialog.hide();
//				connectDevice(false);
			}
		
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (App)getApplication();
        app.manager.setRFstarBLEManagerListener(starMgrListener);
        arraySource = new ArrayList<BluetoothDevice>();

		dialog = new ProgressDialog(this);
		dialog.setMessage("Searching...");
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
        
        setContentView(R.layout.activity_bledevice);
        
        statView = findViewById(R.id.deviceStat);
        respText = (TextView)findViewById(R.id.deviceResp);
        respText.setText("none");
        
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
        findViewById(R.id.button8).setOnClickListener(this);
        findViewById(R.id.button9).setOnClickListener(this);
        findViewById(R.id.button0).setOnClickListener(this);
        findViewById(R.id.buttonPlus).setOnClickListener(this);
        findViewById(R.id.buttonSub).setOnClickListener(this);
        findViewById(R.id.buttonStar).setOnClickListener(this);
        findViewById(R.id.buttonWell).setOnClickListener(this);
        findViewById(R.id.buttonUnknow).setOnClickListener(this);
        
		app.manager.isEdnabled(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bledevice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	if (isConnected) {
				Toast.makeText(this, "You have connected the device.", Toast.LENGTH_LONG).show();
				return true;
			}
        	app.manager.startScanBluetoothDevice();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isConnected) {
				app.manager.cubicBLEDevice.disconnectedDevice();
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View arg0) {
		if (!isConnected) {
			Toast.makeText(this, "Please connect the BLE device.", Toast.LENGTH_SHORT).show();
			return;
		}
		if (arg0 instanceof Button) {
			String flag = ((Button)arg0).getText().toString();
			if ("？".equals(flag)) {
				flag = "?";
			}
			sendMsg(flag);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		super.onActivityResult(requestCode, resultCode, data);
		app.manager.onRequestResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (isScaning) {
			app.manager.stopScanBluetoothDevice();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dialog.dismiss();
		if (app.manager.cubicBLEDevice != null && isConnected) {
			app.manager.cubicBLEDevice.disconnectedDevice();
			app.manager.cubicBLEDevice = null;
		}
	}
	
	private void sendMsg(String flag) {
		if (TextUtils.isEmpty(flag)) {
			return;
		}
		byte[] valueData = flag.getBytes();
		byte[] data = new byte[valueData.length + 2];
		data[0] = 0x55;
		data[data.length - 1] = (byte)0xaa;
		for (int i = 0; i < valueData.length; i++) {
			data[i + 1] = valueData[i];
		}
		app.manager.cubicBLEDevice.writeValue("ffe5", "ffe9", data);
	}
	
	private void connectDevice
			(boolean connected) {
		isConnected = connected;
		statView.setBackgroundResource(isConnected ? 
				android.R.color.holo_green_light : android.R.color.holo_red_dark);
	}

	private void openBLEDeviceListDialog() {
		LinearLayout linearLayoutMain = new LinearLayout(this);//自定义一个布局文件
		linearLayoutMain.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ListView listView = new ListView(this);//this为获取当前的上下文
		listView.setFadingEdgeLength(0);

		List<Map<String, String>> nameList = new ArrayList<Map<String, String>>();//建立一个数组存储listview上显示的数据
		for (int m = 0; m < arraySource.size(); m++) {//initData为一个list类型的数据源
			Map<String, String> nameMap = new HashMap<String, String>();
			nameMap.put("name", arraySource.get(m).getName());
			nameList.add(nameMap);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this,
				nameList, R.layout.list_layout,
				new String[] { "name" },
				new int[] { R.id.bleDeviceName });
		listView.setAdapter(adapter);

		linearLayoutMain.addView(listView);//往这个布局中加入listview

		final AlertDialog listDialog = new AlertDialog.Builder(this)
				.setTitle("Select BLE Device").setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).create();
		listDialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
		listDialog.show();
		listView.setOnItemClickListener(new OnItemClickListener() {//响应listview中的item的点击事件

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// select ble device
				dialog.setMessage("Connecting...");
				dialog.show();
				app.manager.bluetoothDevice = arraySource.get(arg2);
				app.manager.cubicBLEDevice = new CubicBLEDevice(getApplicationContext(), app.manager.bluetoothDevice);
				app.manager.cubicBLEDevice.setBLEBroadcastDelegate(delegate);
				listDialog.cancel();
			}
		});
	
	}

}
