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
                } else if (followInRight.equals("false") && followInLeft.equals("true")) {
                    atualizaçao.setText(senddatainsole2);
                }else {
                    atualizaçao.setText(senddatainsole1 + senddatainsole2);
                }


                //Atualiza os valores de pressão plotados na tela
                updateCircleColors();
                //Reinicia o serviço de captura de dados quando há atualização destes
                startService(serviceIntent);



            }

        });

    }


    private void updateCircleColors() {// Método para atualizar as cores dos círculos com base nos níveis de pressão

        // Obtenha os dados dos sensores
         if (followInRight.equals("true")){

             //A booleana indica que se trata da palmilha direita
             Boolean foot1 = false;
             sharedPreferences = getSharedPreferences("My_Appinsolereadings2", MODE_PRIVATE);
             String[] sensorKeys = {"S1_1", "S2_1", "S3_1", "S4_1", "S5_1", "S6_1", "S7_1", "S8_1", "S9_1"};
             short[][] sensorReadings = new short[9][];

             for (int i = 0; i < 9; i++) {
                 sensorReadings[i] = stringToShortArray(sharedPreferences.getString(sensorKeys[i], "[0,0,0,0,0]"));
             }

             short[] pressureValues = thresholdSensors_steady(sensorReadings, foot1);


             // Para cada sensor, calcule a cor correspondente e atualize o círculo
             for (int i = 0; i < circlesright.length; i++) {
                 int pressure = pressureValues[i];
                 int color = calculateColor(pressure);
                 circlesright[i].setBackgroundTintList(ColorStateList.valueOf(color));
             }
         }

          if (followInLeft.equals("true")){

              //A booleana indica que se trata da palmilha esquerda
              Boolean foot2 = true;
              sharedPreferences = getSharedPreferences("My_Appinsolereadings2", MODE_PRIVATE);
              String[] sensorKeys = {"S1_1", "S2_1", "S3_1", "S4_1", "S5_1", "S6_1", "S7_1", "S8_1", "S9_1"};
              short[][] sensorReadings = new short[9][];

              for (int i = 0; i < 9; i++) {
                  sensorReadings[i] = stringToShortArray(sharedPreferences.getString(sensorKeys[i], "[0,0,0,0,0]"));
              }

              short[] pressureValues2 = thresholdSensors_steady(sensorReadings, foot2);


              // Para cada sensor, calcule a cor correspondente e atualize o círculo
              for (int i = 0; i < circlesleft.length; i++) {
                  int pressure = pressureValues2[i];
                  int color = calculateColor(pressure);
                  circlesleft[i].setBackgroundTintList(ColorStateList.valueOf(color));
              }

          }


    }

    // Método para calcular a cor com base no valor de pressão
    private int calculateColor(int pressure) {
        // Defina os valores mínimos e máximos de pressão
        int minPressure = 0;
        int maxPressure = 4095; // Valor máximo de 12 bits

        // Defina as cores de referência (azul e vermelho)
        int startColor = Color.BLUE; // Cor inicial (azul)
        int endColor = Color.RED;    // Cor final (vermelho)

        // Calcule a proporção do valor de pressão entre o mínimo e o máximo
        float ratio = (float) (pressure - minPressure) / (maxPressure - minPressure);

        // Interpole entre as cores inicial e final com base na proporção
        int interpolatedColor = interpolateColor(startColor, endColor, ratio);

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

    @NonNull
    public short[] thresholdSensors_steady(short[][] sensorReadings, boolean whichfoot) {
        short[] limS = new short[9];
        ArrayList<Short>[] meanPeaks = new ArrayList[9];
        int[] lim = new int[9];
        String[] hex = new String[9];

        for (int i = 0; i < 9; i++) {
            meanPeaks[i] = getMeanPeaks(sensorReadings[i]);
            lim[i] = getMean(meanPeaks[i]);
            hex[i] = Integer.toHexString(lim[i]);
        }
        if(whichfoot == true) {
            sharedPreferences = getSharedPreferences("My_Appregions", MODE_PRIVATE);
            String[] regionKeys = {"S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9", "Sn"};
            Boolean[] regions = new Boolean[10];

            for (int i = 0; i < 10; i++) {
                regions[i] = sharedPreferences.getBoolean(regionKeys[i], false);
                System.out.println("regionsS2"+ regions[i]);
            }

            for (int i = 0; i < 9; i++) {
                short hexS = (short) Integer.parseInt(hex[i], 16);
                limS[i] = regions[i].equals("true") ? (short) ((0x3 << 12) | (hexS & 0xFFF)) : (short) ((0x1 << 12) | (hexS & 0xFFF));
            }


        }
        else{
            SharedPreferences sharedPreferences = getSharedPreferences("My_Appregions", MODE_PRIVATE);
            String[] regionKeys = {"S1r", "S2r", "S3r", "S4r", "S5r", "S6r", "S7r", "S8r", "S9r", "Snr"};
            Boolean[] regions = new Boolean[10];

            for (int i = 0; i < 10; i++) {
                regions[i] = sharedPreferences.getBoolean(regionKeys[i], false);
                System.out.println("regionsSr2: " + regions[i]);
            }


            for (int i = 0; i < 9; i++) {
                short hexS = (short) Integer.parseInt(hex[i], 16);
                if (regions[i]) {
                    // If regions[i] is true, use 0x3
                    limS[i] = (short) ((0x3 << 12) | (hexS & 0xFFF));
                } else {
                    // If regions[i] is false, use 0x1
                    limS[i] = (short) ((0x1 << 12) | (hexS & 0xFFF));
                }
            }


        }
        return limS;
    }

    @NonNull
    private ArrayList<Short> getMeanPeaks(short[] readings) {
        ArrayList<Short> meanPeaks = new ArrayList<>();
        ArrayList<Integer> positionPeak = new ArrayList<>();
        short max = findMax(readings);
        short min = findMin(readings);
        int difference = max - min;

        // Encontra os picos nos dados dos sensores
        for (int i = 1; i < readings.length - 1; i++) {
            if (readings[i] > readings[i - 1] && readings[i] > readings[i + 1]) {
                positionPeak.add(i);
            }
        }

        // Calcula a média dos picos
        for (int j = 0; j < positionPeak.size(); j++) {
            int sumP = readings[positionPeak.get(j)];
            int divD = 1;
            for (int k = 1; k < 20; k++) {
                if (positionPeak.get(j) - k >= 0 &&
                        (j + 1 < positionPeak.size() && positionPeak.get(j) - k < positionPeak.get(j + 1)) &&
                        (readings[positionPeak.get(j)] - readings[positionPeak.get(j) - k]) < (0.2 * difference)) {
                    sumP += readings[positionPeak.get(j) - k];
                    divD++;
                }
                if (positionPeak.get(j) + k < readings.length &&
                        (j + 1 < positionPeak.size() && positionPeak.get(j) + k < positionPeak.get(j + 1)) &&
                        (readings[positionPeak.get(j)] - readings[positionPeak.get(j) + k]) < (0.2 * difference)) {
                    sumP += readings[positionPeak.get(j) + k];
                    divD++;
                }
            }
            meanPeaks.add((short) (sumP / divD));
        }
        return meanPeaks;
    }


    private int getMean(@NonNull ArrayList<Short> values) {
        if (values.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (short value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    public short findMax(@NonNull short[] readings) {
        short max = readings[0];
        for (short reading : readings) {
            if (reading > max) {
                max = reading;
            }
        }
        return max;
    }

    public short findMin(@NonNull short[] readings) {
        short min = readings[0];
        for (short reading : readings) {
            if (reading < min) {
                min = reading;
            }
        }
        return min;
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