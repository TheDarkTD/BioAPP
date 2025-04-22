package com.example.myapplication2;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.myapplication2.Register.Register4Activity;
import com.example.myapplication2.Register.Register6Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;
import java.io.IOException;

public class ConectVibra {
    private OkHttpClient client;
    private SendData receivedData;
    private SharedPreferences sharedPreferences;
    private String  ipAddressVs;
    Boolean connectedVibra = false;

    public static class ConfigData {
        public byte cmd;
        public byte PEST, INT;
        public short TMEST, INEST;
        // Variáveis para controle do motor
        //TMEST            Tempo ligado (ms)
        //INEST           Tempo desligado (ms)
        //PEST              Duty cycle (%)
        //INT              Intensidade (%)
    }

    public static class SendData {
        public byte cmd;
        public int battery;

    }

    public ConectVibra(Context context) {
        client = new OkHttpClient();
        receivedData = new SendData();

        // Obter SharedPreferences usando o contexto
        sharedPreferences = context.getSharedPreferences("My_Appips", MODE_PRIVATE);
        ipAddressVs = sharedPreferences.getString("IPv", "default");
        System.out.println(ipAddressVs);
    }

    public void SendConfigData(byte kcmd, byte kPEST, byte kINT, short kTMEST, short kINEST) {
        ConfigData configData = new ConfigData();
        configData.cmd = kcmd;
        configData.PEST = kPEST;
        configData.INT = kINT;
        configData.TMEST = kTMEST;
        configData.INEST = kINEST;

        sendConfigData(configData);
    }

    public void sendConfigData(@NonNull ConfigData configData) {
        StringBuilder data = new StringBuilder();
        data.append(configData.cmd).append(",")
                .append(configData.PEST).append(",")
                .append(configData.INT).append(",")
                .append(configData.TMEST).append(",")
                .append(configData.INEST).append(",");

        RequestBody body = new FormBody.Builder()
                .add("config_data", data.toString())
                .build();

        String linkV = "http://" + ipAddressVs + "/config";
        Request request = new Request.Builder()
                .url(linkV)
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
        String linkVd = "http://" + ipAddressVs + "/data";
        Request request = new Request.Builder()
                .url(linkVd)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.err.println("Falha ao receber dados: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        receivedData.cmd = (byte) jsonObject.getInt("cmd");
                        receivedData.battery = jsonObject.getInt("battery");


                        SharedPreferences sharedPreferences = context.getSharedPreferences("Battery_info", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("batVibra", receivedData.battery);
                        editor.apply();
                        // Atualize a interface do usuário usando runOnUiThread ou um Handler
                        System.out.println("Dados recebidos e processados com sucesso.");
                        System.out.println(responseData);

                        if (receivedData.cmd == 0X1B) {
                            connectedVibra = true;
                            SharedPreferences sharedPreferences1 = context.getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
                            editor = sharedPreferences1.edit();
                            editor.putBoolean("connectedVibra", connectedVibra);
                            editor.apply();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.err.println("Erro ao processar resposta JSON: ");
                    }
                } else {
                    System.err.println("Falha na resposta: " + response.message());
                }
            }
        });
    }
    // Supondo que você já tenha uma instância de ConfigData dentro da ConectInsole
    private ConectInsole.ConfigData configData;

    // Metodo para retornar a ConfigData
    public ConectInsole.ConfigData getConfigData() {
        return configData;
    }
    // Metodo para substituir os valores da ConfigData
    public void setConfigData(ConectInsole.ConfigData configData) {
        if (configData != null) {
            this.configData.cmd = configData.cmd;


        }
    }

}
