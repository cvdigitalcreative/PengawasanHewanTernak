package com.example.shaff.pengawasanhewanternak.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shaff.pengawasanhewanternak.DataModels.LaporanModel;
import com.example.shaff.pengawasanhewanternak.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class InputData extends Fragment {
    private Button btn_kirim;
    private EditText etTanggal, etLokasiKandang, etKodeHewan, etUmur, etBerat, etCatatan;
    private Spinner sJenisPakan, sJenisSapi;
    private Bitmap bitmap;
    private Uri uri;
    private StorageReference storageRef, mStorageRef;
    private DatabaseReference laporanRef;

    private String idPengawas;

    public InputData() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input_data, container, false);

        inisialisasiKomponen(view);
        inisialisasiTanggal();
        kirimData();

        return view;
    }

    private void inisialisasiKomponen(View view) {
        etTanggal = view.findViewById(R.id.tanggal);
        etLokasiKandang = view.findViewById(R.id.lokasi_kandang);
        etKodeHewan = view.findViewById(R.id.kode_hewan);
        etUmur = view.findViewById(R.id.umur);
        etBerat = view.findViewById(R.id.berat);
        etCatatan = view.findViewById(R.id.catatan_penting);

        sJenisSapi = view.findViewById(R.id.jenis_sapi);
        sJenisPakan = view.findViewById(R.id.jenis_pakan);

        btn_kirim = view.findViewById(R.id.btn_kirim);

        Bundle receivedData = getArguments();
        idPengawas = receivedData.getString("ID_PENGAWAS");

        mStorageRef = FirebaseStorage.getInstance().getReference();
        laporanRef = FirebaseDatabase.getInstance().getReference("laporan");
    }

    private void inisialisasiTanggal() {
        String Currentdate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        etTanggal.setText(Currentdate);
    }

    private void kirimData() {
        btn_kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String key = laporanRef.push().getKey();
                final String tanggal = etTanggal.getText().toString();
                final String lokasi_kandang = etLokasiKandang.getText().toString();
                final String kode_hewan = etKodeHewan.getText().toString();
                final String jenis_sapi = sJenisSapi.getSelectedItem().toString();
                final int umur = Integer.parseInt(etUmur.getText().toString());
                final int berat = Integer.parseInt(etBerat.getText().toString());
                final String jenis_pakan = sJenisPakan.getSelectedItem().toString();
                final String catatan_penting = etCatatan.getText().toString();

                try {
                    String qr_file = kode_hewan+"_"+tanggal;
                    bitmap = TextToImageEncode(kode_hewan);
                    saveImage(bitmap, qr_file);
                    uri = getImageUri(bitmap);

                    storageRef = mStorageRef.child("QR Code/").child(qr_file);
                    storageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            LaporanModel laporan = new LaporanModel();
                            laporan.setId_laporan(key);
                            laporan.setTanggal_laporan(tanggal);
                            laporan.setLokasi_kandang(lokasi_kandang);
                            laporan.setKode_hewan(kode_hewan);
                            laporan.setJenis_sapi(jenis_sapi);
                            laporan.setUmur(umur);
                            laporan.setBerat(berat);
                            laporan.setJenis_pakan(jenis_pakan);
                            laporan.setCatatan_penting(catatan_penting);
                            laporan.setQR_Code(taskSnapshot.getDownloadUrl().toString());

                            laporanRef.child(idPengawas).child(key).setValue(laporan);

                            Toast.makeText(getActivity().getApplicationContext(), "Laporan berhasil dibuat" , Toast.LENGTH_SHORT).show();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            });

                } catch (WriterException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Laporan gagal dibuat" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public Uri getImageUri(Bitmap inImage) {
        String path = "";
        if (checkPermissionREAD_EXTERNAL_STORAGE(getContext())) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), inImage, "Title", null);
        }

        return Uri.parse(path);
    }

    public Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
                    300, 300, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 300, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public void saveImage(Bitmap imgSave, String qr_name) {
        //create directory if not exist
        File dir = new File("/sdcard/qr_code/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File output = new File(dir, qr_name+".jpg");
        OutputStream os = null;

        try {
            os = new FileOutputStream(output);
            imgSave.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();

            //this code will scan the image so that it will appear in your gallery when you open next time
            MediaScannerConnection.scanFile(getContext(), new String[]{output.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d("imageSaved", "image is saved in gallery and gallery is refreshed.");
                        }
                    }
            );
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
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
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
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
