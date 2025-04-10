package com.example.myapplication2;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.Register.Register7Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConectInsole2 {

    private OkHttpClient client;
    private SendData receivedData;
    private SharedPreferences sharedPreferences;
    private String  ipAddressp2s;
    private Register7Activity register7;
    private FirebaseHelper firebasehelper;
    private Calendar calendar;
    String connectedinsole2;
    public static class ConfigData {
        public int cmd;
        public int freq;
        public int S1, S2, S3, S4, S5, S6, S7, S8, S9;
        @Override
        public String toString() {
            return "ConfigData{" +
                    "S1=" + S1 +
                    ", S2=" + S2 +
                    ", S3=" + S3 +
                    ", S4=" + S4 +
                    ", S5=" + S5 +
                    ", S6=" + S6 +
                    ", S7=" + S7 +
                    ", S8=" + S8 +
                    ", S9=" + S9 +
                    '}';
        }
    }

    public static class SendData {
        public int cmd;
        public int hour;
        public int minute;
        public int second;
        public int millisecond;
        public int battery;
        public ArrayList<Integer> SR1 = new ArrayList<>();
        public ArrayList<Integer> SR2 = new ArrayList<>();
        public ArrayList<Integer> SR3 = new ArrayList<>();
        public ArrayList<Integer> SR4 = new ArrayList<>();
        public ArrayList<Integer> SR5 = new ArrayList<>();
        public ArrayList<Integer> SR6 = new ArrayList<>();
        public ArrayList<Integer> SR7 = new ArrayList<>();
        public ArrayList<Integer> SR8 = new ArrayList<>();
        public ArrayList<Integer> SR9 = new ArrayList<>();

        // Novo campo de timestamp
        public long timestamp;

        // Construtor
        public SendData() {
            this.timestamp = System.currentTimeMillis(); // Pega o timestamp atual
        }
    }
    public String getSendDataAsString() {
        // Formata os dados de SendData como uma String
        return "cmd: " + receivedData.cmd + "\n" +
                "Hora: " + receivedData.hour + "\n" +
                "Minuto: " + receivedData.minute + "\n" +
                "Segundo: " + receivedData.second + "\n" +
                "Milissegundo: " + receivedData.millisecond + "\n" +
                "Bateria: " + receivedData.battery + "\n" +
                "SR1: " + receivedData.SR1.toString() + "\n" +
                "SR2: " + receivedData.SR2.toString() + "\n" +
                "SR3: " + receivedData.SR3.toString() + "\n" +
                "SR4: " + receivedData.SR4.toString() + "\n" +
                "SR5: " + receivedData.SR5.toString() + "\n" +
                "SR6: " + receivedData.SR6.toString() + "\n" +
                "SR7: " + receivedData.SR7.toString() + "\n" +
                "SR8: " + receivedData.SR8.toString() + "\n" +
                "SR9: " + receivedData.SR9.toString();
    }

    public ConectInsole2(@NonNull Context context) {
        client = new OkHttpClient();
        receivedData = new SendData();

        firebasehelper = new FirebaseHelper();
        sharedPreferences = context.getSharedPreferences("My_Appips", MODE_PRIVATE);
        ipAddressp2s = sharedPreferences.getString("IP2", "default");
        System.out.println(ipAddressp2s);


    }

    public void createAndSendConfigData(byte kcmd, byte kfreq, short kS1, short kS2, short kS3, short kS4, short kS5, short kS6, short kS7, short kS8, short kS9) {
        ConfigData configData = new ConfigData();
        configData.cmd = kcmd;
        configData.freq = kfreq;
        configData.S1 = kS1;
        configData.S2 = kS2;
        configData.S3 = kS3;
        configData.S4 = kS4;
        configData.S5 = kS5;
        configData.S6 = kS6;
        configData.S7 = kS7;
        configData.S8 = kS8;
        configData.S9 = kS9;

        sendConfigData(configData);
    }

    public void sendConfigData(@NonNull ConfigData configData) {
        StringBuilder data = new StringBuilder();
        data.append(configData.cmd).append(",")
                .append(configData.freq).append(",")
                .append(configData.S1).append(",")
                .append(configData.S2).append(",")
                .append(configData.S3).append(",")
                .append(configData.S4).append(",")
                .append(configData.S5).append(",")
                .append(configData.S6).append(",")
                .append(configData.S7).append(",")
                .append(configData.S8).append(",")
                .append(configData.S9);

        RequestBody body = new FormBody.Builder()
                .add("config_data", data.toString())
                .build();


        String linkp1 = "http://" +  ipAddressp2s + "/config";
        Request request = new Request.Builder()
                .url(linkp1)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Melhorar tratamento de falha
                System.err.println("Falha ao enviar dados de configuração: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("Dados de configuração enviados com sucesso.");
                } else {
                    System.err.println("Falha na resposta: " + response.message());
                }
            }
        });
    }

    public void receiveData(Context context) {
        ConectVibra conectar = new ConectVibra(context);
        String linkp1d = "http://" +  ipAddressp2s + "/data";
        Request request = new Request.Builder()
                .url(linkp1d)
                .build();
        System.out.println(ipAddressp2s);


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Melhorar tratamento de falha
                System.err.println("Falha ao receber dados: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        receivedData.cmd = jsonObject.getInt("cmd");
                        calendar = Calendar.getInstance();
                        receivedData.hour =  calendar.get(Calendar.HOUR_OF_DAY);
                        receivedData.minute =  calendar.get(Calendar.MINUTE);
                        receivedData.second =  calendar.get(Calendar.SECOND);
                        receivedData.millisecond =  calendar.get(Calendar.MILLISECOND);
                        receivedData.battery = (int) jsonObject.getInt("battery");
                        JSONArray sensorsReads = jsonObject.getJSONArray("sensors_reads");

                        // Clear previous data
                        receivedData.SR1.clear();
                        receivedData.SR2.clear();
                        receivedData.SR3.clear();
                        receivedData.SR4.clear();
                        receivedData.SR5.clear();
                        receivedData.SR6.clear();
                        receivedData.SR7.clear();
                        receivedData.SR8.clear();
                        receivedData.SR9.clear();

                        for (int i = 0; i < sensorsReads.length(); i++) {
                            JSONObject sensorRead = sensorsReads.getJSONObject(i);
                            receivedData.SR1.add( sensorRead.getInt("S1"));
                            receivedData.SR2.add( sensorRead.getInt("S2"));
                            receivedData.SR3.add( sensorRead.getInt("S3"));
                            receivedData.SR4.add( sensorRead.getInt("S4"));
                            receivedData.SR5.add( sensorRead.getInt("S5"));
                            receivedData.SR6.add( sensorRead.getInt("S6"));
                            receivedData.SR7.add( sensorRead.getInt("S7"));
                            receivedData.SR8.add( sensorRead.getInt("S8"));
                            receivedData.SR9.add( sensorRead.getInt("S9"));

                        }


                        firebasehelper.saveSendData2(receivedData);
                        System.out.println("Dados recebidos enviados ao Firebase com sucesso.");

                        //Armazenar dados recebidos sensores nas sharedPreferences
                        String receivedS1 = receivedData.SR1.toString();
                        String receivedS2 = receivedData.SR2.toString();
                        String receivedS3 = receivedData.SR3.toString();
                        String receivedS4 = receivedData.SR4.toString();
                        String receivedS5 = receivedData.SR5.toString();
                        String receivedS6 = receivedData.SR6.toString();
                        String receivedS7 = receivedData.SR7.toString();
                        String receivedS8 = receivedData.SR8.toString();
                        String receivedS9 = receivedData.SR9.toString();

                        SharedPreferences sharedPreferences = context.getSharedPreferences("My_Appinsolereadings2", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("S1_2", receivedS1);
                        editor.putString("S2_2", receivedS2);
                        editor.putString("S3_2", receivedS3);
                        editor.putString("S4_2", receivedS4);
                        editor.putString("S5_2", receivedS5);
                        editor.putString("S6_2", receivedS6);
                        editor.putString("S7_2", receivedS7);
                        editor.putString("S8_2", receivedS8);
                        editor.putString("S9_2", receivedS9);
                        editor.apply();


                        // Atualize a interface do usuário usando runOnUiThread ou um Handler
                        System.out.println("Dados recebidos e processados com sucesso.");
                        if (receivedData.cmd==0x3F){
                            System.out.println("Memoria Cheia");
                        }
                        System.out.println(responseData);

                        //verificar cmd para evento, se positivo, enviar alerta ao vibra
                        if (receivedData.cmd == 0X3D) {
                            sharedPreferences = context.getSharedPreferences("My_Appvibra", MODE_PRIVATE);
                            String INT_string = sharedPreferences.getString("int", "default");
                            Byte INT = Byte.valueOf(INT_string);
                            String PEST_string = sharedPreferences.getString("pulse", "default");
                            Byte PEST = Byte.valueOf(PEST_string);
                            String INEST_string = sharedPreferences.getString("interval", "default");
                            Short INEST = Short.valueOf(INEST_string);
                            String TMEST_string = sharedPreferences.getString("time", "default");
                            Short TMEST = Short.valueOf(TMEST_string);
                            Byte cmd = 0x1A;

                            conectar.SendConfigData(cmd, PEST, INT, TMEST, INEST);
                        }
                        if (receivedData.cmd == 0X3E) {
                            connectedinsole2 = "true";
                            SharedPreferences sharedPreferences1 = context.getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
                            editor = sharedPreferences1.edit();
                            editor.putString("connectedinsole2", connectedinsole2);
                            editor.apply();

                        }
                        Utils.checkLoginAndSaveSendData(firebasehelper, receivedData, context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.err.println("Erro ao processar resposta JSON: " + receivedData.SR1);
                    }
                } else {
                    System.err.println("Falha na resposta: " + response.message());
                }
            }
        });
    }

    public void checkForNewData(Context context) {
        String checkUrl = "http://" + ipAddressp2s + "/check";
        Request request = new Request.Builder()
                .url(checkUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Falha ao verificar novos dados: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        boolean newData = jsonObject.getBoolean("newData");

                        if (newData) {
                            receiveData(context);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Erro ao processar resposta JSON: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Falha na resposta de verificação: " + response.message());
                }
            }
        });
    }
    // Supondo que você já tenha uma instância de ConfigData dentro da ConectInsole
    private ConfigData configData;

    public void setConfigData(ConfigData configData) {
        if (configData != null) {
            // Loga o estado atual do objeto interno
            if (this.configData == null) {
                Log.d("ConectInsole", "this.configData is null, creating new instance.");
                this.configData = new ConfigData();
            } else {
                Log.d("ConectInsole", "this.configData exists before substitution: " + this.configData.toString());
            }

            // Loga os novos valores que serão aplicados
            Log.d("ConectInsole", "Substituting new ConfigData: " + configData.toString());

            // Copia os valores do objeto recebido para a instância interna
            this.configData.S1 = configData.S1;
            this.configData.S2 = configData.S2;
            this.configData.S3 = configData.S3;
            this.configData.S4 = configData.S4;
            this.configData.S5 = configData.S5;
            this.configData.S6 = configData.S6;
            this.configData.S7 = configData.S7;
            this.configData.S8 = configData.S8;
            this.configData.S9 = configData.S9;

            // Loga o estado final após a substituição
            Log.d("ConectInsole", "After substitution, this.configData: " + this.configData.toString());
        } else {
            Log.d("ConectInsole", "Received null ConfigData, skipping substitution.");
        }
    }
    public static class Utils {

        // Função que verifica o login e só envia SendData se o usuário estiver logado
        public static void checkLoginAndSaveSendData(FirebaseHelper firebaseHelper, SendData sendData, Context context) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                // Usuário está logado, salvar os dados
                firebaseHelper.saveSendData2(sendData);

                // Verificar se o Contexto é uma Activity antes de mostrar um Toast
                if (context instanceof AppCompatActivity) {
                    // Executar o Toast no thread principal
                    ((AppCompatActivity) context).runOnUiThread(() ->
                            Toast.makeText(context, "SendData enviado com sucesso!", Toast.LENGTH_SHORT).show()
                    );
                }
            } else {
                // Verificar se o Contexto é uma Activity antes de mostrar um Toast
                if (context instanceof AppCompatActivity) {
                    // Executar o Toast no thread principal
                    ((AppCompatActivity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Você precisa fazer login antes de enviar os dados.", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        }
    }
}