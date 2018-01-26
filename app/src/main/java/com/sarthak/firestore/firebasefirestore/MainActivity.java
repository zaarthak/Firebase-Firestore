package com.sarthak.firestore.firebasefirestore;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mNameEt;
    private Button mSaveBtn;

    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNameEt = findViewById(R.id.input_name);
        mSaveBtn = findViewById(R.id.save_btn);

        mFirestore = FirebaseFirestore.getInstance();

        mSaveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.save_btn:

                String username = mNameEt.getText().toString();

                if (!TextUtils.isEmpty(username)) {

                    Map<String, String> userMap = new HashMap<>();

                    userMap.put("name", username);
                    userMap.put("image", "image_link");

                    mFirestore.collection("Users").add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            Toast.makeText(MainActivity.this, "Username added to Firebase.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(MainActivity.this, "An error occcured.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                mNameEt.setText("");

                break;
        }
    }
}
