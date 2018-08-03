package com.example.shaff.pengawasanhewanternak;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shaff.pengawasanhewanternak.Fragments.ChartHewan;
import com.example.shaff.pengawasanhewanternak.Fragments.MenuTampil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanActivity extends AppCompatActivity {
    private IntentIntegrator qrScan;
    private Button btnTampil;
    private String kodeTernak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        insialisasiKomponen();
        scanQR();
        tampilInfo();
    }

    private void insialisasiKomponen() {
        btnTampil= findViewById(R.id.btn_tampil_info);
    }

    private void scanQR() {
        qrScan = new IntentIntegrator(ScanActivity.this);
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        qrScan.setPrompt("Scanning QR Code");
        qrScan.setCameraId(0);
        qrScan.initiateScan();
    }

    private void tampilInfo() {
        btnTampil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle sendedData = new Bundle();
                sendedData.putString("KODE_TERNAK", kodeTernak);

                MenuTampil menuTampil = new MenuTampil();
                menuTampil.setArguments(sendedData);

                if(getSupportFragmentManager().findFragmentById(R.id.container_report) != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_report, menuTampil).commit();
                }
                else{
                    getSupportFragmentManager().beginTransaction().add(R.id.container_report, menuTampil).commit();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getApplicationContext(), "QR Code Kosong", Toast.LENGTH_LONG).show();
            } else {
                kodeTernak = result.getContents();
            }
        } else {
            Toast.makeText(getApplicationContext(), "QR Code Gagal", Toast.LENGTH_LONG).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
