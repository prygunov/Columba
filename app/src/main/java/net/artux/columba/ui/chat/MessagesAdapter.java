package net.artux.columba.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.columba.R;
import net.artux.columba.data.model.Channel;
import net.artux.columba.data.model.Message;
import net.artux.columba.databinding.ItemChannelBinding;

import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageHolder> {


    List<Message> messages;
    MessageClickListener listener;

    public MessagesAdapter(List<Message> messages, MessageClickListener listener) {
        this.messages = messages;
        this.listener = listener;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageHolder(ItemChannelBinding.inflate(LayoutInflater.from(parent.getContext())).getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView message;
        public TextView time;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }

        void bind(Message messageDto){
            title.setText(messageDto.getMessageUser());
            message.setText(messageDto.getMessageText());
            time.setText(new Date(messageDto.getTs()).toString());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.clicked(messageDto);
                }
            });
        }
    }

    interface MessageClickListener{

        void clicked(Message message);

    }

}
