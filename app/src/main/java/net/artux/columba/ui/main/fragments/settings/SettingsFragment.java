package net.artux.columba.ui.main.fragments.settings;

import static net.artux.columba.QRUtil.generateQR;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import net.artux.columba.Cache;
import net.artux.columba.Security;
import net.artux.columba.data.model.ChannelStatus;
import net.artux.columba.data.model.Request;
import net.artux.columba.databinding.FragmentSettingsBinding;
import net.artux.columba.ui.ScannerActivity;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    private final DatabaseReference referenceStatuses = FirebaseDatabase.getInstance("https://columba-73cc9-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference()
            .child("statuses");
    private Cache<String> cache;
    Request request;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cache = new Cache<>(String.class, requireActivity().getApplicationContext(), new Gson());

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (FirebaseAuth.getInstance().getCurrentUser()!=null)
            binding.nickname.setText("Вы вошли как: " + FirebaseAuth.getInstance().getCurrentUser().getEmail());

        String key = Security.generatePrivateKey();

        Request request = new Request(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseAuth.getInstance().getCurrentUser().getUid(), key);

        binding.qrCode.setImageBitmap(generateQR(new Gson().toJson(request), 700));

        Button buttonSignOut = binding.buttonSignOut;
        buttonSignOut.setOnClickListener(view -> AuthUI.getInstance().signOut(requireContext())
                .addOnCompleteListener(task -> {
                    Toast.makeText(requireActivity(),
                            "You have been signed out.",
                            Toast.LENGTH_LONG)
                            .show();

                    requireActivity().finish();
                }));
        binding.buttonScan.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), ScannerActivity.class);
            startActivity(intent);
        });

        referenceStatuses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    ChannelStatus value = postSnapshot.getValue(ChannelStatus.class);
                    if (value!=null)
                        if (value.getUserId().equals(request.getUid())){
                            cache.put(value.getChannelId(), request.getPrivateKey());
                            referenceStatuses.child(postSnapshot.getKey()).removeValue();
                            // TODO вылетает
                            try {
                                Toast.makeText(requireContext(), "Создан канал с пользователем " + value.getUsername(), Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            updateQR();
                            break;
                        }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return root;
    }

    void updateQR(){
        String key = Security.generatePrivateKey();

        request = new Request(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), FirebaseAuth.getInstance().getCurrentUser().getUid(), key);
        binding.qrCode.setImageBitmap(generateQR(new Gson().toJson(request), 700));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}