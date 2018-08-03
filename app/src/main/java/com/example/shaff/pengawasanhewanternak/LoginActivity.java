package com.example.shaff.pengawasanhewanternak;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private Button btn_masuk;
    private EditText userName;
    private EditText kataSandi;
    private Spinner sLogin;

    private String login_type;
    private String kata_sandi;
    private String user_name;

    private FirebaseAuth mAuth;
    private DatabaseReference reference, adminRef;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        adminRef = FirebaseDatabase.getInstance().getReference("admin");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    if(user.getEmail().equals("admin@digitalcreative.web.id")){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("USER_NAME", "admin123");
                        startActivity(intent);
                    }
                    else{
                        reference = FirebaseDatabase.getInstance().getReference("pengawas");

                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot pengawasSnapshot : dataSnapshot.getChildren()){
                                    if(user.getEmail().equals(pengawasSnapshot.child("email").getValue().toString())){
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("USER_NAME", pengawasSnapshot.child("user_name").getValue().toString());
                                        intent.putExtra("ID_PENGAWAS", pengawasSnapshot.getKey());
                                        startActivity(intent);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
        };

        klikBtnMasuk();
    }

    private void klikBtnMasuk() {
        mAuth = FirebaseAuth.getInstance();

        userName = findViewById(R.id.username);
        kataSandi = findViewById(R.id.kata_sandi);
        btn_masuk = findViewById(R.id.btn_login);
        sLogin = findViewById(R.id.s_login_type);

        btn_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_name = userName.getText().toString();
                kata_sandi = kataSandi.getText().toString();
                login_type = sLogin.getSelectedItem().toString();

                if(TextUtils.isEmpty(user_name)){
                    Toast.makeText(getApplicationContext(),"Silahkan masukkan username anda!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(kata_sandi)){
                    Toast.makeText(getApplicationContext(),"Silahkan masukkan kata sandi dengan benar!", Toast.LENGTH_SHORT).show();
                    return;
                }

                reference = FirebaseDatabase.getInstance().getReference("pengawas");

                if(login_type.equals("Pengawas")){
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot pengawasSnapshot : dataSnapshot.getChildren()){
                                if(user_name.equals(pengawasSnapshot.child("user_name").getValue().toString())){
                                    String email = pengawasSnapshot.child("email").getValue().toString();
                                    final String id_pengawas = pengawasSnapshot.getKey();

                                    mAuth.signInWithEmailAndPassword(email, kata_sandi)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(getApplicationContext(),"Autentikasi berhasil!", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        intent.putExtra("USER_NAME", user_name);
                                                        intent.putExtra("ID_PENGAWAS", id_pengawas);
                                                        startActivity(intent);
                                                    }else{
                                                        Toast.makeText(getApplicationContext(),"Autentikasi gagal!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println(databaseError.toException());
                        }
                    });
                }
                else{
                    adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshotAdmin : dataSnapshot.getChildren()){
                                if(user_name.equals(snapshotAdmin.getKey())){
                                    String email = snapshotAdmin.child("email").getValue().toString();

                                    mAuth.signInWithEmailAndPassword(email, kata_sandi)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(getApplicationContext(),"Autentikasi berhasil!", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        intent.putExtra("USER_NAME", user_name);
                                                        startActivity(intent);
                                                    }else{
                                                        Toast.makeText(getApplicationContext(),"Autentikasi gagal!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
