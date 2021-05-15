package com.lokiiichauhan.firebaseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class ViewSentPostsActivity extends AppCompatActivity {

    private ListView postsListView;
    private ArrayList<String> userNames;
    private ArrayList<DataSnapshot> mDataSnapshots;
    private ArrayAdapter mAdapter;
    private FirebaseAuth mFirebaseAuth;

    private ImageView sendPostImageview;
    private TextView txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sent_posts);
        mFirebaseAuth = FirebaseAuth.getInstance();

        postsListView = findViewById(R.id.postslistView);
        userNames = new ArrayList<>();
        mDataSnapshots = new ArrayList<>();
        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,userNames);
        postsListView.setAdapter(mAdapter);

        sendPostImageview= findViewById(R.id.sendPostImageview);
        txtDescription = findViewById(R.id.txtDescription);

        postsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                DataSnapshot myDataSnapshot = mDataSnapshots.get(i);
                String DownloadLInk = myDataSnapshot.child("imageLink").getValue().toString();
                Glide.with(ViewSentPostsActivity.this).load(DownloadLInk).into(sendPostImageview);
                txtDescription.setText(myDataSnapshot.child("des").getValue().toString());
            }
        });

        postsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                new AlertDialog.Builder(ViewSentPostsActivity.this)

                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                FirebaseStorage.getInstance().getReference().child(mDataSnapshots.get(i).child("imageIdentifier").getValue().toString()).delete();
                                FirebaseDatabase.getInstance("https://fir-d07da-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("my_users").child(mFirebaseAuth.getCurrentUser().getUid()).child("received_posts").child(mDataSnapshots.get(i).getKey()).removeValue();

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }
        });

        FirebaseDatabase.getInstance("https://fir-d07da-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("my_users").child(mFirebaseAuth.getCurrentUser().getUid()).child("received_posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull  DataSnapshot snapshot, @Nullable String previousChildName) {

                mDataSnapshots.add(snapshot);
                String fromWhomUsername =(String) snapshot.child("fromWhom").getValue();
                userNames.add(fromWhomUsername);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull  DataSnapshot snapshot, @Nullable String previousChildName) {


            }

            @Override
            public void onChildRemoved(@NonNull  DataSnapshot snapshot) {

                int i = 0;
                for (DataSnapshot snapshot1: mDataSnapshots){

                    if (snapshot1.getKey().equals(snapshot.getKey()));
                    mDataSnapshots.remove(i);
                    userNames.remove(i);
                }
                i++;
                mAdapter.notifyDataSetChanged();
                sendPostImageview.setImageResource(R.drawable.placeholder);
                txtDescription.setText("");

            }

            @Override
            public void onChildMoved(@NonNull  DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

    }
}