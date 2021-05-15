package com.lokiiichauhan.firebaseapp;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText edtEmail, edtUsername, edtPassword;
    private Button btnSignUp, btnSignIn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtUsername = findViewById(R.id.edtUsername);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);

        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();

                btnSignUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        signUp();

                    }
                });
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();

            }
        });
    }

    private void signUp() {

        mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(),
                edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseDatabase.getInstance("https://fir-d07da-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                            .child("my_users").child(task.getResult().getUser().getUid())
                            .child("username")
                            .setValue(edtUsername.getText().toString());

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(edtUsername.getText().toString())
                            .build();

                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Name Updated", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                    toSocialMediaActivity();

                } else {

                    Toast.makeText(MainActivity.this, "SignUp Failed", Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    private void signIn(){

        mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    toSocialMediaActivity();


                }else {

                    Toast.makeText(MainActivity.this, "SignIn Failed", Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    private void toSocialMediaActivity(){
        Intent intent = new Intent(this,SocialMediaActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            toSocialMediaActivity();

        }

    }
}