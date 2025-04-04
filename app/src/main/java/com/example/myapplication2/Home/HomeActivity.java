package com.example.myapplication2.Home;

import static android.service.autofill.Validators.and;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.ConectInsole;
import com.example.myapplication2.ConectInsole2;
import com.example.myapplication2.Connection.ConnectionActivity;
import com.example.myapplication2.Data.DataActivity;
import com.example.myapplication2.DataCaptureService;
import com.example.myapplication2.R;
import com.example.myapplication2.Register.Register4Activity;
import com.example.myapplication2.Register.Register5Activity;
import com.example.myapplication2.Settings.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;


public class HomeActivity extends AppCompatActivity {
    private Register4Activity udpr;
    private Register5Activity udpl;
    FloatingActionButton mPopBtn;
    private SharedPreferences sharedPreferences;
    BottomNavigationView bottomNavigationView;
    private View[] circlesleft, circlesright;
    Button mBtnRead;
    private String followInRight, followInLeft;
    Calendar calendar;
    Boolean restart;
    short S1_1, S2_1, S3_1, S4_1, S5_1, S6_1, S7_1, S8_1, S9_1, S1_2, S2_2, S3_2, S4_2, S5_2, S6_2, S7_2, S8_2, S9_2;
    Intent serviceIntent, serviceIntent_notify;
    private TextView atualizaçao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        super.onStart();


        sharedPreferences = getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
        followInRight = sharedPreferences.getString("Sright", "default");
        followInLeft = sharedPreferences.getString("Sleft", "default");
        if (followInRight.equals("true")) {
            udpr.Insole_RightIP();
        }

        if (followInLeft.equals("true")) {
            udpl.Insole_leftIP();
        }
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

        startService(serviceIntent);
        byte freq = 1;
        byte cmd = 0x3A;

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent_notify);
        } else {
            startService(serviceIntent_notify);
        }*/
        if (followInRight.equals("true")) {
            udpr.Insole_RightIP();
        }

        if (followInLeft.equals("true")) {
            udpl.Insole_leftIP();
        }
        ConectInsole conectar = new ConectInsole(HomeActivity.this);
        ConectInsole2 conectar2 = new ConectInsole2(HomeActivity.this);

        calendar = Calendar.getInstance();
        int khour = calendar.get(Calendar.HOUR_OF_DAY);
        int kminutes = calendar.get(Calendar.MINUTE);
        int kseconds = calendar.get(Calendar.SECOND);
        int kmiliseconds = calendar.get(Calendar.MILLISECOND);

        byte hour = (byte) khour;
        byte minutes = (byte) kminutes;
        byte seconds = (byte) kseconds;
        byte milliseconds = (byte) kmiliseconds;


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
            conectar.createAndSendConfigData(cmd, hour, minutes, seconds, milliseconds, freq, S1_1, S2_1, S3_1, S4_1, S5_1, S6_1, S7_1, S8_1, S9_1);

        }


        if (followInLeft.equals("true")){
            conectar2.createAndSendConfigData(cmd, hour, minutes, seconds, milliseconds, freq, S1_2, S2_2, S3_2, S4_2, S5_2, S6_2, S7_2, S8_2, S9_2);

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
                atualizaçao = findViewById(R.id.textView3);
                //Envia solicitação de leitura a palmilha
                stopService(serviceIntent);
                byte cmd3c = 0X3C;

                if (followInRight.equals("true")){
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        conectar.createAndSendConfigData(cmd3c, hour, minutes, seconds, milliseconds, freq, S1_1, S2_1, S3_1, S4_1, S5_1, S6_1, S7_1, S8_1, S9_1);
                    }, 1000);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        conectar.receiveData(HomeActivity.this);}, 1500);

                }


                if (followInLeft.equals("true")){
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        conectar2.createAndSendConfigData(cmd3c, hour, minutes, seconds, milliseconds, freq, S1_2, S2_2, S3_2, S4_2, S5_2, S6_2, S7_2, S8_2, S9_2);
                    }, 1000);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        conectar2.receiveData(HomeActivity.this);}, 1500);

                }
                String senddatainsole1=conectar.getSendDataAsString();
                String senddatainsole2=conectar2.getSendDataAsString();

                if (followInRight.equals("true") && followInLeft.equals("false")){
                    atualizaçao.setText(senddatainsole1);
                    //Atualiza os valores de pressão plotados na tela
                    checkforcolors_right();
                } else if (followInRight.equals("false") && followInLeft.equals("true")) {
                    atualizaçao.setText(senddatainsole2);
                    //Atualiza os valores de pressão plotados na tela
                    checkforcolors_left();
                }else {
                    atualizaçao.setText(senddatainsole1 + senddatainsole2);
                }


                //Reinicia o serviço de captura de dados quando há atualização destes
                startService(serviceIntent);



            }

        });

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
        if (S1){
            if (comparevalues(sensorReadings[0], S1_t)){
                //tornar círculo vermelho
                circlesright[0].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S2){
            if (comparevalues(sensorReadings[1], S2_t)){
                circlesright[1].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S3){
            if (comparevalues(sensorReadings[2], S3_t)){
                circlesright[2].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S4){
            if (comparevalues(sensorReadings[3], S4_t)){
                circlesright[3].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S5){
            if (comparevalues(sensorReadings[4], S5_t)){
                circlesright[4].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S6){
            if (comparevalues(sensorReadings[5], S6_t)){
                circlesright[5].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S7){
            if (comparevalues(sensorReadings[6], S7_t)){
                circlesright[6].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S8){
            if (comparevalues(sensorReadings[7], S8_t)){
                circlesright[7].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S9){
            if (comparevalues(sensorReadings[8], S9_t)){
                circlesright[8].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
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

        for (int i = 0; i < circlesleft.length; i++) {
            int pressure = ratioValues[i];
            int color = calculateColor(pressure);
            circlesleft[i].setBackgroundTintList(ColorStateList.valueOf(color));
        }

        //comparar valores recebidos com limiar salvo para identificar local do evento
        if (S1){
            if (comparevalues(sensorReadings[0], S1_t)){
                //tornar círculo vermelho
                circlesleft[0].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S2){
            if (comparevalues(sensorReadings[1], S2_t)){
                circlesleft[1].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S3){
            if (comparevalues(sensorReadings[2], S3_t)){
                circlesleft[2].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S4){
            if (comparevalues(sensorReadings[3], S4_t)){
                circlesleft[3].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S5){
            if (comparevalues(sensorReadings[4], S5_t)){
                circlesleft[4].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S6){
            if (comparevalues(sensorReadings[5], S6_t)){
                circlesleft[5].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S7){
            if (comparevalues(sensorReadings[6], S7_t)){
                circlesleft[6].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S8){
            if (comparevalues(sensorReadings[7], S8_t)){
                circlesleft[7].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
        if (S9){
            if (comparevalues(sensorReadings[8], S9_t)){
                circlesleft[8].setBackgroundTintList(ColorStateList.valueOf(Color.RED));
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

        return new short[]{min, max}; // Retorna um array contendo o menor e maior valores
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


}