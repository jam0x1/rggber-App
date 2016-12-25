package org.zoujie.zoubledevice;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.rfstar.kevin.app.App;
import org.zoujie.zoubledevice.model.DateModel;

import java.util.HashMap;

/**
 * Created by benxer on 16/10/3.
 */
public abstract class JuniorBaseActivity extends Activity {

    private final String TAG = JuniorBaseActivity.class.getSimpleName();
    protected App app;
    private TextView head;
    protected HashMap<Integer, String> codeSet = new HashMap<Integer, String>();
    private BtnClickedCallback btnClickedCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (App)getApplication();
        setContentView(R.layout.junior_layout);
        head = (TextView)findViewById(R.id.junior_title);
        LinearLayout layout = (LinearLayout)findViewById(R.id.junior_btns);
        ViewGroup viewGroup = (ViewGroup)View.inflate(this, getBtnsLayoutId(), null);
        layout.addView(viewGroup);
        setHeadTitle(getHeadTitle());
        if (null != getBtnIds()) {
            for (int i = 0; i < getBtnIds().length; i++) {
                setButtonClickListener(getBtnIds()[i]);
            }
            if (null != getCodes() && getCodes().length == getBtnIds().length) {
                for (int i = 0; i < getCodes().length; i ++) {
                    codeSet.put(new Integer(getBtnIds()[i]), getCodes()[i]);
                }
            }
        }
    }

    protected abstract int[] getBtnIds();

    protected abstract String[] getCodes();

    protected abstract int getBtnsLayoutId();

    protected abstract String getHeadTitle();

    public final void sendMsg(String flag) {
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

    public final void setButtonClickListener(int btnResId) {
        findViewById(btnResId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DateModel.getDefault().isConnected()) {
                    Integer key = new Integer(btnResId);
                    if (null == codeSet || !codeSet.containsKey(key)) {
                        return;
                    }
                    String code = codeSet.get(new Integer(btnResId));
                    if (TextUtils.isEmpty(code)) {
                        return;
                    }
                    if (null != btnClickedCallback) {
                        btnClickedCallback.onBtnClicked(view.getId());
                    }
                    sendMsg(code);
                } else {
                    Toast.makeText(JuniorBaseActivity.this, "BLE disconnected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public final void setHeadTitle(String text) {
        head.setText(text);
    }

    public final void setHeadColor(String color) {
        head.setBackgroundColor(Color.parseColor(color));
    }

    public void setBtnClickedCallback(BtnClickedCallback btnClickedCallback) {
        this.btnClickedCallback = btnClickedCallback;
    }

    public interface BtnClickedCallback {

        void onBtnClicked(int btnId);

    }
}
