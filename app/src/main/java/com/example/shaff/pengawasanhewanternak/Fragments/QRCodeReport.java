package com.example.shaff.pengawasanhewanternak.Fragments;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shaff.pengawasanhewanternak.DataModels.LaporanModel;
import com.example.shaff.pengawasanhewanternak.DataModels.PengawasModel;
import com.example.shaff.pengawasanhewanternak.R;
//import com.google.android.gms.vision.Frame;
//import com.google.android.gms.vision.barcode.Barcode;
//import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class QRCodeReport extends Fragment {
    private ArrayList<LaporanModel> laporan_list;
    private DatabaseReference laporanReference;
    private RecyclerView myRecyclerView;

    private String idPengawas;

    public QRCodeReport() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qrcode_report, container, false);

        inisialisasiKomponen(view);

        return view;
    }

    public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.LaporanHolder>{
        private ArrayList<LaporanModel> laporanList;

        public LaporanAdapter(ArrayList<LaporanModel> list) {
            laporanList = list;
        }

        @NonNull
        @Override
        public LaporanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_laporan_qrcode_ternak, parent, false);

            LaporanHolder holder = new LaporanHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull LaporanHolder holder, final int position) {
            holder.tvKodeTernak.setText(laporanList.get(position).getKode_hewan());
            Glide.with(getActivity()).load(laporanList.get(position).getQR_Code()).into(holder.ivQRCode);
            holder.cvLaporanQRCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                    LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                    View mView = layoutInflater.inflate(R.layout.modal_info_qrcode, null);

                    TextView tvTanggal = mView.findViewById(R.id.tanggal_info);
                    TextView tvLokasi = mView.findViewById(R.id.lokasi_kandang_info);
                    TextView tvKode = mView.findViewById(R.id.kode_ternak_info);
                    TextView tvJenisSapi = mView.findViewById(R.id.jenis_sapi_info);
                    TextView tvUmur = mView.findViewById(R.id.umur_info);
                    TextView tvBerat = mView.findViewById(R.id.berat_info);
                    TextView tvJenisPakan = mView.findViewById(R.id.jenis_pakan_info);
                    TextView tvCatatanPenting = mView.findViewById(R.id.catatan_penting_info);
                    Button btnKembali = mView.findViewById(R.id.btn_kembali);

                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();

                    tvTanggal.setText("Tanggal : "+laporan_list.get(position).getTanggal_laporan());
                    tvLokasi.setText("Lokasi Kandang : "+laporan_list.get(position).getLokasi_kandang());
                    tvKode.setText("Kode Ternak : "+laporan_list.get(position).getKode_hewan());
                    tvJenisSapi.setText("Jenis Sapi : "+laporan_list.get(position).getJenis_sapi());
                    tvUmur.setText("Umur : "+String.valueOf(laporan_list.get(position).getUmur()));
                    tvBerat.setText("Berat : "+String.valueOf(laporan_list.get(position).getBerat()));
                    tvJenisPakan.setText("Jenis Pakan : "+laporan_list.get(position).getJenis_pakan());
                    tvCatatanPenting.setText("Catatan Penting : "+laporan_list.get(position).getCatatan_penting());

                    dialog.show();
                    dialog.setCanceledOnTouchOutside(false);

                    btnKembali.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return laporanList.size();
        }

        public class LaporanHolder extends RecyclerView.ViewHolder{
            TextView tvKodeTernak;
            ImageView ivQRCode;
            CardView cvLaporanQRCode;

            public LaporanHolder(@NonNull View itemView) {
                super(itemView);

                tvKodeTernak = itemView.findViewById(R.id.tv_kode_hewan);
                ivQRCode = itemView.findViewById(R.id.iv_qr_code);
                cvLaporanQRCode = itemView.findViewById(R.id.cv_laporan_qrcode);
            }
        }
    }

    private void inisialisasiKomponen(View view) {
        laporanReference= FirebaseDatabase.getInstance().getReference("laporan");

        Bundle receivedData = getArguments();
        idPengawas = receivedData.getString("ID_PENGAWAS");

        myRecyclerView = view.findViewById(R.id.rv_laporan_qrCode);
        myRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLinearLayoutManager = new LinearLayoutManager(getActivity());
        inisialisasi_laporan_qrcode();
        myRecyclerView.setLayoutManager(MyLinearLayoutManager);
    }

    public void inisialisasi_laporan_qrcode(){
        laporan_list = new ArrayList<>();

        laporanReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                laporan_list = new ArrayList<>();

                for(DataSnapshot laporanSnapshot : dataSnapshot.getChildren()){
                    if(idPengawas.equals(laporanSnapshot.getKey())){
                        for(DataSnapshot qrCodeSnapshot : laporanSnapshot.getChildren()){
                            LaporanModel laporan = qrCodeSnapshot.getValue(LaporanModel.class);
                            laporan_list.add(laporan);
                        }
                    }
                }

                myRecyclerView.setAdapter(new LaporanAdapter(laporan_list));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
