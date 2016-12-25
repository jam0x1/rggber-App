package org.zoujie.zoubledevice.junior;

import android.os.Bundle;
import org.zoujie.zoubledevice.JuniorBaseActivity;
import org.zoujie.zoubledevice.R;

import java.util.HashMap;

/**
 * Created by benxer on 16/10/3.
 */
public class Demo3JuniorActivity extends JuniorBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeadColor("#398de3");
    }

    @Override
    protected int[] getBtnIds() {
        return new int[]{
                R.id.demo3_video1_btn,
                R.id.demo3_video2_btn,
                R.id.demo3_video3_btn,
                R.id.demo3_video4_btn,
                R.id.demo3_allvideos_btn,
        };
    }

    @Override
    protected String[] getCodes() {
        return new String[]{
                "<DM3Vd1>",
                "<DM3Vd2>",
                "<DM3Vd3>",
                "<DM3Vd4>",
                "<DM3ALV>",
        };
    }


    @Override
    protected int getBtnsLayoutId() {
        return R.layout.demo3_layout;
    }

    @Override
    protected String getHeadTitle() {
        return "Image network";
    }
}
