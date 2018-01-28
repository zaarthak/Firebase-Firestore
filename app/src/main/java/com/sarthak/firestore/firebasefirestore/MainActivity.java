package com.sarthak.firestore.firebasefirestore;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentListenOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryListenOptions;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> userList = new ArrayList<>();

    private TextView mWelcomeText;
    private EditText mNameEt;
    private Button mSaveBtn, mLoadBtn;

    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWelcomeText = findViewById(R.id.app_title);
        mNameEt = findViewById(R.id.input_name);
        mSaveBtn = findViewById(R.id.save_btn);
        mLoadBtn = findViewById(R.id.load_btn);

        mFirestore = FirebaseFirestore.getInstance();

        mSaveBtn.setOnClickListener(this);
        mLoadBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        QueryListenOptions listenOptions = new QueryListenOptions();
        listenOptions.includeQueryMetadataChanges();

        mFirestore.collection("Users").orderBy("timestamp").addSnapshotListener(this, listenOptions, new EventListener<QuerySnapshot>() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onEvent(QuerySnapshot documentSnapshot, FirebaseFirestoreException e) {

                for (DocumentSnapshot snapshot : documentSnapshot.getDocuments()) {

                    userList.add(0, snapshot.getString("name"));
                    mWelcomeText.setText(String.format("Welcome %s", userList.get(0)));

                    if (snapshot.getMetadata().hasPendingWrites()) {

                        mWelcomeText.setTextColor(getColor(R.color.textColorLight));
                    } else {

                        mWelcomeText.setTextColor(getColor(R.color.textColorDark));
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.save_btn:

                String username = mNameEt.getText().toString();
                String timeStamp = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime());

                if (!TextUtils.isEmpty(username)) {

                    Map<String, String> userMap = new HashMap<>();

                    userMap.put("name", username);
                    userMap.put("timestamp", timeStamp);
                    userMap.put("image", "image_link");

                    mFirestore.collection("Users").add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            Toast.makeText(MainActivity.this, "Username added to Firebase.", Toast.LENGTH_SHORT).show();
                            mNameEt.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(MainActivity.this, "An error occcured.", Toast.LENGTH_SHORT).show();
                            mNameEt.setText("");
                        }
                    });
                }

                break;

            case R.id.load_btn:

                mFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            QuerySnapshot querySnapshot = task.getResult();

                            for (DocumentSnapshot snapshot : querySnapshot) {

                                Log.d("TAG",snapshot.getId() + " => " + snapshot.getData());
                            }
                        }
                    }
                });
        }
    }
}
