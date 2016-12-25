package org.zoujie.zoubledevice.junior;

import android.os.Bundle;
import org.zoujie.zoubledevice.JuniorBaseActivity;
import org.zoujie.zoubledevice.R;

import java.util.HashMap;

/**
 * Created by benxer on 16/10/3.
 */
public class Demo2JuniorActivity extends JuniorBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeadColor("#b1bc39");
    }

    @Override
    protected int[] getBtnIds() {
        return new int[]{
                R.id.demo2_negative_btn,
                R.id.demo2_embossing_btn,
                R.id.demo2_grayscaler_btn,
                R.id.demo2_woodcarving_btn,
                R.id.demo2_4in1_btn,
                R.id.demo2_normal_btn,
        };
    }

    @Override
    protected String[] getCodes() {
        return new String[]{
                "<DM2Neg>",
                "<DM2Emb>",
                "<DM2Gys>",
                "<DM2Wdc>",
                "<DM24n1>",
                "<DM2Nma>",
        };
    }


    @Override
    protected int getBtnsLayoutId() {
        return R.layout.demo2_layout;
    }

    @Override
    protected String getHeadTitle() {
        return "HDMI graphics card";
    }
}
