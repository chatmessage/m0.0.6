package ru.chatmessage.chat.components;

import android.provider.BaseColumns;

public class DatabaseContract {
    final public static DatabaseContract contract = new DatabaseContract();
    final public static PersonEntry personEntry = contract.new PersonEntry();

    public class PersonEntry implements BaseColumns {


        public final String TABLE_NAME = "persons";
        public final String COLUMN_ID = _ID;
        public final String COLUMN_NAME = "name";

        final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT NOT NULL)";

    }
}
