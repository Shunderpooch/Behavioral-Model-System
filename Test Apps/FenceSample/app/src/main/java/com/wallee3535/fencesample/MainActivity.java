package com.wallee3535.fencesample;

import android.*;
import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceQueryRequest;
import com.google.android.gms.awareness.fence.FenceQueryResult;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceStateMap;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Permission;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.text.Text;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    GoogleApiClient client;
    // Declare variable for MyFenceReceiver class.
    private MyFenceReceiver mMyFenceReceiver;
    private PendingIntent mPendingIntent;
    TextView logTextView;
    TextView queryTextView;
    String log = "";

    public boolean fineLocationPermission = false;
    public boolean coarseLocationPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        client = new GoogleApiClient.Builder(this).addApi(Awareness.API).build();
        client.connect();

        logTextView = (TextView) findViewById(R.id.logTextView);
        queryTextView = (TextView) findViewById(R.id.queryView);

        Intent intent = new Intent(Globals.FENCE_RECEIVER_ACTION);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        mMyFenceReceiver = new MyFenceReceiver();
        registerReceiver(mMyFenceReceiver, new IntentFilter(Globals.FENCE_RECEIVER_ACTION));

        requestPermissions();
        createMyFences();
    }

    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    Globals.MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Globals.MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    public void createMyFences() {
        // Create the primitive fences.
        AwarenessFence walkingFence = DetectedActivityFence.during(DetectedActivityFence.WALKING);
        registerFence("walking", walkingFence);
        AwarenessFence headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
        registerFence("headphone", headphoneFence);
        AwarenessFence stillFence = DetectedActivityFence.during(DetectedActivityFence.STILL);
        registerFence("still",stillFence);
        AwarenessFence tiltingFence = DetectedActivityFence.starting(DetectedActivityFence.TILTING);
        registerFence("tilting",tiltingFence);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            AwarenessFence enteringFence = LocationFence.entering(42.272775, -71.809464, 30);
            registerFence("entering", enteringFence);
            AwarenessFence exitingFence = LocationFence.exiting(42.272775, -71.809464, 30);
            registerFence("exiting", exitingFence);
            AwarenessFence inFence = LocationFence.in(42.272775, -71.809464, 30, 1L);
            registerFence("in", inFence);
        }

        // Create a combination fence to AND primitive fences.
        AwarenessFence walkingWithHeadphones = AwarenessFence.and(
                walkingFence, headphoneFence
        );


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Globals.MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fineLocationPermission = true;
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    fineLocationPermission = false;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case Globals.MY_PERMISSIONS_REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    coarseLocationPermission = true;
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    coarseLocationPermission = false;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void registerFence(final String fenceKey, final AwarenessFence fence) {
        Awareness.FenceApi.updateFences(
                client,
                new FenceUpdateRequest.Builder()
                        .addFence(fenceKey, fence, mPendingIntent)
                        .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(Globals.TAG, "Fence was successfully registered.");
                            queryFence(fenceKey);
                        } else {
                            Log.e(Globals.TAG, "Fence could not be registered: " + status);
                        }
                    }
                });
    }

    protected void unregisterFence(final String fenceKey) {
        Awareness.FenceApi.updateFences(
                client,
                new FenceUpdateRequest.Builder()
                        .removeFence(fenceKey)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Log.i(Globals.TAG, "Fence " + fenceKey + " successfully removed.");
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.i(Globals.TAG, "Fence " + fenceKey + " could NOT be removed.");
            }
        });
    }

    protected void queryFence(final String fenceKey) {
        Awareness.FenceApi.queryFences(client,
                FenceQueryRequest.forFences(Arrays.asList(fenceKey)))
                .setResultCallback(new ResultCallback<FenceQueryResult>() {
                    @Override
                    public void onResult(@NonNull FenceQueryResult fenceQueryResult) {
                        if (!fenceQueryResult.getStatus().isSuccess()) {
                            Log.e(Globals.TAG, "Could not query fence: " + fenceKey);
                            return;
                        }
                        FenceStateMap map = fenceQueryResult.getFenceStateMap();

                        for (String fenceKey : map.getFenceKeys()) {
                            FenceState fenceState = map.getFenceState(fenceKey);
                            Log.i(Globals.TAG, "Fence " + fenceKey + ": "
                                    + fenceState.getCurrentState()
                                    + ", was="
                                    + fenceState.getPreviousState()
                                    + ", lastUpdateTime="
                                    + DateFormat.format("MM/dd/yyyy HH:mm:ss",
                                    new Date(fenceState.getLastFenceUpdateTimeMillis())));
                        }
                    }
                });
    }

    // Extend the BroadcastReceiver class.
    public class MyFenceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            FenceState fenceState = FenceState.extract(intent);

            if (TextUtils.equals(fenceState.getFenceKey(), "headphone")) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        record("headphone plugged in", fenceState);
                        break;
                    case FenceState.FALSE:
                        record("headphone not plugged in", fenceState);
                        break;
                    case FenceState.UNKNOWN:
                        record("headphone unknown", fenceState);
                        break;
                }
            } else if (TextUtils.equals(fenceState.getFenceKey(), "entering")) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        record("entering location", fenceState);
                        break;
                    case FenceState.UNKNOWN:
                        record("location unknown", fenceState);
                        break;
                }
            } else if (TextUtils.equals(fenceState.getFenceKey(), "exiting")) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        record("exiting location", fenceState);
                        break;
                    case FenceState.UNKNOWN:
                        record("location unknown", fenceState);
                        break;
                }
            } else if (TextUtils.equals(fenceState.getFenceKey(), "in")) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        record("in location", fenceState);
                        break;
                    case FenceState.FALSE:
                        record("not in location", fenceState);
                        break;
                    case FenceState.UNKNOWN:
                        record("location unknown", fenceState);
                        break;
                }
            }else if (TextUtils.equals(fenceState.getFenceKey(), "walking")) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        record("walking", fenceState);
                        break;
                    case FenceState.FALSE:
                        record("not walking", fenceState);
                        break;
                    case FenceState.UNKNOWN:
                        record("walking unknown", fenceState);
                        break;
                }
            }else if (TextUtils.equals(fenceState.getFenceKey(), "still")) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        record("still", fenceState);
                        break;
                    case FenceState.FALSE:
                        record("not still", fenceState);
                        break;
                    case FenceState.UNKNOWN:
                        record("still unknown", fenceState);
                        break;
                }
            }else if (TextUtils.equals(fenceState.getFenceKey(), "tilting")) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        record("tilting", fenceState);
                        break;
                    case FenceState.UNKNOWN:
                        record("tilting unknown", fenceState);
                        break;
                }
            }
        }
    }

    public void record(String msg, FenceState fenceState) {
        String lastUpdateTime = getLastUpdateTime(fenceState);
        log = log + lastUpdateTime + " " + msg + "\n";
        logTextView.setText(log);
        Log.i(Globals.TAG, lastUpdateTime + " " + msg);
    }

    public String getLastUpdateTime(FenceState fenceState) {
        return DateFormat.format("MM/dd/yyyy HH:mm:ss",
                new Date(fenceState.getLastFenceUpdateTimeMillis())).toString();
    }

    public void onClearClicked(View view) {
        log = "";
        logTextView.setText(log);
    }


    public void onQueryClicked(View view){
        Log.i(Globals.TAG, "query clicked");
        final String query;

        Awareness.FenceApi.queryFences(client,
                FenceQueryRequest.all())
                .setResultCallback(new ResultCallback<FenceQueryResult>() {
                    @Override
                    public void onResult(@NonNull FenceQueryResult fenceQueryResult) {
                        if (!fenceQueryResult.getStatus().isSuccess()) {
                            Log.e(Globals.TAG, "Could not query");
                            return;
                        }
                        FenceStateMap map = fenceQueryResult.getFenceStateMap();
                        String s = "";
                        for (String fenceKey : map.getFenceKeys()) {
                            FenceState fenceState = map.getFenceState(fenceKey);
                            s += "Fence " + fenceKey + ": "
                                    + fenceState.getCurrentState()
                                    + ", was="
                                    + fenceState.getPreviousState()+"\n";
                        }
                        queryTextView.setText(s);
                    }
                });

    }


}
