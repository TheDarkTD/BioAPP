package com.example.myapplication2.Connection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.Data.DataActivity;
import com.example.myapplication2.Home.HomeActivity;
import com.example.myapplication2.R;
import com.example.myapplication2.Settings.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.net.InetAddress;

public class ConnectionActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    Switch conect1, conect2;
    String batInsoleright, batInsoleleft, batVibra;
    TextView batp, batv;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
    }

    @Override
    public void onStart() {
        super.onStart();

        ///checar se esp está conectado ao smartphone, se não -> switch off, se sim  -> switch on

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavview3);
        bottomNavigationView.setSelectedItemId(R.id.connection);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                case R.id.settings:
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    finish();
                    return true;
                case R.id.connection:
                    return true;
                case R.id.data:
                    startActivity(new Intent(getApplicationContext(), DataActivity.class));
                    finish();
                    return true;
            }
            return false;
        });

        conect1 = findViewById(R.id.switch1);
        conect2 = findViewById(R.id.switch2);
        batp = findViewById(R.id.batinsole);
        batv = findViewById(R.id.batvibra);

        conect1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //reconectar com palmilha
            }
        });

        conect2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //reconectar com vibra
            }
        });


        //função para retornar valores de bateria vibra e palmilha e alterar mensagem

        SharedPreferences sharedPreferences = getSharedPreferences("Battery_info", MODE_PRIVATE);
        batVibra = String.valueOf(sharedPreferences.getInt("batVibra", 0));
        batInsoleright = String.valueOf(sharedPreferences.getInt("Insole_right", 0));
        batInsoleleft = String.valueOf(sharedPreferences.getInt("Insole_left", 0));

        batv.setText(batVibra);
        batp.setText(batInsoleright);
    }

}