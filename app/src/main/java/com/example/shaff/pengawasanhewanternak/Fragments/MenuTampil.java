package com.example.shaff.pengawasanhewanternak.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shaff.pengawasanhewanternak.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuTampil extends Fragment {
    private Spinner sPeriode;
    private Button btnTampil;
    private String kodeTernak;

    public MenuTampil() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_tampil, container, false);

        inisialisasiKomponen(view);
        TampilkanInformasi();

        return view;
    }

    private void inisialisasiKomponen(View view) {
        Bundle receivedData = getArguments();
        kodeTernak = receivedData.getString("KODE_TERNAK");

        sPeriode = view.findViewById(R.id.s_periode);
        btnTampil = view.findViewById(R.id.btn_tampil_informasi);
    }

    private void TampilkanInformasi() {
        btnTampil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(kodeTernak == null){
                    Toast.makeText(getContext(), "Scan QR Code terlebih dahulu", Toast.LENGTH_LONG).show();
                }
                else{
                    Bundle sendedData = new Bundle();
                    sendedData.putString("KODE_TERNAK", kodeTernak);
                    sendedData.putString("PERIODE", sPeriode.getSelectedItem().toString());

                    ChartHewan chartHewan = new ChartHewan();
                    chartHewan.setArguments(sendedData);
                    getFragmentManager().beginTransaction().replace(R.id.container_report, chartHewan).commit();
                }
            }
        });
    }

}
