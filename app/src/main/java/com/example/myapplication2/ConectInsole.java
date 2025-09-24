package com.example.myapplication2;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Build.VERSION_CODES.R;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConectInsole { // tratamento palmilha direita

    private static final String TAG = "ConectInsole";
    public static final String CHANNEL_ID = "notify_pressure";
    private final ConectVibra conectar;
    private FirebaseAuth fAuth;
    private FirebaseHelper firebasehelper;
    private OkHttpClient client;
    private SendData receivedData;
    private SharedPreferences sharedPreferences;
    private String ipAddressp1s;
    private Calendar calendar;
    private Boolean recebimentoinsole1 = true;
    private Boolean envioinsole1 = true;
    private List<String> eventlist = new ArrayList<>();
    private boolean spikeOnCooldown = false;
    private boolean pendingSpike = false;
    Boolean produzindo = false;
    private final Handler cooldownHandler = new Handler(Looper.getMainLooper());

    public static class ConfigData {
        public int cmd, freq;
        public int S1, S2, S3, S4, S5, S6, S7, S8, S9;

        @Override
        public String toString() {
            return "ConfigData{" +
                    "cmd=" + cmd +
                    ", freq=" + freq +
                    ", S1=" + S1 +
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
        public int cmd, hour, minute, second, millisecond, battery;
        public ArrayList<Integer> SR1 = new ArrayList<>();
        public ArrayList<Integer> SR2 = new ArrayList<>();
        public ArrayList<Integer> SR3 = new ArrayList<>();
        public ArrayList<Integer> SR4 = new ArrayList<>();
        public ArrayList<Integer> SR5 = new ArrayList<>();
        public ArrayList<Integer> SR6 = new ArrayList<>();
        public ArrayList<Integer> SR7 = new ArrayList<>();
        public ArrayList<Integer> SR8 = new ArrayList<>();
        public ArrayList<Integer> SR9 = new ArrayList<>();
    }

    public ConectInsole(@NonNull Context context) {
        Log.d(TAG, "Constructor: initializing ConectInsole");
        client = new OkHttpClient();
        receivedData = new SendData();
        sharedPreferences = context.getSharedPreferences("My_Appips", MODE_PRIVATE);
        ipAddressp1s = sharedPreferences.getString("IP", "default");
        Log.d(TAG, "Loaded IP: " + ipAddressp1s);
        firebasehelper = new FirebaseHelper(context);
        conectar = new ConectVibra(context);
    }

    public String getSendDataAsString() {
        Log.d(TAG, "getSendDataAsString: formatting send data");
        return "cmd: " + receivedData.cmd + "\n" +
                "Hora: " + receivedData.hour + "\n" +
                "Minuto: " + receivedData.minute + "\n" +
                "Segundo: " + receivedData.second + "\n" +
                "Milissegundo: " + receivedData.millisecond + "\n" +
                "Bateria: " + receivedData.battery + "\n" +
                "SR1: " + receivedData.SR1 + "\n" +
                "SR2: " + receivedData.SR2 + "\n" +
                "SR3: " + receivedData.SR3 + "\n" +
                "SR4: " + receivedData.SR4 + "\n" +
                "SR5: " + receivedData.SR5 + "\n" +
                "SR6: " + receivedData.SR6 + "\n" +
                "SR7: " + receivedData.SR7 + "\n" +
                "SR8: " + receivedData.SR8 + "\n" +
                "SR9: " + receivedData.SR9;
    }

    public void createAndSendConfigData(byte kcmd, byte kfreq,
                                        short kS1, short kS2, short kS3,
                                        short kS4, short kS5, short kS6,
                                        short kS7, short kS8, short kS9) {
        Log.d(TAG, String.format("createAndSendConfigData: cmd=0x%02X, freq=%d", kcmd, kfreq));
        ConfigData configData = new ConfigData();
        configData.cmd = kcmd;
        configData.freq = (byte) 5;
        configData.S1 = kS1;
        configData.S2 = kS2;
        configData.S3 = kS3;
        configData.S4 = kS4;
        configData.S5 = kS5;
        configData.S6 = kS6;
        configData.S7 = kS7;
        configData.S8 = kS8;
        configData.S9 = kS9;
        Log.d(TAG, "ConfigData: " + configData);
        sendConfigData(configData);
    }

    public void sendConfigData(@NonNull ConfigData configData) {
        Log.d(TAG, "sendConfigData: building payload");
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
        Log.d(TAG, "Payload: " + data);

        RequestBody body = new FormBody.Builder()
                .add("config_data", data.toString())
                .build();
        String url = "http://" + ipAddressp1s + "/config";
        Log.d(TAG, "POST to " + url);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                envioinsole1 = false;
                Log.e(TAG, "sendConfigData onFailure", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "sendConfigData success");
                    envioinsole1 = true;
                } else Log.e(TAG, "sendConfigData error: " + response.message());
            }
        });
    }

    public void receiveData(Context context) {
        String url = "http://" + ipAddressp1s + "/data";
        Log.d(TAG, "receiveData: GET " + url);
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "receiveData onFailure", e);
                recebimentoinsole1 = false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "receiveData response: " + response.code());
                if (!response.isSuccessful()) {
                    Log.e(TAG, "receiveData error: " + response.message());
                    return;
                }
                try {
                    recebimentoinsole1 = true;
                    String body = response.body().string();
                    Log.d(TAG, "Raw JSON: " + body);
                    JSONObject jsonObject = new JSONObject(body);
                    receivedData.cmd = jsonObject.getInt("cmd");
                    calendar = Calendar.getInstance();
                    receivedData.hour = calendar.get(Calendar.HOUR_OF_DAY);
                    receivedData.minute = calendar.get(Calendar.MINUTE);
                    receivedData.second = calendar.get(Calendar.SECOND);
                    receivedData.millisecond = calendar.get(Calendar.MILLISECOND);
                    receivedData.battery = jsonObject.getInt("battery");

                    JSONArray sensors = jsonObject.getJSONArray("sensors_reads");
                    receivedData.SR1.clear();
                    receivedData.SR2.clear();
                    receivedData.SR3.clear();
                    receivedData.SR4.clear();
                    receivedData.SR5.clear();
                    receivedData.SR6.clear();
                    receivedData.SR7.clear();
                    receivedData.SR8.clear();
                    receivedData.SR9.clear();
                    for (int i = 0; i < sensors.length(); i++) {
                        JSONObject s = sensors.getJSONObject(i);
                        receivedData.SR1.add(s.getInt("S1"));
                        receivedData.SR2.add(s.getInt("S2"));
                        receivedData.SR3.add(s.getInt("S3"));
                        receivedData.SR4.add(s.getInt("S4"));
                        receivedData.SR5.add(s.getInt("S5"));
                        receivedData.SR6.add(s.getInt("S6"));
                        receivedData.SR7.add(s.getInt("S7"));
                        receivedData.SR8.add(s.getInt("S8"));
                        receivedData.SR9.add(s.getInt("S9"));

                        storeReadings(context);


                    }

                    Log.d(TAG, "Parsed SendData: " + getSendDataAsString());

                    if (receivedData.cmd == 0x3F) Log.d(TAG, "Memory full event");
                    if (receivedData.cmd == 0x3D) {
                        produzirpico(context);
                    }

                    Utils.checkLoginAndSaveSendData(firebasehelper, receivedData, context, eventlist);
                } catch (JSONException e) {
                    Log.e(TAG, "receiveData JSON error", e);
                }
            }
        });
    }

    public void checkForNewData(Context context) {
        String url = "http://" + ipAddressp1s + "/check";
        Log.d(TAG, "checkForNewData: GET " + url);
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "checkForNewData onFailure", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "checkForNewData response: " + response.code());
                if (response.isSuccessful()) {
                    try {
                        JSONObject j = new JSONObject(response.body().string());
                        boolean newData = j.getBoolean("newData");
                        Log.d(TAG, "newData flag=" + newData);
                        if (newData) receiveData(context);
                    } catch (JSONException e) {
                        Log.e(TAG, "checkForNewData JSON error", e);
                    }
                } else {
                    Log.e(TAG, "checkForNewData error: " + response.message());
                }
            }
        });
    }
    private ConfigData configData;
    public void setConfigData(ConfigData configData) {
        Log.d(TAG, "setConfigData: called");
        if (configData != null) {
            if (this.configData == null) {
                Log.d(TAG, "Creating new internal ConfigData");
                this.configData = new ConfigData();
            }
            Log.d(TAG, "Substituting: " + configData);
            this.configData = configData;
            Log.d(TAG, "After substitution: " + this.configData);
        }
    }

    public static class Utils {
        public static void checkLoginAndSaveSendData(FirebaseHelper fh, SendData sd, Context ctx, List<String> ev) {
            Log.d(TAG, "Utils.checkLoginAndSaveSendData: start");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                if (NetworkUtils.isNetworkAvailable(ctx)) {
                    fh.saveSendData(sd);
                    showToast(ctx, "SendData enviado com sucesso!");
                } else {
                    String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    fh.saveSendDataLocally(sd, today);
                    showToast(ctx, "Sem conexão. Dados salvos localmente.");
                }
            } else {
                showToast(ctx, "Você precisa fazer login antes de enviar os dados.");
            }
        }

        private static void showToast(Context ctx, String msg) {
            Log.d(TAG, "showToast: " + msg);
            if (ctx instanceof AppCompatActivity) {
                ((AppCompatActivity) ctx).runOnUiThread(() -> Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void storeReadings(Context ctx) {
        Log.d(TAG, "storeReadings: saving to SharedPreferences");
        SharedPreferences sp = ctx.getSharedPreferences("My_Appinsolereadings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("S1_1", receivedData.SR1.toString());
        editor.putString("S2_1", receivedData.SR2.toString());
        editor.putString("S3_1", receivedData.SR3.toString());
        editor.putString("S4_1", receivedData.SR4.toString());
        editor.putString("S5_1", receivedData.SR5.toString());
        editor.putString("S6_1", receivedData.SR6.toString());
        editor.putString("S7_1", receivedData.SR7.toString());
        editor.putString("S8_1", receivedData.SR8.toString());
        editor.putString("S9_1", receivedData.SR9.toString());
        editor.apply();
    }

    public void produzirpico(Context context){

        if (!produzindo){
            Log.d(TAG, "Evento: pico de pressão (cmd=0x3D)");

            // lê parâmetros
            SharedPreferences prefs = context.getSharedPreferences("My_Appvibra", MODE_PRIVATE);
            byte INT    = Byte.parseByte(prefs.getString("int",       "0"));
            byte PEST   = Byte.parseByte(prefs.getString("pulse",     "0"));
            short INEST = Short.parseShort(prefs.getString("interval",  "0"));
            short TMEST = Short.parseShort(prefs.getString("time",      "0"));

                // 1º envio imediato e entra em cooldown
                Log.d(TAG, "Enviando comando de pico (0x1B) — PEST=" + PEST
                        + ", INT=" + INT + ", TMEST=" + TMEST + ", INEST=" + INEST);
                conectar.SendConfigData((byte)0x1B, PEST, INT, TMEST, INEST);


            }
        }
    }
