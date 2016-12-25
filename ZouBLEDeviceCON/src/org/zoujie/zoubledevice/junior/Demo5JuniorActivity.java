package org.zoujie.zoubledevice.junior;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.zoujie.zoubledevice.JuniorBaseActivity;
import org.zoujie.zoubledevice.R;
import org.zoujie.zoubledevice.model.DateModel;

import java.util.HashMap;

/**
 * Created by benxer on 16/10/3.
 */
public class Demo5JuniorActivity extends JuniorBaseActivity implements DateModel.OnBLEReceiveListener {

    private TextView receiveCodeText, sendTimeText, revTimeText;
    private DateModel dateModel = DateModel.getDefault();
    private StringBuffer revSb = new StringBuffer();
    private int sendTime, revTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendTime = revTime = 0;
        sendTimeText = (TextView)findViewById(R.id.demo5_sendtime_text);
        revTimeText = (TextView)findViewById(R.id.demo5_revtime_text);
        EditText codeEdit = (EditText)findViewById(R.id.demo5_code_edit);
        //设置codeEdit可滚动
        codeEdit.setMovementMethod(ScrollingMovementMethod.getInstance());
        findViewById(R.id.demo5_codeclear_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeEdit.setText(null);
            }
        });
        findViewById(R.id.demo5_codesend_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(codeEdit.getText().toString())) {
                    Toast.makeText(Demo5JuniorActivity.this, "Please input codes!", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendTime++;
                if (sendTime >= Integer.MAX_VALUE) {
                    sendTime = 0;
                }
                setTimeText(sendTimeText, sendTime);
                sendMsg(codeEdit.getText().toString());
            }
        });
        receiveCodeText = (TextView)findViewById(R.id.demo5_receivecode_text);
        //设置receiveCodeText可滚动
        receiveCodeText.setMovementMethod(ScrollingMovementMethod.getInstance());
        findViewById(R.id.demo5_receiveclear_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                synchronized (Demo5JuniorActivity.class) {
                    revSb = new StringBuffer();
                    receiveCodeText.setText(null);
                }
            }
        });
        setTimeText(sendTimeText, sendTime);
        setTimeText(revTimeText, revTime);
        dateModel.setReceive(true);
        dateModel.setReceiveListener(this);
        //for text
//        receiveCodeText.setText("asdasdasdasdasdasdasdasdasdas" +
//                "asdasdasdasdasdasdasdasdasdadasdasdasdasdasdas" +
//                "adasdasdasdasdadasdasdadasdasdadadadadasdadasda" +
//                "adasdasdadasdadadadasdddddddddddddddddddddddddd" +
//                "asdaaaaaaaasdasdadasdadadasdasdasdadasdadasdadda" +
//                "asdasdasdasdasdadadadasdadadadadasdadasdasdadada" +
//                "asdadasdaddasdasdadasdadasdadadasaddaasdadadaada");
    }

    @Override
    protected int[] getBtnIds() {
        return new int[0];
    }

    @Override
    protected String[] getCodes() {
        return new String[0];
    }
    @Override
    protected int getBtnsLayoutId() {
        return R.layout.demo5_layout;
    }

    @Override
    protected String getHeadTitle() {
        return "BL4.0 data channel service";
    }

    @Override
    public void onReceive(String revData) {
        synchronized (Demo5JuniorActivity.class) {
            revTime++;
            if (revTime >= Integer.MAX_VALUE) {
                revTime = 0;
            }
            setTimeText(revTimeText, revTime);
            revSb.append(revData);
            receiveCodeText.setText(revSb.toString());
        }
    }

    @Override
    protected void onDestroy() {
        dateModel.setReceive(false);
        dateModel.setReceiveListener(null);
        super.onDestroy();
    }

    private void setTimeText(TextView textView, int timeCount) {
        textView.setText(String.format("Time:%d", timeCount));
    }
}
