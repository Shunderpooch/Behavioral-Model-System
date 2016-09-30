package com.wallee3535.gesturedetector;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private GestureService mServ;
    private boolean mIsBound=false;

    private ServiceConnection Scon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder
                iBinder) {
            GestureService.ServiceBinder binder = (GestureService.ServiceBinder) iBinder;
            mServ = binder.getService();
            Log.i(Globals.TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(Globals.TAG, "onServiceDisconnected");
        }
    };
    void doBindService() {
        if (!mIsBound) {
            boolean success = bindService(new Intent(this, GestureService.class), Scon, Context.BIND_AUTO_CREATE);
            if(success) {
                mIsBound = true;
                Log.i(Globals.TAG, "doBindService succeeded");
            }else{
                Log.i(Globals.TAG, "doBindService failed");
            }
        }
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    public void onStartServiceButtonClicked(View view){
        Log.i(Globals.TAG, "onStartServiceButtonClicked");
        doBindService();
    }
    public void onStopServiceButtonClicked(View view){
        Log.i(Globals.TAG, "onStopServiceButtonClicked");
        doUnbindService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View startView = inflater.inflate(R.layout.activity_main, null);
        setContentView(startView);

    }

    public void simulate(View view){
        Log.i(Globals.TAG, "reached here");
        simulateEventDown(view, 2, 2);
    }

    public void simulateEventDown( View v, long x, long y )
    {
        MotionEvent e = MotionEvent.obtain( SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN,
                x, y, 0);
        v.dispatchTouchEvent(e);
    }



}
