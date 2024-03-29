package ru.chatmessage.chat.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Objects;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.chatmessage.chat.R;
import ru.chatmessage.chat.components.MessageAdapter;
import ru.chatmessage.chat.data.Message;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ChatFragment extends Fragment {

    private static final String WS_URL = "ws://192.168.1.123:8080/chat/android";

    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private EditText editText;
    private ImageButton sendMessageBtn;
    private CompositeDisposable compositeDisposable;
    private StompClient stompClient;
    private Gson jsonParser;
    private String login;
    private String token;
    private String friendLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_chat, container, false);

        messagesView = view.findViewById(R.id.messagelist);
        messageAdapter = new MessageAdapter(getContext());
        messagesView.setAdapter(messageAdapter);
        editText = view.findViewById(R.id.textField);
        sendMessageBtn = view.findViewById(R.id.bsend);
        sendMessageBtn.setOnClickListener(v -> onClickSendMessage());

        jsonParser = new Gson();

        Bundle bundle = getArguments();

        login = bundle.getString("login");
        token = bundle.getString("token");
        friendLogin = bundle.getString("friendLogin");

        stompConnection();

        return view;
    }


    private void stompConnection() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL);
        resetSubscriptions();

        compositeDisposable.add(buildLifecycle(stompClient));
        compositeDisposable.add(
                stompClient.topic("/topic/activity")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(topicMessage -> {
                            Log.d("", "Received " + topicMessage.getPayload());
                            outputMessages(jsonParser.fromJson(topicMessage.getPayload(), Message[].class));
                        }, throwable -> {
                            Log.e("", "Error on subscribe topic", throwable);
                            backToPreviousForm();
                        })
        );
        compositeDisposable.add(
                stompClient.send("/app/history",
                                "{\"token\":\"" + token + "\", \"toLogin\":\"" + friendLogin + "}")
                        .compose(applySchedulers())
                        .subscribe(() -> {
                            Log.d("", "STOMP echo send successfully");

                        }, throwable -> {
                            Log.e("", "Error send STOMP echo", throwable);
                            toast(throwable.getMessage());
                        })
        );

        stompClient.connect();
    }

    private Disposable buildLifecycle(StompClient stompClient) {
        return stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            toast("Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e("", "Stomp connection error", lifecycleEvent.getException());
                            toast("Stomp connection error");
                            backToPreviousForm();
                            break;
                        case CLOSED:
                            toast("Stomp connection closed");
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            toast("Stomp failed server heartbeat");
                            backToPreviousForm();
                            break;
                    }
                });
    }

    public void onClickSendMessage() {
        String msg = editText.getText().toString();
        editText.getText().clear();

        if (msg.length() > 0) {
            sendMessage(new Message(login, friendLogin, msg, true));
        }
    }

    private void sendMessage(Message message) {
        compositeDisposable.add(
                stompClient.send("/app/send_message",
                                jsonParser.toJson(message, Message.class))
                        .compose(applySchedulers())
                        .subscribe(() -> {
                            Log.d("", "STOMP echo send successfully");
                            showMessage(message);
                        }, throwable -> {
                            Log.e("", "Error send STOMP echo", throwable);
                            toast(throwable.getMessage());
                        })
        );
    }

    private void outputMessages(Message... messages) {
        getActivity().runOnUiThread(() -> {
            for (Message msg : messages) {
                if (Objects.equals(msg.getFrom(), login) || Objects.equals(msg.getTo(), login)) {
                    msg.checkIsBelongsToCurrentUser(login);
                    if (!msg.isBelongsToCurrentUser()) {
                        showMessage(msg);
                    }
                }
            }
        });
    }

    private void showMessage(Message message) {
        messageAdapter.add(message);
        messagesView.setSelection(messagesView.getCount() - 1);
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    private void backToPreviousForm() {
        getActivity().finish();
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void toast(String text) {
        Log.i("", text);
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        stompClient.disconnect();
        if (compositeDisposable != null)
            compositeDisposable.dispose();
        super.onDestroy();
    }
}