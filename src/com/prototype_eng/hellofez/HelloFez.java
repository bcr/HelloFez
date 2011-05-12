package com.prototype_eng.hellofez;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

public class HelloFez extends Activity {
    private static final String TAG = "HelloFez";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");

        UsbAccessory accessory = UsbManager.getAccessory(getIntent());
        Log.v(TAG, (accessory != null) ? "accessory! I haz it!" : "No accessory for you!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}