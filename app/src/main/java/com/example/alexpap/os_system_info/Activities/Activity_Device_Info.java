package com.example.alexpap.os_system_info.Activities;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import com.example.alexpap.os_system_info.R;

import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Alexandros on 19/12/2014.
 */
public class Activity_Device_Info extends FragmentActivity{
    private Button procs;
    private TextView androidVer,model,freeSpace,battery,uptime,connectivity;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_information);
        initGUI();
        initListeners();
        androidVer.setText("Android API: "+Build.VERSION.SDK_INT+" ");

        model.setText(Build.MANUFACTURER+" "+Build.MODEL+" "+Build.DEVICE);

        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        StatFs stats = new StatFs(Environment.getDataDirectory().getPath());
        double sdAvailSize = (double)stat.getAvailableBlocksLong() * (double)stat.getBlockSizeLong();
        double inAvailSize = (double) stats.getAvailableBlocksLong() * (double) stats.getBlockSizeLong();
        //One binary megabyte equals 1048576 bytes.
        double megaAvailableSD = sdAvailSize / 1048576;
        double megaAvailableIn = inAvailSize / 1048576;
        freeSpace.setText("Internal "+String.valueOf(megaAvailableSD)+"\n"+" SD "+String.valueOf(megaAvailableSD));

        battery.setText(BatteryManager.EXTRA_LEVEL+" "+BatteryManager.EXTRA_SCALE);

        uptime.setText(String.valueOf(android.os.SystemClock.uptimeMillis()));

        Context ctx = getBaseContext();
        String temp_con="";
        ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i.isConnected()) {
            temp_con.concat("Connected "+i.toString());
        } else {
            temp_con.concat("Not connected "+i.toString());
        }
        connectivity.setText(ConnectivityManager.EXTRA_NO_CONNECTIVITY);
    }

    public void initGUI(){
        procs = (Button) findViewById(R.id.btProcs);
        androidVer = (TextView) findViewById(R.id.tvAndroidVer);
        model = (TextView) findViewById(R.id.tvModel);
        freeSpace = (TextView) findViewById(R.id.tvFreeSpace);
        battery = (TextView) findViewById(R.id.tvBattery);
        uptime = (TextView) findViewById(R.id.tvUptime);
        connectivity = (TextView) findViewById(R.id.tvConnectivity);
    }

    public void initListeners(){
        procs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Activity_Device_Info.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

}
