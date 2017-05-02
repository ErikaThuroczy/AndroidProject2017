package hu.uniobuda.nik.visualizer.androidproject2017.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import hu.uniobuda.nik.visualizer.androidproject2017.Models.Statistics;

public class DBHandler {
    private DBHelper dbHelper;

    private final static String DB_NAME = "GitVisualizerDB";
    private final static int DB_VERSION = 1;
    private final static String TABLE_USERS = "Users";
    private static final String TABLE_REPOLIST = "RepoList";
    private static final String TABLE_STAT = "Stat";

    //USERS table
    private static final String USERS_ID = "ID";
    private static final String USERS_UID = "UID";
    private static final String USERS_NAME = "Name";
    private static final String USERS_EMAIL = "Email";
    private static final String USERS_PASSWORD = "Password";
    private static final String USERS_CREATED = "Created";

    //REPOLIST table
    private static final String REPOLIST_ID = "ID";
    private static final String REPOLIST_IDNAME = "IdName";
    private static final String REPOLIST_CREATED = "Created";

    //STAT table
    private static final String STAT_ID = "ID";
    private static final String STAT_ID_NAME = "IdName";
    private static final String STAT_AUTHOR = "Author";
    private static final String STAT_TOTAL_COMMIT = "TotalCommit";
    private static final String STAT_ELAPSED_TIME = "ElapsedTime";
    private static final String STAT_MOST_COMMIT_COUNT = "MostCommitCount";
    private static final String STAT_MOST_COMMIT_SIZE = "MostCommitSize";
    private static final String STAT_PERIOD = "BusiestPeriod";
    private static final String STAT_OTHER = "Other";
    private static final String STAT_CREATED = "Created";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE if not EXISTS " + TABLE_USERS + "(" +
            USERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            USERS_UID + " VARCHAR(255)," +
            USERS_NAME + " VARCHAR(255)," +
            USERS_EMAIL + " VARCHAR(255)," +
            USERS_PASSWORD + " VARCHAR(255)," +
            USERS_CREATED + " VARCHAR(255)" +
            ")";
    private static final String CREATE_TABLE_REPOLIST = "CREATE TABLE if not EXISTS " + TABLE_REPOLIST + "(" +
            REPOLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            REPOLIST_IDNAME + " VARCHAR(255)," +
            REPOLIST_CREATED + " VARCHAR(255)" +
            ")";
    private static final String CREATE_TABLE_STAT = "CREATE TABLE if not EXISTS " + TABLE_STAT + "(" +
            STAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            STAT_ID_NAME + " VARCHAR(255)," +
            STAT_AUTHOR + " VARCHAR(255)," +
            STAT_TOTAL_COMMIT + " VARCHAR(255)," +
            STAT_ELAPSED_TIME + " VARCHAR(255)," +
            STAT_MOST_COMMIT_COUNT + " VARCHAR(255)," +
            STAT_MOST_COMMIT_SIZE + " VARCHAR(255)," +
            STAT_PERIOD + " VARCHAR(255)," +
            STAT_OTHER + " VARCHAR(255)," +
            STAT_CREATED + " VARCHAR(255)" +
            ")";


    public DBHandler(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void InsertIntoUSERS(String uid, String name, String email, String password, String created) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("UID", uid);
        contentValues.put("Name", name);
        contentValues.put("Email", email);
        contentValues.put("Password", password);
        contentValues.put("Created", created);

        dbHelper.getWritableDatabase().insert(TABLE_USERS, null, contentValues);

        Log.e(
                "InsertRowUsers",
                "Insert successful:: Name:" + name + "| Uid: " + uid + "| Email: " + email + "| Created: " + created
        );
    }

    public void InsertIntoREPOLIST(String id_name, String created) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("IdName", id_name);
        contentValues.put("Created", created);

        dbHelper.getWritableDatabase().insert(TABLE_REPOLIST, null, contentValues);

        Log.e(
                "InsertRowRepoList",
                "Insert successful:: IdName:" + id_name + "| Created: " + created
        );
    }

    public void InsertIntoSTAT(Statistics stat, String id_name, String created) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("IdName", id_name);
        contentValues.put("Author", stat.getAuthor());
        contentValues.put("TotalCommit", stat.getTotal_commit());
        contentValues.put("ElapsedTime", stat.getElapsed_time());
        contentValues.put("MostCommitCount", stat.getMost_commit_count());
        contentValues.put("MostCommitSize", stat.getMost_commit_size());
        contentValues.put("BusiestPeriod", stat.getBusiest_period());
        contentValues.put("Other", stat.getOther());
        contentValues.put("Created", created);

        dbHelper.getWritableDatabase().insert(TABLE_STAT, null, contentValues);

        Log.e(
                "InsertRowStat",
                "Insert successful:: IdName:" + id_name + "| Author: " + stat.getAuthor() + "| TotalCommit: " + stat.getTotal_commit() + "| Created: " + created
        );
    }

    /*public ArrayList<String> getUsers() {
        Cursor b = DB.rawQuery("Select * From Users", null, null);
        ArrayList<String> usersArrayList = new ArrayList<String>();
        if (b.moveToLast()) {
            usersArrayList.add(b.getString(b.getColumnIndex("UID")));
            usersArrayList.add(b.getString(b.getColumnIndex("Name")));
            usersArrayList.add(b.getString(b.getColumnIndex("Email")));
            usersArrayList.add(b.getString(b.getColumnIndex("Password")));
            usersArrayList.add(b.getString(b.getColumnIndex("Created")));
            usersArrayList.add(b.getString(b.getColumnIndex("ID")));

            Log.e("getUsersList", "List: UID:" + usersArrayList.get(0) + "| Name: " + usersArrayList.get(1) + "| Email: " + usersArrayList.get(2) + "| Created: " + usersArrayList.get(4));

            b.close();
            return usersArrayList;
        }
        b.close();
        return null;
    }*/
    /*
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
    }*/
    public Cursor loadStat() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.query(TABLE_STAT, null, null, null, null, null, null);
        result.moveToFirst();
        db.close();
        return result;
    }

    public Cursor getStatByIdName(String id_name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.query(TABLE_STAT, null, "IdName = ?", new String[]{id_name}, null, null, "Created");
        result.moveToFirst();
        db.close();
        return result;
    }

    public ArrayList<String> getRepoList() {
        ArrayList<String> array_list = new ArrayList<>();

        Cursor res = dbHelper.getReadableDatabase().query(TABLE_REPOLIST, null, null, null, null, null, null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex("IdName")));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    private class DBHelper extends SQLiteOpenHelper {

        private DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE_USERS);
            Log.e("CREATE_TABLE_USERS", "CREATE_TABLE_USERS");

            sqLiteDatabase.execSQL(CREATE_TABLE_REPOLIST);
            Log.e("CREATE_TABLE_REPOLIST", "CREATE_TABLE_REPOLIST");

            sqLiteDatabase.execSQL(CREATE_TABLE_STAT);
            Log.e("CREATE_TABLE_STAT", "CREATE_TABLE_STAT");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            // FIXME QnD
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_REPOLIST);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STAT);
            onCreate(sqLiteDatabase);
        }
    }

}
