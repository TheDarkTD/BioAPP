package com.example.myapplication2.Home;

import static android.service.autofill.Validators.and;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.AppForegroundService;
import com.example.myapplication2.ConectInsole;
import com.example.myapplication2.ConectInsole2;
import com.example.myapplication2.Connection.ConnectionActivity;
import com.example.myapplication2.Data.DataActivity;
import com.example.myapplication2.DataCaptureService;
import com.example.myapplication2.LoginActivity;
import com.example.myapplication2.R;
import com.example.myapplication2.Register.Register4Activity;
import com.example.myapplication2.Register.Register5Activity;
import com.example.myapplication2.Settings.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class HomeActivity extends AppCompatActivity {
    private Register4Activity udpr;
    private Register5Activity udpl;
    FirebaseAuth fAuth;
    FloatingActionButton mPopBtn;
    private SharedPreferences sharedPreferences;
    BottomNavigationView bottomNavigationView;
    private View[] circlesleft, circlesright;
    Button mBtnRead;
    private String followInRight, followInLeft;
    String listeventsleft = "";
    String  listeventsright= "";
    String uid = null;
    Calendar calendar;
    Boolean restart;
    short S1_1, S2_1, S3_1, S4_1, S5_1, S6_1, S7_1, S8_1, S9_1, S1_2, S2_2, S3_2, S4_2, S5_2, S6_2, S7_2, S8_2, S9_2;
    Intent serviceIntent, serviceIntent_notify;
    private TextView atualizacao;
    private ImageView pressuremap, pressuremap2;
    DatabaseReference databaseReference;
    ArrayList<String> Listevents = new ArrayList<String>();
    private View c8r, c1r,c2r,c3r,c4r,c5r,c6r,c7r,c9r,c1,c2,c3,c4,c5,c6,c7,c8,c9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        super.onStart();

        sharedPreferences = getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
        followInRight = sharedPreferences.getString("Sright", "default");
        followInLeft = sharedPreferences.getString("Sleft", "default");

        pressuremap = findViewById(R.id.PressureMap);
        c1r = findViewById(R.id.circle1right);
        c2r = findViewById(R.id.circle2right);
        c3r = findViewById(R.id.circle3right);
        c4r = findViewById(R.id.circle4right);
        c5r = findViewById(R.id.circle5right);
        c6r = findViewById(R.id.circle6right);
        c7r = findViewById(R.id.circle7right);
        c8r = findViewById(R.id.circle8right);
        c9r = findViewById(R.id.circle9right);
        pressuremap2 = findViewById(R.id.PressureMap2);
        c1 = findViewById(R.id.circle1);
        c2 = findViewById(R.id.circle2);
        c3 = findViewById(R.id.circle3);
        c4 = findViewById(R.id.circle4);
        c5 = findViewById(R.id.circle5);
        c6 = findViewById(R.id.circle6);
        c7 = findViewById(R.id.circle7);
        c8 = findViewById(R.id.circle8);
        c9 = findViewById(R.id.circle9);

        // Referencie os círculos e o botão de atualização
        circlesleft = new View[] {
                findViewById(R.id.circle1),
                findViewById(R.id.circle2),
                findViewById(R.id.circle3),
                findViewById(R.id.circle4),
                findViewById(R.id.circle5),
                findViewById(R.id.circle6),
                findViewById(R.id.circle7),
                findViewById(R.id.circle8),
                findViewById(R.id.circle9)
        };

        circlesright = new View[] {
                findViewById(R.id.circle1right),
                findViewById(R.id.circle2right),
                findViewById(R.id.circle3right),
                findViewById(R.id.circle4right),
                findViewById(R.id.circle5right),
                findViewById(R.id.circle6right),
                findViewById(R.id.circle7right),
                findViewById(R.id.circle8right),
                findViewById(R.id.circle9right)
        };

        /*if (followInRight.equals("false")){
            pressuremap.setVisibility(View.GONE);
            c1r.setVisibility(View.GONE);
            c2r.setVisibility(View.GONE);
            c3r.setVisibility(View.GONE);
            c4r.setVisibility(View.GONE);
            c5r.setVisibility(View.GONE);
            c6r.setVisibility(View.GONE);
            c7r.setVisibility(View.GONE);
            c8r.setVisibility(View.GONE);
            c9r.setVisibility(View.GONE);
        }

        if (followInLeft.equals("false")){
            pressuremap2.setVisibility(View.GONE);
            c1.setVisibility(View.GONE);
            c2.setVisibility(View.GONE);
            c3.setVisibility(View.GONE);
            c4.setVisibility(View.GONE);
            c5.setVisibility(View.GONE);
            c6.setVisibility(View.GONE);
            c7.setVisibility(View.GONE);
            c8.setVisibility(View.GONE);
            c9.setVisibility(View.GONE);
        }*/

    }
    public void onStart(){
        super.onStart();
        Intent serviceIntent1 = new Intent(this, AppForegroundService.class);
        Intent serviceIntent2 = new Intent(this, DataCaptureService.class);
        startService(serviceIntent1);
        startService(serviceIntent2);
        byte freq = 1;
        byte cmd = 0x3A;

        ConectInsole conectar = new ConectInsole(HomeActivity.this);
        ConectInsole2 conectar2 = new ConectInsole2(HomeActivity.this);


        //Buscar valores de limiares já calculados para enviar com o comando 3C-leitura de dados (padronização do pacote de envio)
        sharedPreferences = getSharedPreferences("Treshold_insole1", MODE_PRIVATE);
        S1_1 = (short) sharedPreferences.getInt("Lim1I1", 0xffff);
        S2_1 = (short) sharedPreferences.getInt("Lim2I1", 0xffff);
        S3_1 = (short) sharedPreferences.getInt("Lim3I1", 0xffff);
        S4_1 = (short) sharedPreferences.getInt("Lim4I1", 0xffff);
        S5_1 = (short) sharedPreferences.getInt("Lim5I1", 0xffff);
        S6_1 = (short) sharedPreferences.getInt("Lim6I1", 0xffff);
        S7_1 = (short) sharedPreferences.getInt("Lim7I1", 0xffff);
        S8_1 = (short) sharedPreferences.getInt("Lim8I1", 0xffff);
        S9_1 = (short) sharedPreferences.getInt("Lim9I1", 0xffff);

        //Limiares da palmilha esquerda
        sharedPreferences = getSharedPreferences("Treshold_insole2", MODE_PRIVATE);
        S1_2 = (short) sharedPreferences.getInt("Lim1I2", 0xffff);
        S2_2 = (short) sharedPreferences.getInt("Lim2I2", 0xffff);
        S3_2 = (short) sharedPreferences.getInt("Lim3I2", 0xffff);
        S4_2 = (short) sharedPreferences.getInt("Lim4I2", 0xffff);
        S5_2 = (short) sharedPreferences.getInt("Lim5I2", 0xffff);
        S6_2 = (short) sharedPreferences.getInt("Lim6I2", 0xffff);
        S7_2 = (short) sharedPreferences.getInt("Lim7I2", 0xffff);
        S8_2 = (short) sharedPreferences.getInt("Lim8I2", 0xffff);
        S9_2 = (short) sharedPreferences.getInt("Lim9I2", 0xffff);


        if (followInRight.equals("true")){
            conectar.createAndSendConfigData(cmd, freq, S1_1, S2_1, S3_1, S4_1, S5_1, S6_1, S7_1, S8_1, S9_1);

        }
        if (followInLeft.equals("true")){
            conectar2.createAndSendConfigData(cmd, freq, S1_2, S2_2, S3_2, S4_2, S5_2, S6_2, S7_2, S8_2, S9_2);

        }


        //Barra inferior de navegação
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavview1);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    return true;
                case R.id.settings:
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    finish();
                    return true;
                case R.id.connection:
                    startActivity(new Intent(getApplicationContext(), ConnectionActivity.class));
                    finish();
                    return true;
                case R.id.data:
                    startActivity(new Intent(getApplicationContext(), DataActivity.class));
                    finish();
                    return true;
            }
            return false;
        });


        //Botão float que expande lista das atualizações
        mPopBtn =(FloatingActionButton) findViewById(R.id.floatingActionButton2);
        mPopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, Pop.class));
            }
        });


        //Botão para atualizar leitura do sensor
        mBtnRead = findViewById((R.id.buttonread));
        mBtnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Envia solicitação de leitura a palmilha
                byte cmd3c = 0X3C;

                if (followInRight.equals("true")){
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        conectar.createAndSendConfigData(cmd3c, freq, S1_1, S2_1, S3_1, S4_1, S5_1, S6_1, S7_1, S8_1, S9_1);
                    }, 1000);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    conectar.receiveData(HomeActivity.this);}, 1500);

                    //Atualiza os valores de pressão plotados na tela
                    checkforcolors_right();


                }


                if (followInLeft.equals("true")){
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        conectar2.createAndSendConfigData(cmd3c, freq, S1_2, S2_2, S3_2, S4_2, S5_2, S6_2, S7_2, S8_2, S9_2);
                    }, 1000);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        conectar2.receiveData(HomeActivity.this);}, 1500);

                    //Atualiza os valores de pressão plotados na tela
                    checkforcolors_left();


                }

            }

        });
        Insole_RightIP();
        Insole_leftIP();
    }


    private void checkforcolors_right() {
        sharedPreferences = getSharedPreferences("ConfigPrefs1", MODE_PRIVATE);
        int[] thresholds = loadThresholds(sharedPreferences);

        sharedPreferences = getSharedPreferences("My_Appinsolereadings", MODE_PRIVATE);
        short[][] sensorReadings = loadSensorReadings(sharedPreferences);

        short[] minMax = findMinMax(sensorReadings);
        int dimension = minMax[1] - minMax[0];

        int[] ratioValues = calculateRatios(sensorReadings, minMax[0], dimension);
        applyColorsToCircles(circlesright, ratioValues);

        for (int i = 0; i < 9; i++) {
            if (comparevalues(sensorReadings[i], thresholds[i])) {
                circlesright[i].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
    }

    private void checkforcolors_left() {
        sharedPreferences = getSharedPreferences("ConfigPrefs2", MODE_PRIVATE);
        int[] thresholds = loadThresholds(sharedPreferences);

        sharedPreferences = getSharedPreferences("My_Appinsolereadings", MODE_PRIVATE);
        short[][] sensorReadings = loadSensorReadings(sharedPreferences);

        short[] minMax = findMinMax(sensorReadings);
        int dimension = minMax[1] - minMax[0];

        int[] ratioValues = calculateRatios(sensorReadings, minMax[0], dimension);
        applyColorsToCircles(circlesleft, ratioValues);

        for (int i = 0; i < 9; i++) {
            if (comparevalues(sensorReadings[i], thresholds[i])) {
                circlesleft[i].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
    }

    private int[] loadThresholds(SharedPreferences prefs) {
        int[] thresholds = new int[9];
        for (int i = 0; i < 9; i++) {
            thresholds[i] = (short) prefs.getInt("S" + (i + 1), 8191);
        }
        return thresholds;
    }

    private short[][] loadSensorReadings(SharedPreferences prefs) {
        String[] sensorKeys = {"S1_1", "S2_1", "S3_1", "S4_1", "S5_1", "S6_1", "S7_1", "S8_1", "S9_1"};
        short[][] readings = new short[9][];

        for (int i = 0; i < 9; i++) {
            String data = prefs.getString(sensorKeys[i], "[0,0,0,0,0]");
            if (data != null && !data.equals("")) {
                readings[i] = stringToShortArray(data);
            } else {
                readings[i] = new short[]{0, 0, 0, 0, 0};
            }
        }
        return readings;
    }

    private int[] calculateRatios(short[][] sensorReadings, short min, int dimension) {
        int[] ratios = new int[9];

        if (dimension == 0) {
            Arrays.fill(ratios, 1);
            return ratios;
        }

        for (int i = 0; i < 9; i++) {
            short[] values = sensorReadings[i];
            if (values.length > 0) {
                int lastValue = values[values.length - 1];
                ratios[i] = (lastValue - min) / dimension;
            } else {
                ratios[i] = 0;
            }
        }

        return ratios;
    }

    private void applyColorsToCircles(View[] circles, int[] ratioValues) {
        for (int i = 0; i < circles.length; i++) {
            int pressure = ratioValues[i];
            int color = calculateColor(pressure);
            circles[i].setBackgroundTintList(ColorStateList.valueOf(color));
        }
    }

    private Boolean comparevalues(short[] array, int threshold) {
        return array.length > 0 && array[array.length - 1] > threshold;
    }

    private int calculateColor(int pressure) {
        int startColor = Color.BLUE;
        int endColor = Color.RED;
        float clampedRatio = Math.min(1f, Math.max(0f, pressure));
        return interpolateColor(startColor, endColor, clampedRatio);
    }

    private int interpolateColor(int colorStart, int colorEnd, float ratio) {
        int alpha = (int) (Color.alpha(colorStart) + (Color.alpha(colorEnd) - Color.alpha(colorStart)) * ratio);
        int red = (int) (Color.red(colorStart) + (Color.red(colorEnd) - Color.red(colorStart)) * ratio);
        int green = (int) (Color.green(colorStart) + (Color.green(colorEnd) - Color.green(colorStart)) * ratio);
        int blue = (int) (Color.blue(colorStart) + (Color.blue(colorEnd) - Color.blue(colorStart)) * ratio);
        return Color.argb(alpha, red, green, blue);
    }

    public static short[] findMinMax(short[][] array) {
        short min = Short.MAX_VALUE;
        short max = Short.MIN_VALUE;

        for (short[] subArray : array) {
            if (subArray != null) {
                for (short value : subArray) {
                    if (value < min) min = value;
                    if (value > max) max = value;
                }
            }
        }

        return new short[]{min, max, (short) (max - min)};
    }

    private short[] stringToShortArray(String input) {
        input = input.replace("[", "").replace("]", "").trim();

        if (input.isEmpty()) return new short[0];

        String[] parts = input.split(",");
        short[] result = new short[parts.length];

        for (int i = 0; i < parts.length; i++) {
            try {
                result[i] = (short) Integer.parseInt(parts[i].trim());
            } catch (NumberFormatException e) {
                result[i] = 0;
            }
        }

        return result;
    }


    public void Insole_RightIP() {
        final int udpPortr = 20000; // Porta do ESP

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket(udpPortr);
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    while (true) {
                        socket.receive(packet);
                        String IPR = new String(packet.getData(), 0, packet.getLength());
                        Log.e("UDP", "Received IP: " + IPR + " on port: " + udpPortr);
                        // Armazene o IP conforme necessário
                        SharedPreferences sharedPreferences = getSharedPreferences("My_Appips", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("IP", IPR);
                        editor.apply();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void Insole_leftIP() {
        final int udpPortl = 20001; // Porta do ESP

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket(udpPortl);
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    while (true) {
                        socket.receive(packet);
                        String IPL = new String(packet.getData(), 0, packet.getLength());
                        Log.e("UDP", "Received IP left: " + IPL + " on port: " + udpPortl);
                        // Armazene o IP conforme necessário
                        SharedPreferences sharedPreferences = getSharedPreferences("My_Appips", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("IP2", IPL);
                        editor.apply();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}