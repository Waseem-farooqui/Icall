package com.techstartegies.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.techstartegies.icall.ChatMessage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Waseem on 29-Nov-15.
 */
public class SqliteUtil extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "icallDB";
    public static final String ICALL_TABLE_NAME = "messages";
    public static final String ICALL_COLUMN_ID = "id";
    public static final String ICALL_COLUMN_SENDER = "sender";  //To ali@srv.techaa
    public static final String ICALL_COLUMN_RECEIVER = "receiver";//From
    public static final String ICALL_COLUMN_MESSAGE = "message";
    public static final String ICALL_COLUMN_STATUS = "status";
    public static final String ICALL_COLUMN_SR = "sr";
    public static final String ICALL_COLUMN_DATE = "date";
    ArrayList<String> array_list = new ArrayList<String>();
    private HashMap hp;

    public SqliteUtil(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " + ICALL_TABLE_NAME + "( " +
                        ICALL_COLUMN_ID + " integer," +
                        ICALL_COLUMN_SENDER + " text," +
                        ICALL_COLUMN_RECEIVER + " text," +
                        ICALL_COLUMN_MESSAGE + " text," +
                        ICALL_COLUMN_STATUS + " text," +
                        ICALL_COLUMN_SR + " integer," +
                        ICALL_COLUMN_DATE + " text)"
        );
    }


    public void drop() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + ICALL_TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        db.execSQL("DROP TABLE IF EXISTS " + ICALL_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertContact(int id, String sender, String receiver, String message, String status, int sr, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ICALL_COLUMN_ID, id);
        contentValues.put(ICALL_COLUMN_SENDER, sender);
        contentValues.put(ICALL_COLUMN_RECEIVER, receiver);
        contentValues.put(ICALL_COLUMN_MESSAGE, message);
        contentValues.put(ICALL_COLUMN_STATUS, status);
        contentValues.put(ICALL_COLUMN_SR, sr);
        contentValues.put(ICALL_COLUMN_DATE, date);
        db.insert(ICALL_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + ICALL_TABLE_NAME + " where " + ICALL_COLUMN_ID + "=" + id + "", null);
        return res;
    }

    public Cursor checkStatus(int status) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from "+ICALL_TABLE_NAME+" where " + ICALL_COLUMN_STATUS + "=" + status + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db == null)
            Log.e("Database Object", "null");
        int numRows = (int) DatabaseUtils.queryNumEntries(db, ICALL_TABLE_NAME);
        return numRows;
    }


    public boolean updateStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        try {
            db.update(ICALL_TABLE_NAME, contentValues, ICALL_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        } catch (Exception e) {
            Log.e("Status Update", e.getMessage());
            return false;
        }

        return true;
    }

    public Integer deleteMessage(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ICALL_TABLE_NAME,
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<String> getAllMessages() {


        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + ICALL_TABLE_NAME, null);
        res.moveToFirst();

        int i = 0;
        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(ICALL_COLUMN_RECEIVER)));
            // Log.e("Prining",""+array_list.get(i));
            //i++;
            res.moveToNext();
        }
        return array_list;
    }

    public void deleteAll() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery("DELETE FROM "+ICALL_TABLE_NAME,null);
    }

    public Cursor getAllRecords() {


        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + ICALL_TABLE_NAME, null);

        return res;
    }


    public ArrayList<ChatMessage> getFullRecord() {

        ArrayList<ChatMessage> m = new ArrayList<ChatMessage>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + ICALL_TABLE_NAME, null);

        int r = (int) DatabaseUtils.queryNumEntries(db, ICALL_TABLE_NAME);

        if (r != 0) {

            res.moveToFirst();

            while (res.isAfterLast() == false)

            {
                ChatMessage msg = new ChatMessage();
                msg.setId(res.getInt(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_ID)));
                msg.setSender(res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_SENDER)));
                msg.setReceiver(res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_RECEIVER)));
                msg.setMessage(res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_MESSAGE)));
                msg.setStatus(res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_STATUS)));
                msg.setSr(res.getInt(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_SR)));
                msg.setDate(res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_DATE)));
                if (res.getInt(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_SR)) == 1)
                    msg.setMe(true);
                else
                    msg.setMe(false);

                m.add(msg);


                res.moveToNext();

            }
            if(!res.isClosed())
                res.close();
            return m;

        }

        return null;
    }
}
