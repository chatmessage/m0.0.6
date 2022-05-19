package ru.chatmessage.chat.components;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ru.chatmessage.chat.R;
import ru.chatmessage.chat.data.Message;
import ru.chatmessage.chat.data.UserData;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {

    private List<Message> messages = new ArrayList<>();
    private Context context;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);

        if (message.isBelongsToCurrentUser()) {
            convertView = messageInflater.inflate(R.layout.list_mymessage, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_text);
            holder.messageBody.setText(message.getMessage());
            convertView.setTag(holder);
        } else {
            convertView = messageInflater.inflate(R.layout.list_friendmessage, null);

//            holder.name = (TextView) convertView.findViewById(R.id.nameview);
//            holder.name.setText(message.getLogin());
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_textf);
            holder.messageBody.setText(message.getMessage());
            convertView.setTag(holder);
        }

        return convertView;
    }

    private class MessageViewHolder {

        public TextView name;
        public TextView messageBody;
    }
}


