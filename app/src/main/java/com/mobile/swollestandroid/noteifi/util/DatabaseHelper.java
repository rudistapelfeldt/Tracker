package com.mobile.swollestandroid.noteifi.util;

/**
 * Created by Rudolph on 2016/06/30.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rudolph on 2016/02/06.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Tracker";
    public static final String TABLE_NAME = "place_detail";
    public static final String COL1 = "NAME";
    public static final String COL2 = "LATITUDE";
    public static final String COL3 = "LONGITUDE";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 1);

    }


    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COL1 + " TEXT PRIMARY KEY ," + COL2 + " REAL," + COL3 + " REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate(db);
    }

    public boolean InsertRecord(String name, float lat, float lng){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1,name);
        contentValues.put(COL2, lat);
        contentValues.put(COL3, lng);
        long result = db.insert(TABLE_NAME,null,contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllRecords(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select NAME, LATITUDE, LONGITUDE from " + TABLE_NAME, null );
        return result;
    }
}
