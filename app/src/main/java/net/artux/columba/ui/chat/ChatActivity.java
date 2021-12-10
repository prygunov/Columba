package net.artux.columba.ui.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.artux.columba.data.model.Channel;
import net.artux.columba.data.model.Message;
import net.artux.columba.databinding.ActivityChatBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements MessagesAdapter.MessageClickListener {

    ActivityChatBinding binding;
    private MessagesAdapter adapter;
    private DatabaseReference reference = FirebaseDatabase.getInstance("https://columba-73cc9-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference()
            .child("messages");

    private DatabaseReference channelRef = FirebaseDatabase.getInstance("https://columba-73cc9-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference()
            .child("channels");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(getIntent().getSerializableExtra("channel") == null)
            finish();



        adapter = new MessagesAdapter(new ArrayList<>(), this);
        binding.recyclerView.setAdapter(adapter);

        Channel channel = (Channel) getIntent().getSerializableExtra("channel");
        setTitle("Канал " + channel.getTitle());
        DatabaseReference messageRef = reference.child(channel.getUid());
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Message> messages = new ArrayList<>();

                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Message value = postSnapshot.getValue(Message.class);
                    messages.add(value);
                }
                adapter.setMessages(messages);
                binding.recyclerView.smoothScrollToPosition(messages.size()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.textSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
            }
        });

        binding.btnSend.setOnClickListener(view -> {
            String text = binding.textSend.getText().toString();
            if (!text.equals("")) {
                messageRef.push().setValue(new Message(text, FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                channel.setLastMessage(text);
                channelRef.child(channel.getUid()).setValue(channel);
                binding.textSend.setText("");
            }
        });

    }

    @Override
    public void clicked(Message message) {

    }
}