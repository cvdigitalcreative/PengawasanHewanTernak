package com.example.shaff.pengawasanhewanternak.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.shaff.pengawasanhewanternak.DataModels.LaporanModel;
import com.example.shaff.pengawasanhewanternak.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 */
public class ChartHewan extends Fragment {
    private BarChart barChart;
    private Button btnUnduh, btnLihatDetail;
    private DatabaseReference laporanRef;
    private String kodeTernak, jenisPeriode;
    private ArrayList<LaporanModel> laporan_list;
    private double[] periode;
    private int i;

    public ChartHewan() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart_hewan, container, false);

        inisialisasiKomponen(view);
        unduhLaporan();
        lihatDetailLaporan();
        tampilInfoChart();

        return view;
    }

    public void inisialisasiKomponen(View view){
        laporanRef = FirebaseDatabase.getInstance().getReference("laporan");

        btnUnduh = view.findViewById(R.id.btn_unduh);
        btnLihatDetail = view.findViewById(R.id.btn_lihat_detail);
        barChart = view.findViewById(R.id.bar_chart);

        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);

        Bundle receivedData = getArguments();
        kodeTernak = receivedData.getString("KODE_TERNAK");
        jenisPeriode = receivedData.getString("PERIODE");
    }

    public void showTernakBarChart(double[] berat_infos, String jenis_periode){
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for(i=0; i<berat_infos.length; i++){
            barEntries.add(new BarEntry((i+1),(int)berat_infos[i]));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Berat");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setDrawValues(true);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);
        barChart.setData(barData);
        barChart.invalidate();

        String[] bulan_list;
        if(jenis_periode.equals("Periode 1")){
            bulan_list = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun"};
        }
        else{
            bulan_list = new String[]{"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        }

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new XAxisValueFormatter(bulan_list));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    public class XAxisValueFormatter implements IAxisValueFormatter{
        private String[] mValues;

        public XAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int)value-1];
        }
    }

    private void unduhLaporan() {
        btnUnduh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String file_name = kodeTernak+"_"+jenisPeriode;
                if (checkPermissionWRITE_EXTERNAL_STORAGE(getContext())) {
                    saveExcelFile(getContext(), file_name, periode);
                }
            }
        });
    }

    private void lihatDetailLaporan() {
        btnLihatDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataReport report = new DataReport();

                getFragmentManager().beginTransaction().replace(R.id.container_report, report).commit();
            }
        });
    }

    public void tampilInfoChart(){
        laporan_list = new ArrayList<>();
        laporanRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                laporan_list = new ArrayList<>();

                for(DataSnapshot pengawasSnapshot : dataSnapshot.getChildren()){
                    for(DataSnapshot laporanSnapshot : pengawasSnapshot.getChildren()){
                        if(kodeTernak.equals(laporanSnapshot.child("kode_hewan").getValue().toString())){
                            LaporanModel laporan = new LaporanModel();
                            laporan.setId_laporan(laporanSnapshot.child("id_laporan").getValue().toString());
                            laporan.setTanggal_laporan(laporanSnapshot.child("tanggal_laporan").getValue().toString());
                            laporan.setLokasi_kandang(laporanSnapshot.child("lokasi_kandang").getValue().toString());
                            laporan.setKode_hewan(laporanSnapshot.child("kode_hewan").getValue().toString());
                            laporan.setJenis_sapi(laporanSnapshot.child("jenis_sapi").getValue().toString());
                            laporan.setUmur(Integer.parseInt(laporanSnapshot.child("umur").getValue().toString()));
                            laporan.setBerat(Integer.parseInt(laporanSnapshot.child("berat").getValue().toString()));
                            laporan.setJenis_pakan(laporanSnapshot.child("jenis_pakan").getValue().toString());
                            laporan.setCatatan_penting(laporanSnapshot.child("catatan_penting").getValue().toString());
                            laporan.setQR_Code(laporanSnapshot.child("qr_Code").getValue().toString());

                            laporan_list.add(laporan);
                        }
                    }
                }

                double[] beratTernak = new double[12];
                int[] nBeratTernak = new int[12];

                for(i=0; i<beratTernak.length; i++){
                    beratTernak[i] = 0;
                    nBeratTernak[i] = 0;
                }

                for(LaporanModel laporanModel : laporan_list){
                    String[] bulan = laporanModel.getTanggal_laporan().split("-");

                    if(bulan[1].equals("01")){
                        beratTernak[0] += laporanModel.getBerat();
                        nBeratTernak[0]++;
                    }
                    else if(bulan[1].equals("02")){
                        beratTernak[1] += laporanModel.getBerat();
                        nBeratTernak[1]++;
                    }
                    else if(bulan[1].equals("03")){
                        beratTernak[2] += laporanModel.getBerat();
                        nBeratTernak[2]++;
                    }
                    else if(bulan[1].equals("04")){
                        beratTernak[3] += laporanModel.getBerat();
                        nBeratTernak[3]++;
                    }
                    else if(bulan[1].equals("05")){
                        beratTernak[4] += laporanModel.getBerat();
                        nBeratTernak[4]++;
                    }
                    else if(bulan[1].equals("06")){
                        beratTernak[5] += laporanModel.getBerat();
                        nBeratTernak[5]++;
                    }
                    else if(bulan[1].equals("07")){
                        beratTernak[6] += laporanModel.getBerat();
                        nBeratTernak[6]++;
                    }
                    else if(bulan[1].equals("08")){
                        beratTernak[7] += laporanModel.getBerat();
                        nBeratTernak[7]++;
                    }
                    else if(bulan[1].equals("09")){
                        beratTernak[8] += laporanModel.getBerat();
                        nBeratTernak[8]++;
                    }
                    else if(bulan[1].equals("10")){
                        beratTernak[9] += laporanModel.getBerat();
                        nBeratTernak[9]++;
                    }
                    else if(bulan[1].equals("11")){
                        beratTernak[10] += laporanModel.getBerat();
                        nBeratTernak[10]++;
                    }
                    else{
                        beratTernak[11] += laporanModel.getBerat();
                        nBeratTernak[11]++;
                    }
                }

                for(i=0; i<beratTernak.length; i++){
                    beratTernak[i] = beratTernak[i]/(double)nBeratTernak[i];
                }

                periode = new double[6];
                if(jenisPeriode.equals("Periode 1")){
                    for(i=0; i<6; i++){
                        periode[i] = beratTernak[i];
                    }
                }
                else{
                    for(i=0; i<6; i++){
                        periode[i] = beratTernak[i+6];
                    }
                }

                showTernakBarChart(periode, jenisPeriode);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private boolean saveExcelFile(Context context, String fileName, double[] new_periode) {
        int baris = 0;
        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("Laporan Perkembangan Hewan Ternak");

        // Generate column headings

        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Kode_Ternak");

        if(jenisPeriode.equals("Periode 1")){
            c = row.createCell(1);
            c.setCellValue("Januari");

            c = row.createCell(2);
            c.setCellValue("Februari");

            c = row.createCell(3);
            c.setCellValue("Maret");

            c = row.createCell(4);
            c.setCellValue("April");

            c = row.createCell(5);
            c.setCellValue("Mei");

            c = row.createCell(6);
            c.setCellValue("Juni");
        }
        else{
            c = row.createCell(1);
            c.setCellValue("Juli");

            c = row.createCell(2);
            c.setCellValue("Agustus");

            c = row.createCell(3);
            c.setCellValue("September");

            c = row.createCell(4);
            c.setCellValue("Oktober");

            c = row.createCell(5);
            c.setCellValue("November");

            c = row.createCell(6);
            c.setCellValue("Desember");
        }

        sheet1.setColumnWidth(0, (8 * 500));
        sheet1.setColumnWidth(1, (8 * 500));
        sheet1.setColumnWidth(2, (15 * 500));
        sheet1.setColumnWidth(3, (15 * 500));
        sheet1.setColumnWidth(4, (15 * 500));
        sheet1.setColumnWidth(5, (15 * 500));
        sheet1.setColumnWidth(6, (15 * 500));

        row = sheet1.createRow(1);

        c = row.createCell(0);
        c.setCellValue(kodeTernak);

        c = row.createCell(1);
        c.setCellValue(new_periode[0]);

        c = row.createCell(2);
        c.setCellValue(new_periode[1]);

        c = row.createCell(3);
        c.setCellValue(new_periode[2]);

        c = row.createCell(4);
        c.setCellValue(new_periode[3]);

        c = row.createCell(5);
        c.setCellValue(new_periode[4]);

        c = row.createCell(6);
        c.setCellValue(new_periode[5]);


        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName+".xls");
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }

        return true;
    }

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionWRITE_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(getContext(), "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
}
