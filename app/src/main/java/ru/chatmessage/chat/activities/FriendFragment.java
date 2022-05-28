package ru.chatmessage.chat.activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import ru.chatmessage.chat.R;
import ru.chatmessage.chat.components.DBHalper;

public class FriendFragment extends Fragment {

    EditText entfriend;
    DBHalper dbHalper;
    Context thiscontext;
    ImageButton badd, bdel;
    String name;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> users = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_friendlist, container, false);

        thiscontext = container.getContext();

        badd = view.findViewById(R.id.baddfriend);
        bdel = view.findViewById(R.id.bdelfriend);
        entfriend = view.findViewById(R.id.emailField);
        listView = view.findViewById(R.id.friendlist);

        dbHalper = new DBHalper(thiscontext, DBHalper.DATABASE_NAME, 1);

        return view;
    }

    SQLiteDatabase database = dbHalper.getWritableDatabase();

    ContentValues contentValues = new ContentValues();

    public void onClickAdd(){
        name = entfriend.getText().toString();
        entfriend.getText().clear();

        contentValues.put(DBHalper.KEY_NAME, name);

        database.insert(DBHalper.TABLE_FRIEND, null, contentValues);

        database.close();

    }

//    public void onClickDel(){
//        name = entfriend.getText().toString();
//        entfriend.getText().clear();
//
//        Cursor cursor = database.query(DBHalper.TABLE_FRIEND,null,null, null, null,null,null);
//        }

}
