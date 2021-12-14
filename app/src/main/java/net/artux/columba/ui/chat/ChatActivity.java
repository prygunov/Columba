package net.artux.columba.ui.chat;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import net.artux.columba.Cache;
import net.artux.columba.QRUtil;
import net.artux.columba.R;
import net.artux.columba.Security;
import net.artux.columba.data.model.Channel;
import net.artux.columba.data.model.Message;
import net.artux.columba.data.model.ShareKey;
import net.artux.columba.databinding.ActivityChatBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements MessagesAdapter.MessageClickListener {

    ActivityChatBinding binding;
    private MessagesAdapter adapter;
    private final DatabaseReference reference = FirebaseDatabase.getInstance("https://columba-73cc9-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference()
            .child("messages");

    private final DatabaseReference channelRef = FirebaseDatabase.getInstance("https://columba-73cc9-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference()
            .child("channels");

    private Cache<String> cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cache = new Cache<>(String.class, getApplicationContext(), new Gson());

        if(getIntent().getSerializableExtra("channel") == null)
            finish();

        if (getSupportActionBar()!=null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolbar)));

        adapter = new MessagesAdapter(new ArrayList<>(), this);
        binding.recyclerView.setAdapter(adapter);

        Channel channel = (Channel) getIntent().getSerializableExtra("channel");
        String privateKey = cache.get(channel.getUid());
        if(privateKey==null) {
            Toast.makeText(getApplicationContext(), "Добавьте ключ с другого устройства", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            Security security = new Security(privateKey);
            setTitle("Канал " + channel.getTitle());
            DatabaseReference messageRef = reference.child(channel.getUid());
            messageRef.limitToLast(30).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Message> messages = new ArrayList<>();

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Message value = postSnapshot.getValue(Message.class);
                        messages.add(value);
                    }
                    for (Message m : messages) {
                        try {
                            m.setMessageText(security.decrypt(m.getMessageText()));
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Ошибка ключей, попробуйте снова", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    adapter.setMessages(messages);

                    if (messages.size()>0)
                        binding.recyclerView.smoothScrollToPosition(messages.size() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            binding.textSend.setOnClickListener(v -> binding.recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1));

            binding.btnSend.setOnClickListener(view -> {
                String text = binding.textSend.getText().toString();
                if (!text.equals("")) {
                    try {
                        String nickname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        messageRef.push().setValue(new Message(security.encrypt(text), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                        channel.setLastMessageId(nickname + ": " + text);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    channelRef.child(channel.getUid()).setValue(channel);

                    binding.textSend.setText("");
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        Channel channel = (Channel) getIntent().getSerializableExtra("channel");
        switch (item.getItemId()) {
            case R.id.share_key:
                ImageView image = new ImageView(this);
                String privateKey = cache.get(channel.getUid());
                Gson gson = new Gson();
                image.setImageBitmap(QRUtil.generateQR(gson.toJson(new ShareKey(channel.getUid(), privateKey)), 700));

                builder.setMessage("Сканируйте ключ на другом устройстве").
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).
                                setView(image);
                builder.create().show();
                return true;
            case R.id.change_title:
                EditText editText = new EditText(this);

                editText.setText(channel.getTitle());
                builder.setMessage("Введите новое название канала").
                        setPositiveButton("OK", (dialogInterface, i) -> {
                            String title = editText.getText().toString();
                            if (!title.equals("")) {
                                channel.setTitle(title);
                                setTitle("Канал " + title);
                                channelRef.child(channel.getUid()).setValue(channel);
                            }
                        }).
                        setView(editText);
                builder.create().show();
                return true;
            case R.id.change_icon:
                editText = new EditText(this);
                editText.setText(channel.getIcon());
                builder.setMessage("Введите новую ссылку на изображение").
                        setPositiveButton("OK", (dialogInterface, i) -> {
                            String title = editText.getText().toString();
                            if (!title.equals("")) {
                                channel.setIcon(title);
                                channelRef.child(channel.getUid()).setValue(channel);
                            }
                        }).
                        setView(editText);
                builder.create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void clicked(Message message) {

    }
}