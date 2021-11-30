package net.artux.columba.ui.main.fragments.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.artux.columba.data.model.Channel;
import net.artux.columba.data.model.Channels;
import net.artux.columba.databinding.FragmentHomeBinding;
import net.artux.columba.ui.chat.ChatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment implements ChannelsAdapter.ChannelClickListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private ChannelsAdapter adapter;
    private DatabaseReference reference = FirebaseDatabase.getInstance("https://columba-73cc9-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference()
            .child("channels");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = binding.channelsView;
        adapter = new ChannelsAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Channel> channels = new ArrayList<>();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Channel value = postSnapshot.getValue(Channel.class);
                    channels.add(value);
                }
                adapter.setChannels(channels);
                binding.channelsView.setVisibility(View.VISIBLE);
                binding.empty.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                final EditText edittext = new EditText(requireContext());
                alert.setTitle("Enter title of channel");

                alert.setView(edittext);

                alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String title = edittext.getText().toString();
                        List<String> userId = Collections.singletonList(FirebaseAuth.getInstance().getUid());
                        String uid = reference.push().getKey();
                        reference.child(uid).setValue(new Channel(uid, title, userId));

                    }
                });

                alert.setNegativeButton("Cancel", (dialog, whichButton) -> { });

                alert.show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void clicked(Channel channel) {
        Intent intent = new Intent(requireActivity(), ChatActivity.class);
        intent.putExtra("channel", channel);
        startActivity(intent);
    }
}