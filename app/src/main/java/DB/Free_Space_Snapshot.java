package DB;

import android.os.Environment;
import android.os.StatFs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Alexandros on 2/1/2015.
 */
public class Free_Space_Snapshot {
    private String Date;
    private double inMegaAvailable;
    private double exMegaAvailable;

    /*
    Saves the current date and free space (internal and external) information
     */
    public Free_Space_Snapshot(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        Date=dateFormat.format(cal.getTime());
        getSpaceInfo();
    }

    /*
    Helper function to get internal and external space info
     */
    private void getSpaceInfo(){
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        StatFs stats = new StatFs(Environment.getDataDirectory().getPath());
        double sdAvailSize = (double)stat.getAvailableBlocks() * (double)stat.getBlockSize();
        double inAvailSize = (double) stats.getAvailableBlocks() * (double) stats.getBlockSize();
        //One binary megabyte equals 1048576 bytes.
        exMegaAvailable = sdAvailSize / 1048576;
        inMegaAvailable = inAvailSize / 1048576;
    }


    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public double getInMegaAvailable() {
        return inMegaAvailable;
    }

    public void setInMegaAvailable(double inMegaAvailable) {
        this.inMegaAvailable = inMegaAvailable;
    }

    public double getExMegaAvailable() {
        return exMegaAvailable;
    }

    public void setExMegaAvailable(double exMegaAvailable) {
        this.exMegaAvailable = exMegaAvailable;
    }
}
