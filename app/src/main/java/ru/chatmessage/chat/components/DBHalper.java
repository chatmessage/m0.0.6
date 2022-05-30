package ru.chatmessage.chat.components;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DBHalper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "listDb";
    public static final String TABLE_FRIEND = "friend";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";

    public DBHalper(@Nullable Context context, @Nullable String name, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_FRIEND + "(" + KEY_NAME + " text" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
