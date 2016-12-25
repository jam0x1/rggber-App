package org.zoujie.zoubledevice.junior;

import android.os.Bundle;
import org.zoujie.zoubledevice.JuniorBaseActivity;
import org.zoujie.zoubledevice.R;

import java.util.HashMap;

/**
 * Created by benxer on 16/10/3.
 */
public class Demo4JuniorActivity extends JuniorBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeadColor("#dc5562");
    }

    @Override
    protected int[] getBtnIds() {
        return new int[]{
                R.id.demo4_video1_btn,
                R.id.demo4_video2_btn,
                R.id.demo4_2videos_btn,
        };
    }

    @Override
    protected String[] getCodes() {
        return new String[]{
                "<DM4Vd1>",
                "<DM4Vd2>",
                "<DM42Vd>",
        };
    }

    @Override
    protected int getBtnsLayoutId() {
        return R.layout.demo4_layout;
    }

    @Override
    protected String getHeadTitle() {
        return "ADAS";
    }
}
