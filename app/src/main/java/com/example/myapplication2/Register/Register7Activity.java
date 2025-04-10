package com.example.myapplication2.Register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.ConectInsole;
import com.example.myapplication2.ConectInsole2;
import com.example.myapplication2.DataCaptureService;
import com.example.myapplication2.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import pl.droidsonroids.gif.GifImageView;

public class Register7Activity extends AppCompatActivity {
    private ConectInsole conectar;
    private ConectInsole2 conectar2;
    private SharedPreferences sharedPreferences;
    private Calendar calendar;
    Intent serviceIntent;
    private String followInRight, followInLeft;
    private short S1, S2, S3, S4, S5, S6, S7, S8, S9;
    private byte freq = 1;
    private Handler handler = new Handler();
    private Boolean verificar;
    private TextView instruct;
    private GifImageView gifimagewait;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register7_1);

        serviceIntent = new Intent(this, DataCaptureService.class);
        sharedPreferences = getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
        followInRight = sharedPreferences.getString("Sright", "default");
        followInLeft = sharedPreferences.getString("Sleft", "default");


        // Inicializar ConectInsole com o contexto da Activity
        conectar = new ConectInsole(this);
        conectar2 = new ConectInsole2(this);

        // Inicialização das views e outros componentes
        ImageView positioninsole = findViewById(R.id.imageinstructionsinsole);
        Button mTest1 = findViewById(R.id.buttontestinsole1);
        S1 = S2 = S3 = S4 = S5 = S6 = S7 = S8 = S9 = 0x1FFF;

        positioninsole.setImageResource(R.drawable.positioninsole);


        mTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setContentView(R.layout.activity_register7_2);
                instruct = findViewById(R.id.inst_connection);
                Button mNext7Btn = findViewById(R.id.btnNext7);
                mNext7Btn.setVisibility(View.GONE);
                instruct.setText("Permaneça de pé. Aguarde enquanto coletamos alguns dados importantes.");

                sendCommand((byte) 0x3A, freq);
                if (followInRight.equals("true")) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        handleStopCommand((byte) 0x3B, freq);
                    }, 10000); // Delay para permitir a recepção dos dados
                }
                if (followInLeft.equals("true")) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        handleStopCommand2((byte) 0x3B, freq);
                    }, 10000); // Delay para permitir a recepção dos dados
                }

                // checar variável S1, se nulo, reiniciar coleta (break)

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        execute_nextlayout();
                    }
                }, 20000);
            }
        });
    }

    private void execute_nextlayout(){

        Button mNext7Btn = findViewById(R.id.btnNext7);
        gifimagewait = findViewById(R.id.gifimage);
        instruct.setText("Pronto! Podemos prosseguir.");
        mNext7Btn.setVisibility(View.VISIBLE);
        gifimagewait.setVisibility(View.GONE);

        mNext7Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificar = false;
                SharedPreferences sharedPreferences = getSharedPreferences("My_Appcalibrar", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("verificar", verificar);
                startActivity(new Intent(getApplicationContext(), Register6Activity.class));
            }
        });}

    private void sendCommand(byte cmd, byte freq) {
                calendar = Calendar.getInstance();
                byte hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                byte minutes = (byte) calendar.get(Calendar.MINUTE);
                byte seconds = (byte) calendar.get(Calendar.SECOND);
                byte milliseconds = (byte) calendar.get(Calendar.MILLISECOND);
                S1 = S2 = S3 = S4 = S5 = S6 = S7 = S8 = S9 = 0x1FFF;

                if (followInRight.equals("true")) {
                    conectar.createAndSendConfigData(cmd,freq, S1, S2, S3, S4, S5, S6, S7, S8, S9);
                }
                if (followInLeft.equals("true")) {
                    conectar2.createAndSendConfigData(cmd,freq, S1, S2, S3, S4, S5, S6, S7, S8, S9);
                }
            }

    public void handleStopCommand(byte cmd, byte freq) {

                sendCommand(cmd, freq);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    conectar.receiveData(this);
                }, 450); // Delay para permitir a recepção dos dados*/
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    stopService(serviceIntent);
                    processReceivedData(conectar);
                }, 1050); // Delay para permitir o calculo dos dados*/


            }

    public void handleStopCommand2(byte cmd, byte freq) {

                sendCommand(cmd, freq);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    conectar2.receiveData(this);
                }, 450); // Delay para permitir a recepção dos dados*/
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    stopService(serviceIntent);
                    processReceivedData2(conectar2);
                }, 1050); // Delay para permitir o calculo dos dados*/
            }


    private void processReceivedData(@NonNull ConectInsole insole) {
                calendar = Calendar.getInstance();
                byte hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                byte minutes = (byte) calendar.get(Calendar.MINUTE);
                byte seconds = (byte) calendar.get(Calendar.SECOND);
                byte milliseconds = (byte) calendar.get(Calendar.MILLISECOND);


                Boolean foot = false;
                sharedPreferences = getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
                String Sleft = sharedPreferences.getString("Sleft", "default");
                String Sright = sharedPreferences.getString("Sright", "default");
                if (Sright == "true") {
                    foot = false;

                }
                if (Sleft == "true") {
                    foot = true;
                }
                sharedPreferences = getSharedPreferences("My_Appinsolereadings", MODE_PRIVATE);
                String[] sensorKeys = {"S1_1", "S2_1", "S3_1", "S4_1", "S5_1", "S6_1", "S7_1", "S8_1", "S9_1"};
                short[][] sensorReadings = new short[9][];

                for (int i = 0; i < 9; i++) {
                    sensorReadings[i] = stringToShortArray(sharedPreferences.getString(sensorKeys[i], "[0,0,0,0,0]"));
                    System.out.println("Sensor " + sensorKeys[i] + ": " + Arrays.toString(sensorReadings[i]));
                }

                short[] limS = thresholdSensors_steady(sensorReadings, foot);

                byte cmd1 = 0x2A;
                insole.createAndSendConfigData(cmd1,freq, limS[0], limS[1], limS[2], limS[3], limS[4], limS[5], limS[6], limS[7], limS[8]);
                saveConfigData1ToPrefs(limS[0], limS[1], limS[2], limS[3], limS[4], limS[5], limS[6], limS[7], limS[8]);
                SharedPreferences sharedPreferences = getSharedPreferences("Treshold_insole1", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("Lim1I1", limS[0]);
                editor.putInt("Lim2I1", limS[1]);
                editor.putInt("Lim3I1", limS[2]);
                editor.putInt("Lim4I1", limS[3]);
                editor.putInt("Lim5I1", limS[4]);
                editor.putInt("Lim6I1", limS[5]);
                editor.putInt("Lim7I1", limS[6]);
                editor.putInt("Lim8I1", limS[7]);
                editor.putInt("Lim9I1", limS[8]);
                editor.apply();


            }

    private void processReceivedData2(@NonNull ConectInsole2 insole) {
                calendar = Calendar.getInstance();

                Boolean foot = false;
                sharedPreferences = getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
                String Sleft = sharedPreferences.getString("Sleft", "default");
                String Sright = sharedPreferences.getString("Sright", "default");
                if (Sright == "true") {
                    foot = false;

                }
                if (Sleft == "true") {
                    foot = true;
                }

                sharedPreferences = getSharedPreferences("My_Appinsolereadings2", MODE_PRIVATE);
                String[] sensorKeys = {"S1_1", "S2_1", "S3_1", "S4_1", "S5_1", "S6_1", "S7_1", "S8_1", "S9_1"};
                short[][] sensorReadings = new short[9][];

                for (int i = 0; i < 9; i++) {
                    sensorReadings[i] = stringToShortArray(sharedPreferences.getString(sensorKeys[i], "[0,0,0,0,0]"));
                }

                short[] limS = thresholdSensors_steady(sensorReadings, foot);

                byte cmd1 = 0x2A;
                insole.createAndSendConfigData(cmd1,freq, limS[0], limS[1], limS[2], limS[3], limS[4], limS[5], limS[6], limS[7], limS[8]);
                saveConfigData2ToPrefs(limS[0], limS[1], limS[2], limS[3], limS[4], limS[5], limS[6], limS[7], limS[8]);
                SharedPreferences sharedPreferences = getSharedPreferences("Treshold_insole2", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("Lim1I2", limS[0]);
                editor.putInt("Lim2I2", limS[1]);
                editor.putInt("Lim3I2", limS[2]);
                editor.putInt("Lim4I2", limS[3]);
                editor.putInt("Lim5I2", limS[4]);
                editor.putInt("Lim6I2", limS[5]);
                editor.putInt("Lim7I2", limS[6]);
                editor.putInt("Lim8I2", limS[7]);
                editor.putInt("Lim9I2", limS[8]);
                editor.apply();
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
                if (whichfoot == true) {
                    sharedPreferences = getSharedPreferences("My_Appregions", MODE_PRIVATE);
                    String[] regionKeys = {"S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9", "Sn"};
                    Boolean[] regions = new Boolean[10];

                    for (int i = 0; i < 10; i++) {
                        regions[i] = sharedPreferences.getBoolean(regionKeys[i], false);
                        System.out.println("regionsS2" + regions[i]);
                    }

                    for (int i = 0; i < 9; i++) {
                        short hexS = (short) Integer.parseInt(hex[i], 16);
                        limS[i] = regions[i].equals("true") ? (short) ((0x3 << 12) | (hexS & 0xFFF)) : (short) ((0x1 << 12) | (hexS & 0xFFF));
                    }


                } else {
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
    // Função para armazenar os dados de S1 a S9
    private void saveConfigData1ToPrefs(int S1, int S2, int S3, int S4, int S5, int S6, int S7, int S8, int S9) {
        SharedPreferences sharedPreferences = getSharedPreferences("ConfigPrefs1", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Salvando os valores de S1 a S9
        editor.putInt("S1", S1);
        editor.putInt("S2", S2);
        editor.putInt("S3", S3);
        editor.putInt("S4", S4);
        editor.putInt("S5", S5);
        editor.putInt("S6", S6);
        editor.putInt("S7", S7);
        editor.putInt("S8", S8);
        editor.putInt("S9", S9);

        // Aplicando as mudanças
        editor.apply();
    }
    private void saveConfigData2ToPrefs(int S1, int S2, int S3, int S4, int S5, int S6, int S7, int S8, int S9) {
        SharedPreferences sharedPreferences = getSharedPreferences("ConfigPrefs2", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Salvando os valores de S1 a S9
        editor.putInt("S1", S1);
        editor.putInt("S2", S2);
        editor.putInt("S3", S3);
        editor.putInt("S4", S4);
        editor.putInt("S5", S5);
        editor.putInt("S6", S6);
        editor.putInt("S7", S7);
        editor.putInt("S8", S8);
        editor.putInt("S9", S9);

        // Aplicando as mudanças
        editor.apply();
    }

}
