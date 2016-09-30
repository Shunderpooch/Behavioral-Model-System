package com.wallee3535.gesturedetector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by Walter on 9/28/2016.
 */
public class GestureService extends Service {
    WindowManager mWindowManager;
    View mDummyView;
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new ServiceBinder();

    public class ServiceBinder extends Binder {
        GestureService getService() {
            return GestureService.this;
        }
    }

    //These three are our main components.
    WindowManager wm;
    LinearLayout ll;
    WindowManager.LayoutParams ll_lp;


    public GestureService(){

    }

    @Override
    public void onCreate() {
        Log.i(Globals.TAG, "GestureService-onCreate");
        super.onCreate();
        Context mContext = getApplicationContext();

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mDummyView = new LinearLayout(mContext);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(1, WindowManager.LayoutParams.MATCH_PARENT);
        mDummyView.setLayoutParams(params);

        mDummyView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("walter's tag", "Touch event: " + motionEvent.toString());
                return false;
            }
        });
        /*
        mDummyView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View view, MotionEvent motionEvent) {
                Log.d("walter's tag",motionEvent.toString());
                return false;
            }
        });
        */


        WindowManager.LayoutParams mparams = new WindowManager.LayoutParams(
                //1, 1
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                |WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                ,
                PixelFormat.TRANSPARENT
        );
        mparams.gravity = Gravity.LEFT | Gravity.TOP;
        mWindowManager.addView(mDummyView, mparams);




    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        WindowManager.LayoutParams mparams = new WindowManager.LayoutParams(
                1, /* width */
                1, /* height */
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT
        );
        mparams.gravity = Gravity.LEFT | Gravity.TOP;
        mWindowManager.addView(mDummyView, mparams);


        return super.onStartCommand(intent, flags, startId);
    }



}
