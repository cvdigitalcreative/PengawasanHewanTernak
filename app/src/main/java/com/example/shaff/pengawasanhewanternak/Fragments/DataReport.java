package com.example.shaff.pengawasanhewanternak.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shaff.pengawasanhewanternak.DataModels.LaporanModel;
import com.example.shaff.pengawasanhewanternak.DataModels.PengawasModel;
import com.example.shaff.pengawasanhewanternak.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataReport extends Fragment {
    private ArrayList<PengawasModel> laporan_list;
    private DatabaseReference laporanReference;
    private RecyclerView myRecyclerView;

    public DataReport() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_report, container, false);

        inisialisasiKomponen(view);

        return view;
    }

    public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.LaporanHolder>{
        private ArrayList<PengawasModel> laporanList;

        public LaporanAdapter(ArrayList<PengawasModel> list) {
            laporanList = list;
        }

        @NonNull
        @Override
        public LaporanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_laporan_ternak, parent, false);

            LaporanHolder holder = new LaporanHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull LaporanHolder holder, final int position) {
            holder.tvNamaPelapor.setText(laporanList.get(position).getUser_name());

            holder.cvLaporanTernak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle sendedData = new Bundle();
                    sendedData.putString("ID_PENGAWAS", laporanList.get(position).getId_pengawas());

                    QRCodeReport qrCodeReport = new QRCodeReport();
                    qrCodeReport.setArguments(sendedData);
                    getFragmentManager().beginTransaction().replace(R.id.container_report, qrCodeReport).addToBackStack(null).commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return laporanList.size();
        }

        public class LaporanHolder extends RecyclerView.ViewHolder{
            TextView tvNamaPelapor;
            CardView cvLaporanTernak;

            public LaporanHolder(@NonNull View itemView) {
                super(itemView);

                tvNamaPelapor = itemView.findViewById(R.id.tv_nama_pelapor);
                cvLaporanTernak = itemView.findViewById(R.id.cv_laporan_item);
            }
        }
    }

    private void inisialisasiKomponen(View view) {
        laporanReference= FirebaseDatabase.getInstance().getReference();
        myRecyclerView = view.findViewById(R.id.rv_data_laporan);
        myRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLinearLayoutManager = new LinearLayoutManager(getActivity());
        inisialisasi_data_laporan();
        myRecyclerView.setLayoutManager(MyLinearLayoutManager);
    }

    public void inisialisasi_data_laporan(){
        laporan_list = new ArrayList<>();

        laporanReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                laporan_list = new ArrayList<>();

                for(DataSnapshot laporanSnapshot : dataSnapshot.child("laporan").getChildren()){
                    for(DataSnapshot pengawasSnapshot : dataSnapshot.child("pengawas").getChildren()){
                        if(laporanSnapshot.getKey().equals(pengawasSnapshot.getKey())){
                            PengawasModel pengawas = pengawasSnapshot.getValue(PengawasModel.class);
                            laporan_list.add(pengawas);
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
