package com.example.shaff.pengawasanhewanternak.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.shaff.pengawasanhewanternak.LoginActivity;
import com.example.shaff.pengawasanhewanternak.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuPengawas extends Fragment {
    private FirebaseAuth mAuth;

    private Button btnInputData, btnLogout;
    private String idPengawas;

    public MenuPengawas() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_pengawas, container, false);

        inisialisasiKomponen(view);
        inputData();
        keluarAplikasi();

        return view;
    }

    private void inisialisasiKomponen(View view) {
        Bundle receivedData = getArguments();
        idPengawas = receivedData.getString("ID_PENGAWAS");

        btnInputData = view.findViewById(R.id.btn_input_data);
        btnLogout = view.findViewById(R.id.btn_logout_pengawas);
        mAuth = FirebaseAuth.getInstance();
    }

    private void inputData() {
        btnInputData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle sendedData = new Bundle();
                sendedData.putString("ID_PENGAWAS", idPengawas);

                InputData inputData = new InputData();
                inputData.setArguments(sendedData);
                getFragmentManager().beginTransaction().replace(R.id.container_fragment, inputData).addToBackStack(null).commit();
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
