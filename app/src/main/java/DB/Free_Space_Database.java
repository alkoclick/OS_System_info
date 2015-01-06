package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Alexandros on 2/1/2015.
 */


public class Free_Space_Database extends SQLiteOpenHelper {

    private static final String Database_Name = "FreeSpaceDB" ;
    private static final int Database_Version = 1 ;

    private static final String Table_FreeSpace = "Free_Space";

    private static final String Key_Id = "_id";
    private static final String Key_Internal = "internal";
    private static final String Key_External="external";
    private static final String Key_Date = "date";

    private static final String Create_Free_Space_Table= "CREATE TABLE "+ Table_FreeSpace + "("
            + Key_Id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + Key_Date + " TEXT NOT NULL,"
            + Key_Internal + " DOUBLE,"
            + Key_External + " DOUBLE" + ")";

    private SQLiteDatabase db;


    /*
    Connects to the database of free space snapshots
     */
    public Free_Space_Database(Context context){
        super(context, Database_Name, null, Database_Version);
        db=this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_Free_Space_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Table_FreeSpace);
        onCreate(db);
    }

    /**
     * Adds current space info to the database
     * @param Snap Object containing current free space info
     */
    public void insertSnapshot(Free_Space_Snapshot Snap){
        ContentValues cv = new ContentValues();
        cv.put(Key_Date,Snap.getDate());
        cv.put(Key_Internal,Snap.getInMegaAvailable());
        cv.put(Key_External,Snap.getExMegaAvailable());

        Log.i("msg",Snap.getInMegaAvailable()+"");
        Log.i("msg",Snap.getExMegaAvailable()+"");
        Log.i("msg",Snap.getDate()+"");

        db.insert(Table_FreeSpace,null,cv);

    }

    /*
    Gets all snapshots from the database
     */
    public Cursor getAllSnapshots(){
        return this.getReadableDatabase().rawQuery("SELECT * FROM " + Table_FreeSpace,
                null);
    }
}
