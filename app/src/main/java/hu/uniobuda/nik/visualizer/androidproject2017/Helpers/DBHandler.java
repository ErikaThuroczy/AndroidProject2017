package hu.uniobuda.nik.visualizer.androidproject2017.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler {
    public final static String DB_NAME = "gitVisualizerDB";
    public final static int DB_VERSION = 1;
    public final static String TABLE_USERS = "users";

    public DBHelper dbHelper;

    public DBHandler(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long insertUser(String name, String uid) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uid", uid);
        values.put("uname", name);
        // többi mező értéke ...
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public Cursor loadUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.query(TABLE_USERS, null, null, null, null, null, null);
        result.moveToFirst();
        db.close();
        return result;
    }


    public class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_USERS + "(" +
                    "_id    INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "uid    VARCHAR(255)" +
                    "uname  VARCHAR(255)" +
                    ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            // FIXME QnD
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(sqLiteDatabase);
        }
    }

}
