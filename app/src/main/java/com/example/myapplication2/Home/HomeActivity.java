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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    DatabaseReference databaseReference;
    ArrayList<String> Listevents = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        super.onStart();

        sharedPreferences = getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
        followInRight = sharedPreferences.getString("Sright", "default");
        followInLeft = sharedPreferences.getString("Sleft", "default");

        atualizacao = findViewById(R.id.textView3);

        serviceIntent = new Intent(this, DataCaptureService.class);

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

    }
    public void onStart(){
        super.onStart();

        FirebaseUser user = fAuth.getCurrentUser();
        uid = user.getUid();
        startService(serviceIntent);
        byte freq = 1;
        byte cmd = 0x3A;

        ConectInsole conectar = new ConectInsole(HomeActivity.this);
        ConectInsole2 conectar2 = new ConectInsole2(HomeActivity.this);

        if (followInRight.equals("true")){

           databaseReference = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(uid)
                    .child("novaVariavel");


            databaseReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        // Converter o valor para uma lista de strings
                        for (DataSnapshot item : snapshot.getChildren()) {
                            String valor = item.getValue(String.class);
                            if (valor != null) {
                                listeventsright = valor;
                            }
                        }

                        // Agora você pode usar listeventsright como quiser
                        Log.d("Firebase", "listeventsright: " + listeventsright.toString());

                    } else {
                        Log.d("Firebase", "novaVariavel está vazia ou não existe.");
                    }
                } else {
                    Log.e("Firebase", "Erro ao buscar dados: ", task.getException());
                }
            });

        }
        if (followInLeft.equals("true")) {
            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(uid)
                    .child("novaVariavel2");


            databaseReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        // Converter o valor para uma lista de strings
                        for (DataSnapshot item : snapshot.getChildren()) {
                            String valor = item.getValue(String.class);
                            if (valor != null) {
                                listeventsleft=valor;
                            }
                        }

                        // Agora você pode usar listeventsright como quiser
                        Log.d("Firebase", "listeventsright: " + listeventsright.toString());

                    } else {
                        Log.d("Firebase", "novaVariavel está vazia ou não existe.");
                    }
                } else {
                    Log.e("Firebase", "Erro ao buscar dados: ", task.getException());
                }
            });

        }

        Listevents.add(listeventsleft);
        Listevents.add(listeventsright);

        StringBuilder builder = new StringBuilder();
        for (String item : Listevents) {
            builder.append(item).append("\n");
        }
        atualizacao.setText(builder.toString());



        //Buscar valores de limiares já calculados para enviar com o comando 3C-leitura de dados (padronização do pacote de envio)
        sharedPreferences = getSharedPreferences("Treshold_insole1", MODE_PRIVATE);
        S1_1 = (short) sharedPreferences.getInt("Lim1I1", 8191);
        S2_1 = (short) sharedPreferences.getInt("Lim2I1", 8191);
        S3_1 = (short) sharedPreferences.getInt("Lim3I1", 8191);
        S4_1 = (short) sharedPreferences.getInt("Lim4I1", 8191);
        S5_1 = (short) sharedPreferences.getInt("Lim5I1", 8191);
        S6_1 = (short) sharedPreferences.getInt("Lim6I1", 8191);
        S7_1 = (short) sharedPreferences.getInt("Lim7I1", 8191);
        S8_1 = (short) sharedPreferences.getInt("Lim8I1", 8191);
        S9_1 = (short) sharedPreferences.getInt("Lim9I1", 8191);

        //Limiares da palmilha esquerda
        sharedPreferences = getSharedPreferences("Treshold_insole2", MODE_PRIVATE);
        S1_2 = (short) sharedPreferences.getInt("Lim1I2", 8191);
        S2_2 = (short) sharedPreferences.getInt("Lim2I2", 8191);
        S3_2 = (short) sharedPreferences.getInt("Lim3I2", 8191);
        S4_2 = (short) sharedPreferences.getInt("Lim4I2", 8191);
        S5_2 = (short) sharedPreferences.getInt("Lim5I2", 8191);
        S6_2 = (short) sharedPreferences.getInt("Lim6I2", 8191);
        S7_2 = (short) sharedPreferences.getInt("Lim7I2", 8191);
        S8_2 = (short) sharedPreferences.getInt("Lim8I2", 8191);
        S9_2 = (short) sharedPreferences.getInt("Lim9I2", 8191);


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
                stopService(serviceIntent);
                byte cmd3c = 0X3C;

                if (followInRight.equals("true")){
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        conectar.createAndSendConfigData(cmd3c, freq, S1_1, S2_1, S3_1, S4_1, S5_1, S6_1, S7_1, S8_1, S9_1);
                    }, 1000);

                    //new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    //    conectar.receiveData(HomeActivity.this);}, 1500);

                    //Atualiza os valores de pressão plotados na tela
                    checkforcolors_right();


                }


                if (followInLeft.equals("true")){
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        conectar2.createAndSendConfigData(cmd3c, freq, S1_2, S2_2, S3_2, S4_2, S5_2, S6_2, S7_2, S8_2, S9_2);
                    }, 1000);

                    //new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    //   conectar2.receiveData(HomeActivity.this);}, 1500);

                    //Atualiza os valores de pressão plotados na tela
                    checkforcolors_left();


                }


                //Reinicia o serviço de captura de dados quando há atualização destes
                startService(serviceIntent);



            }

        });
        Insole_RightIP();
        Insole_leftIP();
    }


    private void checkforcolors_right() {

        //checar regiões de interesse
        SharedPreferences sharedPreferences = getSharedPreferences("My_Appregions", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean S1 = sharedPreferences.getBoolean("S1r", false);
        Boolean S2 =sharedPreferences.getBoolean("S2r", false);
        Boolean S3 =sharedPreferences.getBoolean("S3r", false);
        Boolean S4 =sharedPreferences.getBoolean("S4r", false);
        Boolean S5 =sharedPreferences.getBoolean("S5r", false);
        Boolean S6 =sharedPreferences.getBoolean("S6r", false);
        Boolean S7 =sharedPreferences.getBoolean("S7r", false);
        Boolean S8 =sharedPreferences.getBoolean("S8r", false);
        Boolean S9 =sharedPreferences.getBoolean("S9r", false);


        //checar limiares
        sharedPreferences = getSharedPreferences("Treshold_insole1", MODE_PRIVATE);
        int S1_t = (short) sharedPreferences.getInt("Lim1I1", 8191);
        int S2_t = (short) sharedPreferences.getInt("Lim2I1", 8191);
        int S3_t = (short) sharedPreferences.getInt("Lim3I1", 8191);
        int S4_t = (short) sharedPreferences.getInt("Lim4I1", 8191);
        int S5_t = (short) sharedPreferences.getInt("Lim5I1", 8191);
        int S6_t = (short) sharedPreferences.getInt("Lim6I1", 8191);
        int S7_t = (short) sharedPreferences.getInt("Lim7I1", 8191);
        int S8_t = (short) sharedPreferences.getInt("Lim8I1", 8191);
        int S9_t = (short) sharedPreferences.getInt("Lim9I1", 8191);


        //calcular valores para cada círculo
        //definir menor valor de todas as leituras e maior valor de todas as leituras
        sharedPreferences = getSharedPreferences("My_Appinsolereadings", MODE_PRIVATE);
        String[] sensorKeys = {"S1_1", "S2_1", "S3_1", "S4_1", "S5_1", "S6_1", "S7_1", "S8_1", "S9_1"};
        short[][] sensorReadings = new short[9][];

        for (int i = 0; i < 9; i++) {
            sensorReadings[i] = stringToShortArray(sharedPreferences.getString(sensorKeys[i], "[0,0,0,0,0]"));
        }

        short[] minMax = findMinMax(sensorReadings);
        int dimension = minMax[1] - minMax[0];
        int plength = sensorReadings[1].length;

        //avaliar ultimo valor lido por cada sensor e calcular porcentagem com base no maximo e minimo
        short p1 = (short) ((sensorReadings[0][plength-1])- minMax[0]/dimension);
        short p2 = (short) ((sensorReadings[1][plength-1]- minMax[0])/dimension);
        short p3 = (short) ((sensorReadings[2][plength-1]- minMax[0])/dimension);
        short p4 = (short) ((sensorReadings[3][plength-1]- minMax[0])/dimension);
        short p5 = (short) ((sensorReadings[4][plength-1]- minMax[0])/dimension);
        short p6 = (short) ((sensorReadings[5][plength-1]- minMax[0])/dimension);
        short p7 = (short) ((sensorReadings[6][plength-1]- minMax[0])/dimension);
        short p8 = (short) ((sensorReadings[7][plength-1]- minMax[0])/dimension);
        short p9 = (short) ((sensorReadings[8][plength-1]- minMax[0])/dimension);
        int[] ratioValues = {p1, p2, p3, p4, p5, p6, p7, p8, p9};

        //definir cor com base na porcentagem

        for (int i = 0; i < circlesright.length; i++) {
            int pressure = ratioValues[i];
            int color = calculateColor(pressure);
            circlesright[i].setBackgroundTintList(ColorStateList.valueOf(color));
        }

        //comparar valores recebidos com limiar salvo para identificar local do evento
        // Array de flags (regiões ativas)
        boolean[] sensoresAtivos = {S1, S2, S3, S4, S5, S6, S7, S8, S9};

        // Array de limiares
        int[] thresholds = {S1_t, S2_t, S3_t, S4_t, S5_t, S6_t, S7_t, S8_t, S9_t};

        // Loop para comparar valores e colorir os círculos
        for (int i = 0; i < 9; i++) {
            if (sensoresAtivos[i] && comparevalues(sensorReadings[i], thresholds[i])) {
                circlesright[i].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }


    }


    private void checkforcolors_left() {

        //checar regiões de interesse
        SharedPreferences sharedPreferences = getSharedPreferences("My_Appregions", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean S1 = sharedPreferences.getBoolean("S1", false);
        Boolean S2 =sharedPreferences.getBoolean("S2", false);
        Boolean S3 =sharedPreferences.getBoolean("S3", false);
        Boolean S4 =sharedPreferences.getBoolean("S4", false);
        Boolean S5 =sharedPreferences.getBoolean("S5", false);
        Boolean S6 =sharedPreferences.getBoolean("S6", false);
        Boolean S7 =sharedPreferences.getBoolean("S7", false);
        Boolean S8 =sharedPreferences.getBoolean("S8", false);
        Boolean S9 =sharedPreferences.getBoolean("S9", false);


        //checar limiares
        sharedPreferences = getSharedPreferences("Treshold_insole2", MODE_PRIVATE);
        int S1_t = (short) sharedPreferences.getInt("Lim1I2", 8191);
        int S2_t = (short) sharedPreferences.getInt("Lim2I2", 8191);
        int S3_t = (short) sharedPreferences.getInt("Lim3I2", 8191);
        int S4_t = (short) sharedPreferences.getInt("Lim4I2", 8191);
        int S5_t = (short) sharedPreferences.getInt("Lim5I2", 8191);
        int S6_t = (short) sharedPreferences.getInt("Lim6I2", 8191);
        int S7_t = (short) sharedPreferences.getInt("Lim7I2", 8191);
        int S8_t = (short) sharedPreferences.getInt("Lim8I2", 8191);
        int S9_t = (short) sharedPreferences.getInt("Lim9I2", 8191);


        //calcular valores para cada círculo
        //definir menor valor de todas as leituras e maior valor de todas as leituras
        sharedPreferences = getSharedPreferences("My_Appinsolereadings2", MODE_PRIVATE);
        String[] sensorKeys = {"S1_2", "S2_2", "S3_2", "S4_2", "S5_2", "S6_2", "S7_2", "S8_2", "S9_2"};
        short[][] sensorReadings = new short[9][];

        for (int i = 0; i < 9; i++) {
            sensorReadings[i] = stringToShortArray(sharedPreferences.getString(sensorKeys[i], "[0,0,0,0,0]"));
        }

        short[] minMax = findMinMax(sensorReadings);
        int dimension = minMax[2];
        System.out.println(dimension);
        int plength = sensorReadings[1].length;

        //avaliar ultimo valor lido por cada sensor e calcular porcentagem com base no maximo e minimo
        short p1 = (short) ((sensorReadings[0][plength-1])- minMax[0]/dimension);
        short p2 = (short) ((sensorReadings[1][plength-1]- minMax[0])/dimension);
        short p3 = (short) ((sensorReadings[2][plength-1]- minMax[0])/dimension);
        short p4 = (short) ((sensorReadings[3][plength-1]- minMax[0])/dimension);
        short p5 = (short) ((sensorReadings[4][plength-1]- minMax[0])/dimension);
        short p6 = (short) ((sensorReadings[5][plength-1]- minMax[0])/dimension);
        short p7 = (short) ((sensorReadings[6][plength-1]- minMax[0])/dimension);
        short p8 = (short) ((sensorReadings[7][plength-1]- minMax[0])/dimension);
        short p9 = (short) ((sensorReadings[8][plength-1]- minMax[0])/dimension);
        int[] ratioValues = {p1, p2, p3, p4, p5, p6, p7, p8, p9};

        //definir cor com base na porcentagem

        for (int i = 0; i < circlesleft.length; i++) {
            int pressure = ratioValues[i];
            int color = calculateColor(pressure);
            circlesleft[i].setBackgroundTintList(ColorStateList.valueOf(color));
        }

        //comparar valores recebidos com limiar salvo para identificar local do evento
        // Array de flags (regiões ativas)
        boolean[] sensoresAtivos = {S1, S2, S3, S4, S5, S6, S7, S8, S9};

        // Array de limiares
        int[] thresholds = {S1_t, S2_t, S3_t, S4_t, S5_t, S6_t, S7_t, S8_t, S9_t};

        // Loop para comparar valores e colorir os círculos
        for (int i = 0; i < 9; i++) {
            if (sensoresAtivos[i] && comparevalues(sensorReadings[i], thresholds[i])) {
                circlesright[i].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }


    }

    private Boolean comparevalues(short[] array, int threshold) {
        boolean event = false;
        int num = array.length;

        // Verifica se o array não está vazio antes de acessar o último elemento
        if (num > 0 && array[num - 1] > threshold) {
            event = true;
        }

        return event;
    }


    // Método para calcular a cor com base no valor de pressão
    private int calculateColor(int pressure) {

        // Defina as cores de referência (azul e vermelho)
        int startColor = Color.BLUE; // Cor inicial (azul)
        int endColor = Color.RED;    // Cor final (vermelho)

        // Interpole entre as cores inicial e final com base na proporção
        int interpolatedColor = interpolateColor(startColor, endColor, pressure);

        return interpolatedColor;
    }

    // Método para interpolar entre duas cores com base em uma proporção
    private int interpolateColor(int colorStart, int colorEnd, float ratio) {
        // Extraia os componentes ARGB das cores inicial e final
        int alphaStart = Color.alpha(colorStart);
        int redStart = Color.red(colorStart);
        int greenStart = Color.green(colorStart);
        int blueStart = Color.blue(colorStart);

        int alphaEnd = Color.alpha(colorEnd);
        int redEnd = Color.red(colorEnd);
        int greenEnd = Color.green(colorEnd);
        int blueEnd = Color.blue(colorEnd);

        // Interpolação dos componentes ARGB com base na proporção
        int interpolatedAlpha = (int) (alphaStart + (alphaEnd - alphaStart) * ratio);
        int interpolatedRed = (int) (redStart + (redEnd - redStart) * ratio);
        int interpolatedGreen = (int) (greenStart + (greenEnd - greenStart) * ratio);
        int interpolatedBlue = (int) (blueStart + (blueEnd - blueStart) * ratio);

        // Componha a cor interpolada
        int interpolatedColor = Color.argb(interpolatedAlpha, interpolatedRed, interpolatedGreen, interpolatedBlue);

        return interpolatedColor;
    }

    public static short[] findMinMax(short[][] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("O array não pode estar vazio!");
        }

        short min = Short.MAX_VALUE;
        short max = Short.MIN_VALUE;

        // Percorre cada subarray
        for (short[] subArray : array) {
            if (subArray != null) { // Evita NullPointerException
                for (short value : subArray) {
                    if (value < min) min = value;
                    if (value > max) max = value;
                }
            }
        }
        short dimension = (short) (max - min);

        return new short[]{min, max, dimension}; // Retorna um array contendo o menor e maior valores
    }


    public short[] stringToShortArray(String str) {
        str = str.replaceAll("[\\[\\]\\s]", ""); // Remove colchetes e espaços
        String[] stringArray = str.split(",");
        short[] shortArray = new short[stringArray.length];

        for (int i = 0; i < stringArray.length; i++) {
            shortArray[i] = Short.parseShort(stringArray[i]);
        }

        return shortArray;
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
                        Log.e("UDP", "Received IP: " + IPL + " on port: " + udpPortl);
                        // Armazene o IP conforme necessário
                        SharedPreferences sharedPreferences = getSharedPreferences("My_Appips", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("IP", IPL);
                        editor.apply();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}