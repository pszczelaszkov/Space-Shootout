package com.pszczelaszkov.spaceShootout;
 
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.os.PowerManager;
import android.os.Bundle;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.jme3.app.AndroidHarness;
import com.jme3.system.AppSettings;
import com.jme3.system.android.AndroidConfigChooser.ConfigType;
import com.pszczelaszkov.spaceShootout.R;
import java.util.logging.Level;
import java.util.logging.LogManager;
import spaceShootout.AndroidBridge;
import spaceShootout.Main;

public class MainActivity extends AndroidHarness implements SensorEventListener, AndroidBridge
{    
 
    /*
     * Note that you can ignore the errors displayed in this file,
     * the android project will build regardless.
     * Install the 'Android' plugin under Tools->Plugins->Available Plugins
     * to get error checks and code completion for the Android project files.
     */
    private SensorManager sensorManager = null;
    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;
    spaceShootout.Main main;
    
    InterstitialAd ad;
    boolean canShow;
    @Override
    protected void onResume() 
    {
        super.onResume();
        // Register this class as a listener for the accelerometer sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        wakeLock.acquire();
    }

    @Override
    protected void onStop() 
    {
        // Unregister the listener
        sensorManager.unregisterListener(this);
        wakeLock.release();
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        powerManager = (PowerManager)getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "FullWake");
        wakeLock.acquire();
        canShow = false;
 
        if (app != null) {
            ((Main)app).setAndroidListener(this);
        } else {
            throw new IllegalArgumentException("app is null!");
        }
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();      
        wakeLock.release();
        System.runFinalization();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    
    public MainActivity()
    {
        // Set the application class to run
        appClass = "spaceShootout.Main";
        // Try ConfigType.FASTEST; or ConfigType.LEGACY if you have problems
        eglConfigType = ConfigType.FASTEST;
        // Exit Dialog title & message
        exitDialogTitle = "Confirm Exit";
        // Enable verbose logging
        eglConfigVerboseLogging = false;
        // Choose screen orientation
        screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        // Enable MouseEvents being generated from TouchEvents (default = true)
        mouseEventsEnabled = true;
        audioRendererType = AppSettings.ANDROID_OPENAL_SOFT;
        splashPicID = R.drawable.splash;
        // Set the default logging level (default=Level.INFO, Level.ALL=All Debug Info)
        LogManager.getLogManager().getLogger("").setLevel(Level.OFF);
    }

    public void onSensorChanged(SensorEvent se) 
    {
        //wait until app is loaded
        if(main == null)
            main = (spaceShootout.Main)this.getJmeApplication();
        
        if(main != null)
        {
            if(main.sensorController != null)
            {
                main.sensorController.setX(se.values[0]);
                main.sensorController.setY(se.values[1]);
                main.sensorController.setZ(se.values[2]);
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int i) 
    {
    }

    public void loadAd() 
    {
        canShow = false;
        ad = new InterstitialAd(this);
        ad.setAdUnitId("ca-app-pub-6273712334269963/6507396526");
        AdRequest.Builder requestBuilder = new AdRequest.Builder();
        requestBuilder.addTestDevice("14131F4A6B533F8D9B823FC85FABB52D");
        final AdRequest request = requestBuilder.build();
        ad.setAdListener(new AdListener()
        {
            @Override
            public void onAdFailedToLoad(int errorCode)
            {
                canShow = false;
                ad.loadAd(request);
            }
            
            @Override
            public void onAdLoaded()
            {
                canShow = true;
            }

            @Override
            public void onAdClosed() 
            {
                canShow = false;
                ad.loadAd(request);
            }
            
        });
        
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ad.loadAd(request);
            }
        });      
    }

    public boolean showAd() 
    {
       if(canShow)
       {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ad.show();
                }
           });
           return true;
       }
       else
           return false;
    }
 
    public void exitGame()
    {
        finish();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) 
    {
        super.onConfigurationChanged(newConfig);
    }
}
