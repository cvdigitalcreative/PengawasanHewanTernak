package com.example.shaff.pengawasanhewanternak;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.shaff.pengawasanhewanternak.Fragments.MenuAdmin;
import com.example.shaff.pengawasanhewanternak.Fragments.MenuPengawas;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle receivedData = getIntent().getExtras();
        String username = receivedData.getString("USER_NAME");

        if(username.equals("admin123")){
            MenuAdmin menu = new MenuAdmin();
            getSupportFragmentManager().beginTransaction().add(R.id.container_fragment, menu).addToBackStack(null).commit();
        }
        else{
            String id_pengawas = receivedData.getString("ID_PENGAWAS");
            Bundle sendedData = new Bundle();
            sendedData.putString("ID_PENGAWAS", id_pengawas);

            MenuPengawas menu = new MenuPengawas();
            menu.setArguments(sendedData);
            getSupportFragmentManager().beginTransaction().add(R.id.container_fragment, menu).addToBackStack(null).commit();
        }
    }

}
