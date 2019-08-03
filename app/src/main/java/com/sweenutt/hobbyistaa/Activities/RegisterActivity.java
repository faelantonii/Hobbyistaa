package com.sweenutt.hobbyistaa.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.sweenutt.hobbyistaa.R;

public class RegisterActivity extends AppCompatActivity {
    TextView textView;

    private EditText userEmail, userName, userPassword, userPassword2;
    private Button btnReqister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        textView = (TextView) findViewById(R.id.textLogin);

        userEmail = findViewById(R.id.email);
        userName =findViewById(R.id.namapengguna);
        userPassword = findViewById(R.id.password1);
        userPassword2 = findViewById(R.id.password2);

        btnReqister = findViewById(R.id.btn_register);

        mAuth = FirebaseAuth.getInstance();

        btnReqister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = userEmail.getText().toString();
                final String namapengguna = userName.getText().toString();
                final String password1 = userPassword.getText().toString();
                final String password2 = userPassword2.getText().toString();

                if (email.isEmpty() || namapengguna.isEmpty() || password1.isEmpty() || !password1.equals(password2)) {
                    
                    showMessage("Periksa lagi form nya");
                }
                else{
                    CreateUserAccount(email,namapengguna,password1);
                }

            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void CreateUserAccount(String email, final String namapengguna, String password1) {

        mAuth.createUserWithEmailAndPassword(email,password1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            showMessage("Berhasil daftar");
                            updateUserInfo(namapengguna, mAuth.getCurrentUser());
                        }
                        else {
                            showMessage("Gagal mendaftar" + task.getException());
                        }
                    }
                });


    }

        private void updateUserInfo(String namapengguna, FirebaseUser currentUser) {

            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(namapengguna)
                    .build();

            currentUser.updateProfile(profileUpdate)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                showMessage("Berhasil mendaftar");
                                updateUI();
                            }
                        }
                    });
    }

    private void updateUI() {

        Intent homeActivity = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(homeActivity);
        finish();
    }

    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            updateUI();
        }
    }
}
