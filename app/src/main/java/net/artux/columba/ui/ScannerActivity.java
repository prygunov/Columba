package net.artux.columba.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.zxing.Result;

import net.artux.columba.Cache;
import net.artux.columba.data.model.Channel;
import net.artux.columba.data.model.ChannelStatus;
import net.artux.columba.data.model.Request;
import net.artux.columba.data.model.ShareKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    private DatabaseReference reference = FirebaseDatabase.getInstance("https://columba-73cc9-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference()
            .child("channels");

    private DatabaseReference referenceStatuses = FirebaseDatabase.getInstance("https://columba-73cc9-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference()
            .child("statuses");
    private Cache<String> cache;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        cache = new Cache<>(String.class, this, new Gson());
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Gson gson = new Gson();
        try {
            Request request = gson.fromJson(rawResult.getText(), Request.class);

            if (request.getNickname() == null)
                throw new Exception();

            String title = request.getNickname() + " и " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            List<String> userId = new ArrayList<>();
            userId.add(FirebaseAuth.getInstance().getUid());
            userId.add(request.getUid());
            String uid = reference.push().getKey();

            if (uid != null) {
                reference.child(uid).setValue(new Channel(uid, title, userId));

                cache.put(uid, request.getPrivateKey());
                referenceStatuses.push().setValue(new ChannelStatus(uid, request.getUid(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                Toast.makeText(ScannerActivity.this, "Канал создан", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (Exception exception){
            try {
                ShareKey shareKey = gson.fromJson(rawResult.getText(), ShareKey.class);
                reference.child(shareKey.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Channel channel = snapshot.getValue(Channel.class);
                        if (channel!=null) {
                            if (!channel.getUsersUIDs().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                List<String> ids = channel.getUsersUIDs();
                                ids.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                channel.setUsersUIDs(ids);
                                reference.child(shareKey.getUid()).setValue(channel);
                            } else {
                                //TODO отключить надо после этого
                                cache.put(shareKey.getUid(), shareKey.getPrivateKey());
                                Toast.makeText(ScannerActivity.this, "Ключ канала добавлен", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        }else
                            Toast.makeText(ScannerActivity.this, "Что-то пошло не так, попробуйте снова", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }catch (Exception e){
                Toast.makeText(this, "Чел, ты хуйню сканировал", Toast.LENGTH_SHORT).show();
                mScannerView.resumeCameraPreview(this);
                exception.printStackTrace();
            }
        }

        Intent intent = new Intent();
        intent.putExtra("qr", rawResult.getText());
        setResult(RESULT_OK, intent);
    }
}