package com.example.alexpap.os_system_info.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import com.example.alexpap.os_system_info.R;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import DB.Free_Space_Database;
import DB.Free_Space_Snapshot;

/**
 * Created by Alexandros on 19/12/2014.
 */
public class Activity_Free_Space extends FragmentActivity {
    private Button procs,takesnap;
    private TextView date,freeIn,freeSD;
    private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.free_space);
        initGUI();
        initListeners();

        Free_Space_Database db = new Free_Space_Database(Activity_Free_Space.this);


        String [] dates_In_Sd = getAndDisplayMeansPerDay(db);
        date.setText("Date:\n"+dates_In_Sd[0]);
        freeIn.setText("Free int:\n"+dates_In_Sd[1]);
        freeSD.setText("Free ext:\n"+dates_In_Sd[2]);
        db.close();

    }

    public void initGUI(){
        procs = (Button) findViewById(R.id.btProcs); // Click to go to procs (main) menu
        takesnap = (Button) findViewById(R.id.btTakeSnapshot); // Click to take a snapshot of current info
        date = (TextView) findViewById(R.id.tvDate);
        freeIn = (TextView) findViewById(R.id.tvInternalFree);
        freeSD = (TextView) findViewById(R.id.tvSDFree);

    }

    public void initListeners(){
        procs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Activity_Free_Space.this, MainActivity.class);
                startActivity(i);
            }
        });
        takesnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Free_Space_Database db = new Free_Space_Database(Activity_Free_Space.this);
                Free_Space_Snapshot current = new Free_Space_Snapshot();
                db.insertSnapshot(current);
                String [] dates_In_Sd = getAndDisplayMeansPerDay(db);
                date.setText("Date:\n"+dates_In_Sd[0]);
                freeIn.setText("Free int:\n"+dates_In_Sd[1]);
                freeSD.setText("Free ext:\n"+dates_In_Sd[2]);
                db.close();
            }
        });
    }

    /**
     * Creates clusters with snapshots taken on the same day, calculates and gets a mean for them
     * @param db The database to draw information from
     * @return String[] or Double[], the means of internal and external space every day
     */
    private String[] getAndDisplayMeansPerDay(Free_Space_Database db){
        double tempIn,tempSD;
        String tempDate;
        double count=0;
        String[] means=new String[]{new String(""),new String(""),new String("")};
        try {

            cursor = db.getAllSnapshots();
            Log.i("msg",cursor.getCount()+"");
            cursor.moveToFirst();
            tempDate=cursor.getString(1);
            tempIn=cursor.getDouble(2);
            tempSD=cursor.getDouble(3);
            count++;
            cursor.moveToNext();

            while (cursor.getPosition() < cursor.getCount()) {
                if (cursor.getString(1).equals(tempDate)){ // If the date is the same, we want to get the mean from the items
                    tempIn+=cursor.getDouble(2);
                    tempSD+=cursor.getDouble(3);
                    count++;
                }
                else {     // If the date is different, calculate the mean of the previous day items and start counting again
                    means[0]+=tempDate+"\n";
                    means[1]+=tempIn/count+"\n";
                    means[2]+=tempSD/count+"\n";
                    tempDate=cursor.getString(1);
                    tempIn=cursor.getDouble(2);
                    tempSD=cursor.getDouble(3);
                    count=1.0;
                }
                cursor.moveToNext();
            }
            // Don't forget to calculate the last element!
            means[0]+=tempDate+"\n";
            means[1]+=String.format("%.3f",tempIn/count)+"\n";
            means[2]+=String.format("%.3f",tempSD/count)+"\n";

            cursor.close();
        }
        catch (ArrayIndexOutOfBoundsException e){
            Toast.makeText(Activity_Free_Space.this,"Error loading Database values",Toast.LENGTH_LONG).show();
            Log.i("msg","Error");
            return null;
        }
        return means;
    }

}
