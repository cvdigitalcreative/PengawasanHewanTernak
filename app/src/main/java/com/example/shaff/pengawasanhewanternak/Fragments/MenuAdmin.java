package com.example.shaff.pengawasanhewanternak.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shaff.pengawasanhewanternak.LoginActivity;
import com.example.shaff.pengawasanhewanternak.R;
import com.example.shaff.pengawasanhewanternak.ScanActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuAdmin extends Fragment {
    private FirebaseAuth mAuth;
    private Button btnLogout, btnReport, btnRegister;

    public MenuAdmin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_admin, container, false);

        insialisasiKomponen(view);
        reportData();
        registerPengawas();
        keluarAplikasi();

        return view;
    }

    private void insialisasiKomponen(View view) {
        btnReport = view.findViewById(R.id.btn_report_data);
        btnLogout = view.findViewById(R.id.btn_logout_admin);
        btnRegister = view.findViewById(R.id.btn_register);

        mAuth = FirebaseAuth.getInstance();
    }

    private void reportData() {
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScanActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerPengawas() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register register = new Register();

                getFragmentManager().beginTransaction().replace(R.id.container_fragment, register).addToBackStack(null).commit();
            }
        });
    }

    private void keluarAplikasi() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
