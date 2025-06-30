package com.example.myapplication2.Home;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.AppForegroundService;
import com.example.myapplication2.ConectInsole;
import com.example.myapplication2.ConectInsole2;
import com.example.myapplication2.Connection.ConnectionActivity;
import com.example.myapplication2.Data.DataActivity;
import com.example.myapplication2.DataCaptureService;

import com.example.myapplication2.HeatMapViewL;
import com.example.myapplication2.HeatMapViewR;

import com.example.myapplication2.R;
import com.example.myapplication2.Register.Register4Activity;
import com.example.myapplication2.Register.Register5Activity;
import com.example.myapplication2.Settings.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

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
    FrameLayout frameL, frameR;
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
    DatabaseReference databaseReference;
    ArrayList<String> Listevents = new ArrayList<String>();

    ImageView maskL, maskR;

    HeatMapViewL heatmapViewL;
    HeatMapViewR heatmapViewR;
    List<HeatMapViewL.SensorRegionL> sensoresL = new ArrayList<>();
    List<HeatMapViewR.SensorRegionR> sensoresR = new ArrayList<>();
    float raioRelativo = 0.05f; // proporcional ao tamanho da imagem


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        super.onStart();

        sharedPreferences = getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
        followInRight = sharedPreferences.getString("Sright", "default");
        followInLeft = sharedPreferences.getString("Sleft", "default");

        heatmapViewL = findViewById(R.id.heatmapViewL);
        heatmapViewR = findViewById(R.id.heatmapViewR);
        maskL = findViewById(R.id.imageView5);
        maskR = findViewById(R.id.imageView8);
        frameL = findViewById(R.id.frameL);


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

        if(followInLeft.equals("false")){
            heatmapViewL.setVisibility(View.GONE);
            maskL.setVisibility(View.GONE);
            frameL.setVisibility(View.GONE);
        }

        if (followInRight.equals("false")){
            heatmapViewR.setVisibility(View.GONE);
            maskR.setVisibility(View.GONE);
            frameR.setVisibility(View.GONE);
        }

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

        //plotagem heatmap inicial
        loadColorsL();
        loadColorsR();


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

                    //atualiza plotagem heatmap
                    loadColorsR();


                }


                if (followInLeft.equals("true")){
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        conectar2.createAndSendConfigData(cmd3c, freq, S1_2, S2_2, S3_2, S4_2, S5_2, S6_2, S7_2, S8_2, S9_2);
                    }, 1000);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        conectar2.receiveData(HomeActivity.this);}, 1500);

                    //atualiza plotagem heatmap
                    loadColorsL();
                }

            }

        });
        Insole_RightIP();
        Insole_leftIP();
    }




    private void loadColorsR(){
        sharedPreferences = getSharedPreferences("My_Appinsolereadings", MODE_PRIVATE);
        short[][] sensorReadings = loadSensorReadings(sharedPreferences);

        if (sensorReadings == null || sensorReadings.length < 9) {
            Log.e("HeatMap", "Dados insuficientes: sensorReadings está nulo ou incompleto.");
            return;
        }

        int numSensores = sensorReadings.length;
        int numLeituras = sensorReadings[0].length;

        if (numLeituras == 0) {
            Log.e("HeatMap", "Nenhuma leitura encontrada nos sensores.");
            return;
        }

        int ultimo = numLeituras - 1;

        // Certifique-se que todos os vetores têm o mesmo tamanho
        for (int i = 0; i < numSensores; i++) {
            if (sensorReadings[i] == null || sensorReadings[i].length <= ultimo) {
                Log.e("HeatMap", "Sensor " + i + " não tem leitura no índice " + ultimo);
                return;
            }
        }

        float[] leituraAtual = new float[9];
        for (int i = 0; i < 9; i++) {
            leituraAtual[i] = sensorReadings[i][ultimo];
        }

        sensoresR.clear();
        float raioRelativo = 0.3f;

        sensoresR.add(new HeatMapViewR.SensorRegionR(0.28f, 0.12f, leituraAtual[0], raioRelativo));
        sensoresR.add(new HeatMapViewR.SensorRegionR(0.55f, 0.15f, leituraAtual[1], raioRelativo));
        //3
        sensoresR.add(new HeatMapViewR.SensorRegionR(0.62f, 0.45f, leituraAtual[2], raioRelativo));
        //4
        sensoresR.add(new HeatMapViewR.SensorRegionR(0.49f, 0.30f, leituraAtual[3], raioRelativo));
        sensoresR.add(new HeatMapViewR.SensorRegionR(0.30f, 0.40f, leituraAtual[4], raioRelativo));
        sensoresR.add(new HeatMapViewR.SensorRegionR(0.53f, 0.59f, leituraAtual[5], raioRelativo));
        sensoresR.add(new HeatMapViewR.SensorRegionR(0.51f, 0.72f, leituraAtual[6], raioRelativo));
        //8
        sensoresR.add(new HeatMapViewR.SensorRegionR(0.49f, 0.85f, leituraAtual[7], raioRelativo));
        sensoresR.add(new HeatMapViewR.SensorRegionR(0.34f, 0.85f, leituraAtual[8], raioRelativo));

        heatmapViewR.setRegions(sensoresR);
    }


    private void loadColorsL() {
        sharedPreferences = getSharedPreferences("My_Appinsolereadings2", MODE_PRIVATE);
        short[][] sensorReadings = loadSensorReadings2(sharedPreferences);

        if (sensorReadings == null || sensorReadings.length < 9) {
            Log.e("HeatMap", "Dados insuficientes: sensorReadings está nulo ou incompleto.");
            return;
        }

        int numSensores = sensorReadings.length;
        int numLeituras = sensorReadings[0].length;

        if (numLeituras == 0) {
            Log.e("HeatMap", "Nenhuma leitura encontrada nos sensores.");
            return;
        }

        int ultimo = numLeituras - 1;

        // Certifique-se que todos os vetores têm o mesmo tamanho
        for (int i = 0; i < numSensores; i++) {
            if (sensorReadings[i] == null || sensorReadings[i].length <= ultimo) {
                Log.e("HeatMap", "Sensor " + i + " não tem leitura no índice " + ultimo);
                return;
            }
        }

        float[] leituraAtual = new float[9];
        for (int i = 0; i < 9; i++) {
            leituraAtual[i] = sensorReadings[i][ultimo];
        }

        sensoresL.clear();
        float raioRelativo = 0.3f;

        //1
        sensoresL.add(new HeatMapViewL.SensorRegionL(0.74f, 0.12f, leituraAtual[0], raioRelativo));
        //2
        sensoresL.add(new HeatMapViewL.SensorRegionL(0.51f, 0.18f, leituraAtual[1], raioRelativo));
        //4
        sensoresL.add(new HeatMapViewL.SensorRegionL(0.51f, 0.32f, leituraAtual[3], raioRelativo));
        //3
        sensoresL.add(new HeatMapViewL.SensorRegionL(0.69f, 0.38f, leituraAtual[2], raioRelativo));
        //5
        sensoresL.add(new HeatMapViewL.SensorRegionL(0.42f, 0.45f, leituraAtual[4], raioRelativo));
        //6
        sensoresL.add(new HeatMapViewL.SensorRegionL(0.44f, 0.61f, leituraAtual[5], raioRelativo));
        sensoresL.add(new HeatMapViewL.SensorRegionL(0.48f, 0.75f, leituraAtual[6], raioRelativo));
        sensoresL.add(new HeatMapViewL.SensorRegionL(0.51f, 0.87f, leituraAtual[7], raioRelativo));
        sensoresL.add(new HeatMapViewL.SensorRegionL(0.65f, 0.87f, leituraAtual[8], raioRelativo));

        heatmapViewL.setRegions(sensoresL);
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

    private short[][] loadSensorReadings2(SharedPreferences prefs) {
        String[] sensorKeys = {"S1_2", "S2_2", "S3_2", "S4_2", "S5_2", "S6_2", "S7_2", "S8_2", "S9_2"};
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


    /*
    private int[] loadThresholds(SharedPreferences prefs) {
        int[] thresholds = new int[9];
        for (int i = 0; i < 9; i++) {
            thresholds[i] = (short) prefs.getInt("S" + (i + 1), 8191);
        }
        return thresholds;
    }
     */

    private Boolean comparevalues(short[] array, int threshold) {
        return array.length > 0 && array[array.length - 1] > threshold;
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