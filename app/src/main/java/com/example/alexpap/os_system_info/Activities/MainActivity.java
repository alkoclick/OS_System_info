package com.example.alexpap.os_system_info.Activities;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.alexpap.os_system_info.R;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import DB.Free_Space_Database;
import DB.Free_Space_Snapshot;

public class MainActivity extends Activity {
    private Button freeSpace;
    private Button devInfo;
    private TextView procWindow,pidWindow,memUseWindow;

    private static final int every_x_minutes=30;

    /*  TODO when given time:
        TODO Implement scrolling via swipe between different screens
        TODO Implement sorting the procs by name,pid,rss
        TODO Implement more detailed date/time of last update info

     */



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGUI();
        initListeners();

        /**
         * Takes a snapshot of available space info every X minutes, specified above
         */
        Runnable helloRunnable = new Runnable() {
            public void run() {
                Free_Space_Database db = new Free_Space_Database(MainActivity.this);
                Free_Space_Snapshot current = new Free_Space_Snapshot();
                db.insertSnapshot(current);
                db.close();
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, every_x_minutes, TimeUnit.MINUTES);
    }

    public void initGUI(){
        freeSpace = (Button) findViewById(R.id.btFreeSpace);  // Click to go to free space (left) menu
        devInfo = (Button) findViewById(R.id.btDevInfo);  // Click to go to device info (right) menu
        procWindow = (TextView) findViewById(R.id.tvProcs); // Diplays proc names
        pidWindow = (TextView) findViewById(R.id.tvPids); // Diplays proc pids
        memUseWindow = (TextView) findViewById(R.id.tvMemUse); // Dsiplays proc rss


        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = activityManager.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        String procs="Process name\n\n";
        String pids="PID\n\n";
        String mem="RSS (kB)\n\n";


        while(i.hasNext())
        {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
            try
            {
                CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                procs+=c.toString()+"\n";  // Add the proc name
                pids+=info.pid+"\n";        // its pid
                mem+=String.format("%.0f",getMemUse(info.pid))+"\n";  // and its RSS
            }
            catch(Exception e)
            {
                Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        procWindow.setText(procs);
        pidWindow.setText(pids);
        memUseWindow.setText(mem);
    }

    public void initListeners(){
        devInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Activity_Device_Info.class);
                startActivity(i);
            }
        });

        freeSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Activity_Free_Space.class);
                startActivity(i);
            }
        });
    }

    private double getMemUse(int PID) {
        String address="/proc/"+PID+"/status";
        if (new File(address).exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(address)));
                String aLine;
                while ((aLine = br.readLine()) != null) {
                    if (aLine.contains("VmRSS")) {
                        return Double.valueOf(aLine.substring(10, 16)); // Remove unecessary info, the "VMRSS:   " , "    kB" parts from the line
                    }
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }



}