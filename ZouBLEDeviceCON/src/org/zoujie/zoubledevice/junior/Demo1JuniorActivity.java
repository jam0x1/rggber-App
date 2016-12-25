package org.zoujie.zoubledevice.junior;

import android.os.Bundle;
import org.zoujie.zoubledevice.JuniorBaseActivity;
import org.zoujie.zoubledevice.R;

import java.util.HashMap;

/**
 * Created by benxer on 16/10/3.
 */
public class Demo1JuniorActivity extends JuniorBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeadColor("#dc5562");
    }

    @Override
    protected int[] getBtnIds() {
        return new int[]{
                R.id.demo1_af_btn,
                R.id.demo1_releasefocus_btn,
        };
    }

    @Override
    protected String[] getCodes() {
        return new String[]{
                "<DM1AF>",
                "<DM1RFu>",
        };
    }

    @Override
    protected int getBtnsLayoutId() {
        return R.layout.demo1_layout;
    }

    @Override
    protected String getHeadTitle() {
        return "HD camera";
    }
}
