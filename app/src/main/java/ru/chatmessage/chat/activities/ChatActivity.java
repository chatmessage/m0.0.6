package ru.chatmessage.chat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import ru.chatmessage.chat.R;


public class ChatActivity extends AppCompatActivity {

    private FragmentManager manager;
    private String login;
    private String token;
    private String friendLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = getSupportFragmentManager();

        Intent intent = this.getIntent();
        login = intent.getStringExtra("login");
        token = intent.getStringExtra("token");
        friendLogin = intent.getStringExtra("friendLogin");

        ChatFragment chatFragment = new ChatFragment();

        Bundle bundleCompat = new Bundle();
        bundleCompat.putString("login", login);
        bundleCompat.putString("token", token);
        bundleCompat.putString("friendLogin", friendLogin);

        chatFragment.setArguments(bundleCompat);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.container, chatFragment, "chatFragment");
        transaction.commit();

        ImageButton bexit = (ImageButton) findViewById(R.id.bexit);

        bexit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FriendFragment friendFragment = new FriendFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container, friendFragment, "friendFragment");
                transaction.commit();
            }
        });

    }

}