package org.zoujie.zoubledevice.junior;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.zoujie.zoubledevice.JuniorBaseActivity;
import org.zoujie.zoubledevice.R;
import org.zoujie.zoubledevice.model.DateModel;

/**
 * Created by benxer on 2016/12/11.
 */
public class Demo6JuniorActivity extends JuniorBaseActivity implements DateModel.OnBLEReceiveListener,
        JuniorBaseActivity.BtnClickedCallback {

    private DateModel dateModel = DateModel.getDefault();
    private TextView revChkTxText, revChkOvText, revWedidText, revChkBaText;

    private final static String Rev_ChkTx_Prefix = "(CHKTX";
    private final static String Rev_ChkOv_Prefix = "(CHKOV";
    private final static String Rev_Wedid_Prefix = "(WEDID";
    private final static String Rev_ChkBa_Prefix = "(CHKBA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBtnClickedCallback(this);
        revChkTxText = (TextView)findViewById(R.id.demo6_revhdmi_text);
        revChkOvText = (TextView)findViewById(R.id.demo6_revov5640_text);
        revWedidText = (TextView)findViewById(R.id.demo6_rev1080p_text);
        revChkBaText = (TextView)findViewById(R.id.demo6_revbattery_text);
        dateModel.setReceive(true);
        dateModel.setReceiveListener(this);
    }

    @Override
    protected int[] getBtnIds() {
        return new int[] {
                R.id.demo6_s1_btn,
                R.id.demo6_s2_btn,
                R.id.demo6_led1_btn,
                R.id.demo6_led2_btn,
                R.id.demo6_led3_btn,
                R.id.demo6_led4_btn,
                R.id.demo6_checkhdmi_btn,
                R.id.demo6_checkov5640_btn,
                R.id.demo6_1080p_btn,
                R.id.demo6_checkbattery_btn,
        };
    }

    @Override
    protected String[] getCodes() {
        return new String[] {
                "<INT00>",
                "<INT10>",
                "<LED1>",
                "<LED2>",
                "<LED3>",
                "<LED4>",
                "<CHKTX>",
                "<CHKOV>",
                "<WEDID>",
                "<CHKBA>",
        };
    }

    @Override
    protected int getBtnsLayoutId() {
        return R.layout.demo6_layout;
    }

    @Override
    protected String getHeadTitle() {
        return "Board Level Test";
    }

    @Override
    public void onReceive(String revData) {
        if ("(INT01)".equals(revData)) {
            setButtonEnable(R.id.demo6_s1_btn, true, R.drawable.junior_active_bluebtn);
        } else
        if ("(INT11)".equals(revData)) {
            setButtonEnable(R.id.demo6_s2_btn, true, R.drawable.junior_active_bluebtn);
        } else
        if (!TextUtils.isEmpty(revData) && revData.contains(Rev_ChkTx_Prefix)) {
            setRevText(R.id.demo6_revhdmi_text, revData);
        } else
        if (!TextUtils.isEmpty(revData) && revData.contains(Rev_ChkOv_Prefix)) {
            setRevText(R.id.demo6_revov5640_text, revData);
        } else
        if (!TextUtils.isEmpty(revData) && revData.contains(Rev_Wedid_Prefix)) {
            setRevText(R.id.demo6_rev1080p_text, revData);
        } else
        if (!TextUtils.isEmpty(revData) && revData.contains(Rev_ChkBa_Prefix)) {
            setRevText(R.id.demo6_revbattery_text, revData);
        }
    }

    @Override
    public void onBtnClicked(int btnId) {
        if (btnId == R.id.demo6_s1_btn || btnId == R.id.demo6_s2_btn) {
            setButtonEnable(btnId, false, R.drawable.junior_button_graybg);
        } else
        if (btnId == R.id.demo6_checkhdmi_btn) {
            clearText(R.id.demo6_revhdmi_text);
        } else
        if (btnId == R.id.demo6_checkov5640_btn) {
            clearText(R.id.demo6_revov5640_text);
        } else
        if (btnId == R.id.demo6_1080p_btn) {
            clearText(R.id.demo6_rev1080p_text);
        } else
        if (btnId == R.id.demo6_checkbattery_btn) {
            clearText(R.id.demo6_revbattery_text);
        }
    }

    @Override
    protected void onDestroy() {
        dateModel.setReceive(false);
        dateModel.setReceiveListener(null);
        super.onDestroy();
    }

    private void setButtonEnable(int btnId, boolean enable, int resId) {
        Button button = (Button)findViewById(btnId);
        button.setEnabled(enable);
        button.setBackgroundResource(resId);
    }

    private void clearText(int textId) {
        TextView textView = (TextView)findViewById(textId);
        textView.setText(null);
    }

    private void setRevText(int textId, String text) {
        TextView textView = (TextView)findViewById(textId);
        textView.setText(text);
    }


}
