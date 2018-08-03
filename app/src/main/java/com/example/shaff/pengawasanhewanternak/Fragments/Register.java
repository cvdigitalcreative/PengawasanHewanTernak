package com.example.shaff.pengawasanhewanternak.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shaff.pengawasanhewanternak.DataModels.PengawasModel;
import com.example.shaff.pengawasanhewanternak.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class Register extends Fragment {
    private Button btnRegister;
    private EditText etUserName, etEmail, etPassword;

    private FirebaseAuth mAuth;
    private DatabaseReference pengawasRef;

    public Register() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        insialisasiKomponen(view);
        registerPengawas();

        return view;
    }

    private void insialisasiKomponen(View view) {
        etUserName = view.findViewById(R.id.username_register);
        etEmail = view.findViewById(R.id.email_register);
        etPassword = view.findViewById(R.id.kata_sandi_register);
        btnRegister = view.findViewById(R.id.btn_Register);

        mAuth = FirebaseAuth.getInstance();
        pengawasRef = FirebaseDatabase.getInstance().getReference("pengawas");
    }

    private void registerPengawas() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user_name = etUserName.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                if(TextUtils.isEmpty(user_name)){
                    Toast.makeText(getContext(),"Silahkan masukkan username anda!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getContext(),"Silahkan masukkan username anda!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getContext(),"Silahkan masukkan kata sandi dengan benar!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    String key = pengawasRef.push().getKey();

                                    PengawasModel pengawas = new PengawasModel();
                                    pengawas.setId_pengawas(key);
                                    pengawas.setUser_name(user_name);
                                    pengawas.setEmail(email);
                                    pengawas.setPassword(password);

                                    pengawasRef.child(key).setValue(pengawas);
                                    Toast.makeText(getContext(),"Register Berhasil!", Toast.LENGTH_SHORT).show();
                                }
                                else{

                                }
                            }
                        });
            }
        });
    }
}
