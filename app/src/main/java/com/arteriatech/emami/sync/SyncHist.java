package com.arteriatech.emami.sync;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.arteriatech.emami.common.Constants;


public class SyncHist {

    SQLiteDatabase db;
    private static SyncHist instance = null;

    private SyncHist() {
        db = Constants.EventUserHandler;
    }

    public static SyncHist getInstance() {
        if (instance == null) {
            instance = new SyncHist();
        }
        return instance;
    }

    public Cursor findAllSyncHist() {
        Cursor c = null;
        try {
            c = db.query(Constants.SYNC_TABLE, null, null, null, null, null, Constants.Collections);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return c;
    }

    public Cursor getLastSyncTime(String Tbl, String whereColumn, String wherecolumnVal) {
        String lastSyncTimeStampQry = Constants.getLastSyncTimeStamp(Tbl, whereColumn, wherecolumnVal);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(lastSyncTimeStampQry, new String[]{});
        } catch (Exception e) {
            cursor = null;
        }
        return cursor;
    }

}
