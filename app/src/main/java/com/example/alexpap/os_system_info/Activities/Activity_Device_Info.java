package com.example.alexpap.os_system_info.Activities;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Alexandros on 19/12/2014.
 */

public class Activity_Device_Info extends FragmentActivity{
    private Button procs;
    private TextView androidVer,androidCPU,model,freeSpace,battery,uptime,connectivity;
    BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            battery.setText("Battery level: "+String.valueOf(level) + "%");
            mBatInfoReceiver.clearAbortBroadcast();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_information);
        this.registerReceiver(this.mBatInfoReceiver,  new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        initGUI();
        initListeners();
    }

    public void initGUI(){
        procs = (Button) findViewById(R.id.btProcs);  // Click to open procs (main) menu

        androidVer = (TextView) findViewById(R.id.tvAndroidVer);  // Shows android version information
        androidVer.setText("Android Version: "+Build.VERSION.RELEASE);

        androidCPU = (TextView) findViewById(R.id.tvAndroidCPU);  // Shows CPU information
        androidCPU.setText(getCPUInfo());

        model = (TextView) findViewById(R.id.tvModel);  // Shows make and model information
        model.setText("Make and model: "+Build.MANUFACTURER+" "+Build.MODEL);

        freeSpace = (TextView) findViewById(R.id.tvFreeSpace);  // Shows internal and external available space
        freeSpace.setText("Free space:\n"+getSpaceInfo());

        battery = (TextView) findViewById(R.id.tvBattery);  // Shows remaining battery

        uptime = (TextView) findViewById(R.id.tvUptime);   // Shows the current uptime
        double uptime_secs = android.os.SystemClock.uptimeMillis()/1000;
        int uptime_hours = (int) uptime_secs/3600;
        int uptime_mins = (int) (uptime_secs - uptime_hours*3600)/60;
        uptime.setText("Uptime: "+String.valueOf(uptime_hours)+" hours "+String.valueOf(uptime_mins)+" minutes");

        connectivity = (TextView) findViewById(R.id.tvConnectivity);  // Shows whether or not the device is connected and if yes, what type of connection
        connectivity.setText("Connection: "+getNetInfo());
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


    private String getCPUInfo() {
        StringBuffer sb = new StringBuffer();
        //sb.append("abi: ").append(Build.CPU_ABI).append("\n");
        if (new File("/proc/cpuinfo").exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
                String aLine;
                while ((aLine = br.readLine()) != null) {
                    //sb.append(aLine + "\n");
                    if (aLine.contains("Processor")) sb.append(aLine);
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private String getSpaceInfo(){
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        StatFs stats = new StatFs(Environment.getDataDirectory().getPath());
        double sdAvailSize = (double)stat.getAvailableBlocks() * (double)stat.getBlockSize();
        double inAvailSize = (double) stats.getAvailableBlocks() * (double) stats.getBlockSize();
        //One binary megabyte equals 1048576 bytes.
        double megaAvailableSD = sdAvailSize / 1048576;
        double megaAvailableIn = inAvailSize / 1048576;
        return "\t-Internal: "+String.format("%.2f", megaAvailableIn)+" MB\n"+"\t-SD card: "+String.format("%.2f",megaAvailableSD)+" MB";
    }

    private String getNetInfo() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            String toBeReturned = "";
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                toBeReturned+="Wi-Fi ";
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                toBeReturned+="3G ";
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_BLUETOOTH)
                toBeReturned+="Bluetooth ";
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_VPN)
                toBeReturned+="VPN ";
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_ETHERNET)
                toBeReturned+="Ethernet ";
            return toBeReturned;
        }
        else return "Disconnected";
    }

}
