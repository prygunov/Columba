package net.artux.columba.ui.main.fragments.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.artux.columba.R;
import net.artux.columba.data.model.Channel;
import net.artux.columba.databinding.ItemChannelBinding;

import java.util.List;

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.ChannelHolder> {

    List<Channel> channels;
    ChannelClickListener listener;
    public ChannelsAdapter(List<Channel> channels, ChannelClickListener listener) {
        this.channels = channels;
        this.listener = listener;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChannelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChannelHolder(ItemChannelBinding.inflate(LayoutInflater.from(parent.getContext())).getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelHolder holder, int position) {
        holder.bind(channels.get(position));
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public class ChannelHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView message;
        public ImageView imageView;

        public ChannelHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.message);
            imageView = itemView.findViewById(R.id.profile_image);
        }

        void bind(Channel channel){
            title.setText(channel.getTitle());
            if (channel.getLastMessageId() != null)
                message.setText(channel.getLastMessageId());
            if (channel.getIcon() != null && !channel.getIcon().equals(""))
                Glide.with(imageView).load(channel.getIcon()).into(imageView);
            itemView.setOnClickListener(view -> listener.clicked(channel));
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.longClicked(channel);
                    return true;
                }
            });
        }
    }

    interface ChannelClickListener{

        void clicked(Channel channel);
        void longClicked(Channel channel);

    }

}
