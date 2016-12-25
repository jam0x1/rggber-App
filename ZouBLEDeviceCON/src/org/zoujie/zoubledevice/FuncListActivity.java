package org.zoujie.zoubledevice;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rfstar.kevin.app.App;
import org.w3c.dom.Text;
import org.zoujie.zoubledevice.junior.*;
import org.zoujie.zoubledevice.model.DateModel;

import java.util.Date;

/**
 * Created by benxer on 16/9/28.
 */
public class FuncListActivity extends Activity implements DateModel.RFStarBLEListener {
    //for app
    private final static int Func_Count = 5;
    //for developer
//    private final static int Func_Count = 6;
    private final static String[] Func_Titles = {
            "Demo1",
            "Demo2",
            "Demo3",
            "Demo4",
            "BL4.0",
            "Test",
    };
    private final static String[] Func_SubTitles = {
            "HD camera",
            "HDMI graphics card",
            "Image network",
            "ADAS",
            "BL4.0 data channel service",
            "Board Level Test",
    };
    private LinearLayout funcList;
    private final static Class[] Func_Classes = { Demo1JuniorActivity.class,
                                                  Demo2JuniorActivity.class,
                                                  Demo3JuniorActivity.class,
                                                  Demo4JuniorActivity.class,
                                                  Demo5JuniorActivity.class,
                                                  Demo6JuniorActivity.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.funclist_layout);
        DateModel.getDefault().addBLEListener(this);
        TextView search = (TextView) findViewById(R.id.ble_search);
        search.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG); //下划线
        search.getPaint().setAntiAlias(true);//抗锯齿
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToIntent(SearchActivity.class);
            }
        });
        funcList = (LinearLayout) findViewById(R.id.funclist);
        connect(DateModel.getDefault().isConnected(), DateModel.getDefault().getDeviceName());
    }

    @Override
    protected void onDestroy() {
        DateModel.getDefault().removeBLEListener(this);
//        App app = (App)getApplication();
//        if (app.manager.cubicBLEDevice != null && DateModel.getDefault().isConnected()) {
//            app.manager.cubicBLEDevice.disconnectedDevice();
//        }
        super.onDestroy();
    }

    private void connect(boolean connected, String deviceName) {
        TextView name = (TextView) findViewById(R.id.connect_name);
        if (connected) {
            name.setVisibility(View.VISIBLE);
            name.setText("connected " + deviceName);
        } else {
            name.setVisibility(View.GONE);
            name.setText(null);
        }
        funcList.removeAllViews();
        for (int i = 0; i < Func_Count; i++) {
            initFuncList(i);
        }
    }

    private void initFuncList(int index) {
        LinearLayout itemLayout = (LinearLayout) View.inflate(this, R.layout.funclist_item, null);
        if (index == 0) {
            itemLayout.setBackgroundResource(DateModel.getDefault().isConnected() ?
                    R.drawable.funclist_active_topitem : R.drawable.funclist_itemtop_graybg);
        } else
        if (index == Func_Count - 1) {
            itemLayout.setBackgroundResource(DateModel.getDefault().isConnected() ?
                    R.drawable.funclist_active_bottomitem : R.drawable.funclist_itembottom_graybg);
        } else {
            itemLayout.setBackgroundResource(DateModel.getDefault().isConnected() ?
                    R.drawable.funclist_active_item : R.drawable.funclist_item_graybg);
        }
        View itemFlag = (View) itemLayout.findViewById(R.id.itemflag);
        if (index % 2 == 0) {
            itemFlag.setBackgroundColor(Color.parseColor("#f56f6c"));
        } else {
            itemFlag.setBackgroundColor(Color.parseColor("#3fbd42"));
        }
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //for test
                if (DateModel.getDefault().isConnected()) {
                    goToIntent(Func_Classes[index]);
                }
            }
        });
        TextView titleText = (TextView) itemLayout.findViewById(R.id.title);
        TextView subTitleText = (TextView) itemLayout.findViewById(R.id.subtitle);
        titleText.setText(Func_Titles[index]);
        subTitleText.setText(Func_SubTitles[index]);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 1;
        funcList.addView(itemLayout, params);
    }

    private void goToIntent(Class className) {
        Intent intent = new Intent(this, className);
        startActivity(intent);
    }

    @Override
    public void onGattConnected(String name) {
        connect(true, name);
    }

    @Override
    public void onGattDisconnected() {
        connect(false, null);
    }

}
