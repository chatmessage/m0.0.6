package ru.chatmessage.chat.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.Inflater;

import ru.chatmessage.chat.R;
import ru.chatmessage.chat.components.DBHelper;
import ru.chatmessage.chat.components.DatabaseContract;
import ru.chatmessage.chat.data.UserData;

public class FriendFragment extends Fragment {

    EditText entfriend;
    DBHelper dbHelper;
    Context thiscontext;
    ImageButton badd, bdel;
    String name;
    ListView listView;
    Cursor c;
    List<UserData> friends = new ArrayList<>();
    PersonAdapter friendsAdapter;
    FriendFragment fragment;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_friendlist, container, false);

        thiscontext = container.getContext();

        friendsAdapter = new PersonAdapter(thiscontext);

        badd = view.findViewById(R.id.baddfriend);
        bdel = view.findViewById(R.id.bdelfriend);
        badd.setOnClickListener(v -> onClickAdd());
        bdel.setOnClickListener(v -> onClickDel());

        entfriend = view.findViewById(R.id.emailField);
        listView = view.findViewById(R.id.friendlist);
        listView.setAdapter(friendsAdapter);
        fragment = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserData user = (UserData)adapterView.getAdapter().getItem(i);
                System.out.println(user.getLogin());
                Intent chat = new Intent(getContext(), ChatActivity.class);
                chat.putExtra("friendLogin", user.getLogin());
                getActivity().startActivityFromFragment(fragment, chat, 1);
            }
        });

        dbHelper = new DBHelper(thiscontext);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        returnNames();
    }

    public void onClickAdd(){
        name = entfriend.getText().toString();
        entfriend.getText().clear();
        ContentValues contentValues = new ContentValues();
        if(name.length()>0) {
            contentValues.put(DatabaseContract.personEntry.COLUMN_NAME, name);
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            database.insert(DatabaseContract.personEntry.TABLE_NAME, null, contentValues);

            returnNames();
        }
    }

    public void onClickDel() {
        name = entfriend.getText().toString();
        entfriend.getText().clear();

        if (name.length() > 0) {
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            database.delete(DatabaseContract.personEntry.TABLE_NAME, DatabaseContract.personEntry.COLUMN_NAME + " = ?", new String[]{name});
            returnNames();
        }
    }

    public void returnNames(){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        c = database.query(DatabaseContract.personEntry.TABLE_NAME, new String[]{DatabaseContract.personEntry.COLUMN_NAME},
                null, null,
                null, null, null);

        Set<UserData> result = new HashSet<>();

        while (c.moveToNext()) {
            int userNameIndex = c.getColumnIndex("name");
            String retname = c.getString(userNameIndex);
            result.add(new UserData(retname, null));
        }

        friends.clear();
        friends.addAll(result);
        friendsAdapter.notifyDataSetChanged();
    }


    private class PersonAdapter extends ArrayAdapter<UserData> {

        public PersonAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1, friends);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserData friend = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_list_item_1, null);
            }
            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(friend.getLogin());
            return convertView;
        }

//        public void getLogin(View view) {
//            Button btn = (Button) view;
//            String friendLogin = btn.getText().toString();
//            Log.i("ButtonClick", friendLogin);
//        }
    }

}