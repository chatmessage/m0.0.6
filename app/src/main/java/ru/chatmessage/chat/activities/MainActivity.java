package ru.chatmessage.chat.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import ru.chatmessage.chat.R;
import ru.chatmessage.chat.data.UserData;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final String AUTH_URL = "http://192.168.1.123:8080/login";
    private EditText editText;
    private TextView tokenTextView;
    public Fragment friendfragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_email_code);
        editText = findViewById(R.id.editLogin);
        tokenTextView = findViewById(R.id.tokenTextView);

//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction transition = fragmentManager.beginTransaction();
//        friendfragment = new FriendFragment();
//        transition.add(R.id.friendframe, friendfragment, "add fragment");
//        transition.commit();
    }




    public void onLoginClick(View view) {
        String login = editText.getText().toString();
        if (login.length() > 0) {
            tokenTextView.setText(R.string.token_expected_label);
            HttpRequest req = new HttpRequest();
            req.execute(AUTH_URL, login);
        }
    }

    private void openDialog(UserData data) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("login", data.getLogin());
        intent.putExtra("token", data.getToken());
        startActivity(intent);
    }

    private class HttpRequest extends AsyncTask<String, Void, UserData> {

        @Override
        protected UserData doInBackground(String... params) {
            return getUserData(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(UserData data) {
            super.onPostExecute(data);
            if (data != null) {
                tokenTextView.setText(R.string.token_success_label);
                openDialog(data);
            } else {
                tokenTextView.setText(R.string.token_failure_label);
            }
        }

        private UserData getUserData(String url, String login) {
            OkHttpClient httpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(JSON, login))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return new Gson().fromJson(response.body().string(), UserData.class);
                }
            } catch (Exception e) {
                Log.e("", e.toString(), e);
            }
            return null;
        }

    }
}
