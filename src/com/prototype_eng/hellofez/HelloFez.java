package com.prototype_eng.hellofez;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

class FezRunnable implements Runnable
{
    private FileDescriptor fileDescriptor;
    private FileInputStream inputStream;
    private FileOutputStream outputStream;

    public FezRunnable(UsbManager usbManager, UsbAccessory accessory)
    {
        fileDescriptor = usbManager.openAccessory(accessory).getFileDescriptor();
        inputStream = new FileInputStream(fileDescriptor);
        outputStream = new FileOutputStream(fileDescriptor);
    }

    @Override
    public void run()
    {
    }

    public int ledOn() throws IOException
    {
        return sendCommand(1);
    }
    
    public int ledOff() throws IOException
    {
        return sendCommand(2);
    }

    private int sendCommand(int command) throws IOException
    {
        outputStream.write(command);
        return inputStream.read();
    }
}

public class HelloFez extends Activity {
    private static final String TAG = "HelloFez";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");

        UsbAccessory accessory = UsbManager.getAccessory(getIntent());
        Log.v(TAG, (accessory != null) ? "accessory! I haz it!" : "No accessory for you!");
        UsbManager usbManager = UsbManager.getInstance(this);
        Log.v(TAG, (usbManager != null) ? "manager! I haz it!" : "No manager for you!");
        if ((accessory != null) && (usbManager != null))
        {
            Log.v(TAG, "Got an accessory and a manager");
            FezRunnable runnable = new FezRunnable(usbManager, accessory);
            Log.v(TAG, "Runnable constructed");
            try
            {
                Log.v(TAG, "ledOn returned " + runnable.ledOn());
                Log.v(TAG, "ledOff returned " + runnable.ledOff());
            }
            catch (IOException e)
            {
                Log.v(TAG, "Unexpected exception", e);
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}