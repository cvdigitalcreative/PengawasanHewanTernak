package com.example.shaff.pengawasanhewanternak.Fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.shaff.pengawasanhewanternak.DataModels.LaporanModel;
import com.example.shaff.pengawasanhewanternak.R;
//import com.google.android.gms.vision.Frame;
//import com.google.android.gms.vision.barcode.Barcode;
//import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class QRCodeDetailInfo extends Fragment {
    private TextView tvInfo, tvTanggal, tvLokasiKandang, tvKodeTernak, tvJenisSapi, tvUmur, tvBerat, tvJenisPakan, tvCatatanPenting;
    private ImageView ivQRCode;
    private Button btnScan;
    private String urlQRCode, idPengawas;
    private Bitmap QRCodeIMage;
    private DatabaseReference laporanRef;

    public QRCodeDetailInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_qrcode_detail_info, container, false);

        inisialisasiKomponen(view);

        TampilQRCode();
//        TampilInfoQRCode();

        return view;
    }

    private void inisialisasiKomponen(View view) {
        Bundle receivedData = getArguments();
        idPengawas = receivedData.getString("ID_PENGAWAS");
        urlQRCode = receivedData.getString("URL_QRCODE");

        laporanRef = FirebaseDatabase.getInstance().getReference("laporan");

        tvInfo = view.findViewById(R.id.info);
        tvTanggal = view.findViewById(R.id.tanggal_info);
        tvLokasiKandang = view.findViewById(R.id.lokasi_kandang_info);
        tvKodeTernak = view.findViewById(R.id.kode_ternak_info);
        tvJenisSapi = view.findViewById(R.id.jenis_sapi_info);
        tvUmur = view.findViewById(R.id.umur_info);
        tvBerat = view.findViewById(R.id.berat_info);
        tvJenisPakan = view.findViewById(R.id.jenis_pakan_info);
        tvCatatanPenting = view.findViewById(R.id.catatan_penting_info);

        ivQRCode = view.findViewById(R.id.iv_qrcode_info);
        btnScan = view.findViewById(R.id.btn_scan);
    }

    private void TampilQRCode() {
        Glide.with(getActivity()).load(urlQRCode).into(ivQRCode);

        Glide.with(this).asBitmap().load(urlQRCode)
                .apply(new RequestOptions().override(300, 300))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        QRCodeIMage = resource;

                        return false;
                    }
                })
        .submit();
    }

//    private void TampilInfoQRCode() {
//        btnScan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String id_qrCode = scan_qrCode();
//
//                laporanRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for(DataSnapshot pengawasSnapshot : dataSnapshot.getChildren()){
//                            if(idPengawas.equals(pengawasSnapshot.getKey())){
//                                for(DataSnapshot laporanSnapshot : pengawasSnapshot.getChildren()){
//                                    if(id_qrCode.equals(laporanSnapshot.getKey())){
//                                        tvTanggal.setText("Tanggal : "+laporanSnapshot.child("tanggal_laporan").getValue().toString());
//                                        tvLokasiKandang.setText("Lokasi Kandang : "+laporanSnapshot.child("lokasi_kandang").getValue().toString());
//                                        tvKodeTernak.setText("Kode Ternak : "+laporanSnapshot.child("kode_hewan").getValue().toString());
//                                        tvJenisSapi.setText("Jenis Sapi : "+laporanSnapshot.child("jenis_sapi").getValue().toString());
//                                        tvUmur.setText("Umur : "+laporanSnapshot.child("umur").getValue().toString());
//                                        tvBerat.setText("Berat : "+laporanSnapshot.child("berat").getValue().toString());
//                                        tvJenisPakan.setText("Jenis Pakan : "+laporanSnapshot.child("jenis_pakan").getValue().toString());
//                                        tvCatatanPenting.setText("Catatan Penting : "+laporanSnapshot.child("catatan_penting").getValue().toString());
//
//                                        tvInfo.setVisibility(View.VISIBLE);
//                                        tvTanggal.setVisibility(View.VISIBLE);
//                                        tvLokasiKandang.setVisibility(View.VISIBLE);
//                                        tvKodeTernak.setVisibility(View.VISIBLE);
//                                        tvJenisSapi.setVisibility(View.VISIBLE);
//                                        tvUmur.setVisibility(View.VISIBLE);
//                                        tvBerat.setVisibility(View.VISIBLE);
//                                        tvJenisPakan.setVisibility(View.VISIBLE);
//                                        tvCatatanPenting.setVisibility(View.VISIBLE);
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });
//    }

//    public String scan_qrCode(){
//        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getContext())
//                .setBarcodeFormats(Barcode.QR_CODE).build();
//
//        Frame frame = new Frame.Builder().setBitmap(QRCodeIMage).build();
//
//        SparseArray<Barcode> barsCode = barcodeDetector.detect(frame);
//        Barcode result = barsCode.valueAt(0);
//
//        return result.rawValue;
//    }
}
