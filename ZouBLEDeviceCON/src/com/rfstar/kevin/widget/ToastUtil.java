package com.rfstar.kevin.widget;

import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

	public ToastUtil() {
		
	}
	
	public static void makeText(Context context, String text, int duration) {
		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static void makeTextTop(Context context, String text, int duration) {
		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.TOP, 0, 0);
		toast.show();
	}

}
