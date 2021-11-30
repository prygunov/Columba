package net.artux.columba.ui.main.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import net.artux.columba.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.nickname.setText("Вы вошли как: " + FirebaseAuth.getInstance().getCurrentUser().getEmail());

        Button buttonSignOut = binding.buttonSignOut;
        buttonSignOut.setOnClickListener(view -> AuthUI.getInstance().signOut(requireContext())
                .addOnCompleteListener(task -> {
                    Toast.makeText(requireActivity(),
                            "You have been signed out.",
                            Toast.LENGTH_LONG)
                            .show();

                    requireActivity().finish();
                }));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}