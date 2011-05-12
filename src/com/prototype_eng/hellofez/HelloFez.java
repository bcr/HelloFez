package com.prototype_eng.hellofez;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

class FezController
{
    private FileDescriptor fileDescriptor;
    private FileInputStream inputStream;
    private FileOutputStream outputStream;

    public FezController(UsbManager usbManager, UsbAccessory accessory)
    {
        fileDescriptor = usbManager.openAccessory(accessory).getFileDescriptor();
        inputStream = new FileInputStream(fileDescriptor);
        outputStream = new FileOutputStream(fileDescriptor);
    }

    public int ledOn() throws IOException
    {
        return sendCommand(1);
    }
    
    public int ledOff() throws IOException
    {
        return sendCommand(2);
    }

    public boolean isButtonPressed() throws IOException
    {
        return (sendCommand(3) & 0x80) != 0;
    }

    private int sendCommand(int command) throws IOException
    {
        synchronized (outputStream)
        {
            outputStream.write(command);
            return inputStream.read();
        }
    }
}

public class HelloFez extends Activity {
    private static final String TAG = "HelloFez";
    private FezController fezController;
    private CheckBox ledcheck;
    private CheckBox buttoncheck;
    private Thread buttonthread;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");

        maybeCreateFezController();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ledcheck = (CheckBox) findViewById(R.id.ledcheck);
        buttoncheck = (CheckBox) findViewById(R.id.buttoncheck);

        if (fezController != null)
        {
            ledcheck.setOnCheckedChangeListener(new OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked)
                    {
                        Log.v(TAG, "OnCheckedChanged " + isChecked);
                        try
                        {
                            if (isChecked)
                            {
                                fezController.ledOn();
                            }
                            else
                            {
                                fezController.ledOff();
                            }
                        }
                        catch (IOException e)
                        {
                            Log.v(TAG, "Unexpected exception", e);
                        }
                    }
                });
            buttonthread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        while (true)
                        {
                            try
                            {
                                Thread.sleep(500);
                                buttoncheck.post(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            boolean newButtonState = false;
                                            try
                                            {
                                                newButtonState = fezController.isButtonPressed();
                                            }
                                            catch (IOException e)
                                            {
                                                Log.v(TAG, "Unexpected exception", e);
                                            }
                                            if (newButtonState != buttoncheck.isChecked())
                                            {
                                                buttoncheck.setChecked(newButtonState);
                                            }
                                        }
                                    });
                            }
                            catch (InterruptedException e)
                            {
                                Log.v(TAG, "Unexpected exception", e);
                                break;
                            }
                        }
                    }
                });
            buttonthread.start();
        }
    }

    private void maybeCreateFezController()
    {
        UsbAccessory accessory = UsbManager.getAccessory(getIntent());
        Log.v(TAG, (accessory != null) ? "accessory! I haz it!" : "No accessory for you!");
        UsbManager usbManager = UsbManager.getInstance(this);
        Log.v(TAG, (usbManager != null) ? "manager! I haz it!" : "No manager for you!");
        if ((accessory != null) && (usbManager != null))
        {
            Log.v(TAG, "Got an accessory and a manager");
            fezController = new FezController(usbManager, accessory);
            Log.v(TAG, "Controller constructed");
        }
    }
}