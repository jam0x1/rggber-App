package org.zoujie.zoubledevice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.rfstar.kevin.app.App;
import com.rfstar.kevin.params.BLEDevice;
import org.zoujie.zoubledevice.model.DateModel;
import org.zoujie.zoubledevice.widget.WidgetUtil;

/**
 * Created by benxer on 16/9/28.
 */
public class SplashActivity extends Activity {

    private Handler handler;
    private boolean animationFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DateModel.getDefault().setApp((App)getApplication());
        setContentView(R.layout.splash_layout);
        findViewById(R.id.splash_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (animationFlag) {
                    goToMain();
                }
            }
        });
        handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animation();
            }
        }, 1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void animation() {
        ImageView logoImg = (ImageView) findViewById(R.id.logoImg);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)logoImg.getLayoutParams();
        float logoImgY = WidgetUtil.getScreenHeight(this) - logoImg.getHeight() - params.bottomMargin;
        float deltaY = WidgetUtil.getScreenHeight(this) / 3 - logoImgY;
//        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(0f, 0f, 0f, deltaY);
//        animationSet.addAnimation(translateAnimation);
        translateAnimation.setDuration(1000);
        translateAnimation.setFillAfter(true);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                float layoutY = WidgetUtil.getScreenHeight(SplashActivity.this) - WidgetUtil.dip2px(SplashActivity.this, 60);
                float toDeltaY = WidgetUtil.getScreenHeight(SplashActivity.this) / 3 + logoImg.getHeight() + 20 - layoutY;
                moveTextView(R.id.textLayout, toDeltaY, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        goToMain();
                        animationFlag = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        logoImg.startAnimation(translateAnimation);
    }

    private void moveTextView(int textId, float toDeltaY, Animation.AnimationListener listener) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0f, 0f, 0f, toDeltaY);
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        if (null != listener) {
            translateAnimation.setAnimationListener(listener);
        }
        findViewById(textId).setVisibility(View.VISIBLE);
        findViewById(textId).startAnimation(translateAnimation);
    }

    private void goToMain() {
        Intent intent = new Intent(SplashActivity.this, FuncListActivity.class);
        SplashActivity.this.startActivity(intent);
    }
}
